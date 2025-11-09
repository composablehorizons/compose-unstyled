/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package core.com.composeunstyled

import androidx.compose.foundation.ComposeFoundationFlags.isDetectTapGesturesImmediateCoroutineDispatchEnabled
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.GestureCancellationException
import androidx.compose.foundation.gestures.PressGestureScope
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Density
import androidx.compose.ui.util.fastAll
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex

/**
 * A modified version of [clickable] that allow the composable to handle long clicks before its children.
 */
@Composable
internal fun Modifier.interceptingLongClickable(onLongPress: () -> Unit): Modifier {
    val hapticFeedback = LocalHapticFeedback.current

    return this then Modifier.pointerInput(Unit) {
        detectTapGestures(
            pass = PointerEventPass.Initial,
            onLongPress = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                onLongPress()
            }
        )
    }
}

private val NoPressGesture: suspend PressGestureScope.(Offset) -> Unit = {}

@OptIn(ExperimentalFoundationApi::class)
private val coroutineStartForCurrentDispatchBehavior
    get() =
        if (isDetectTapGesturesImmediateCoroutineDispatchEnabled) {
            CoroutineStart.UNDISPATCHED
        } else {
            CoroutineStart.DEFAULT
        }

internal sealed class LongPressResult {
    /** Long press was triggered */
    object Success : LongPressResult()

    /** All pointers were released without long press being triggered */
    class Released(val finalUpChange: PointerInputChange) : LongPressResult()

    /** The gesture was canceled */
    object Canceled : LongPressResult()
}

internal suspend fun AwaitPointerEventScope.waitForLongPress(
    pass: PointerEventPass = PointerEventPass.Main
): LongPressResult {
    var result: LongPressResult = LongPressResult.Canceled
    try {
        withTimeout(viewConfiguration.longPressTimeoutMillis) {
            while (true) {
                val event = awaitPointerEvent(pass)
                if (event.changes.fastAll { it.changedToUp() }) {
                    // All pointers are up
                    result = LongPressResult.Released(event.changes[0])
                    break
                }

                if (event.isDeepPress) {
                    result = LongPressResult.Success
                    break
                }

                if (
                    event.changes.fastAny {
                        it.isConsumed || it.isOutOfBounds(size, extendedTouchPadding)
                    }
                ) {
                    result = LongPressResult.Canceled
                    break
                }

                // Check for cancel by position consumption. We can look on the Final pass of the
                // existing pointer event because it comes after the pass we checked above.
                val consumeCheck = awaitPointerEvent(PointerEventPass.Final)
                if (consumeCheck.changes.fastAny { it.isConsumed }) {
                    result = LongPressResult.Canceled
                    break
                }
            }
        }
    } catch (_: PointerEventTimeoutCancellationException) {
        return LongPressResult.Success
    }
    return result
}


suspend fun PointerInputScope.detectTapGestures(
    pass: PointerEventPass = PointerEventPass.Main,
    onLongPress: ((Offset) -> Unit),
) = coroutineScope {
    // special signal to indicate to the sending side that it shouldn't intercept and consume
    // cancel/up events as we're only require down events
    val pressScope = PressGestureScopeImpl(this@detectTapGestures)
    awaitEachGesture {
        val down = awaitFirstDown(pass = pass)
        if (down.type != PointerType.Touch && down.type != PointerType.Stylus) {
            return@awaitEachGesture
        }
        var resetJob = launch(start = coroutineStartForCurrentDispatchBehavior) { pressScope.reset() }
        val upOrCancel: PointerInputChange?
        val cancelOrReleaseJob: Job?

        // wait for first tap up or long press
        upOrCancel =
            when (val longPressResult = waitForLongPress(pass = pass)) {
                LongPressResult.Success -> {
                    onLongPress.invoke(down.position)
                    consumeUntilUp()
                    launchAwaitingReset(resetJob) { pressScope.release() }
                    // End the current gesture
                    return@awaitEachGesture
                }

                is LongPressResult.Released -> {
                    longPressResult.finalUpChange
                }

                is LongPressResult.Canceled -> {
                    null
                }
            }
    }
}


internal class PressGestureScopeImpl(density: Density) : PressGestureScope, Density by density {
    private var isReleased = false
    private var isCanceled = false
    private val mutex = Mutex(locked = false)

    /** Called when a gesture has been canceled. */
    fun cancel() {
        isCanceled = true
        if (mutex.isLocked) {
            mutex.unlock()
        }
    }

    /** Called when all pointers are up. */
    fun release() {
        isReleased = true
        if (mutex.isLocked) {
            mutex.unlock()
        }
    }

    /** Called when a new gesture has started. */
    suspend fun reset() {
        mutex.lock()
        isReleased = false
        isCanceled = false
    }

    override suspend fun awaitRelease() {
        if (!tryAwaitRelease()) {
            throw GestureCancellationException("The press gesture was canceled.")
        }
    }

    override suspend fun tryAwaitRelease(): Boolean {
        if (!isReleased && !isCanceled) {
            mutex.lock()
            mutex.unlock()
        }
        return isReleased
    }
}


@OptIn(ExperimentalFoundationApi::class)
private fun CoroutineScope.launchAwaitingReset(
    resetJob: Job,
    start: CoroutineStart = coroutineStartForCurrentDispatchBehavior,
    block: suspend CoroutineScope.() -> Unit,
): Job =
    launch(start = start) {
        if (isDetectTapGesturesImmediateCoroutineDispatchEnabled) {
            resetJob.join()
        }
        block()
    }


private suspend fun AwaitPointerEventScope.consumeUntilUp() {
    do {
        val event = awaitPointerEvent()
        event.changes.fastForEach { it.consume() }
    } while (event.changes.fastAny { it.pressed })
}


internal expect val PointerEvent.isDeepPress: Boolean

private suspend fun AwaitPointerEventScope.awaitSecondDown(
    firstUp: PointerInputChange
): PointerInputChange? =
    withTimeoutOrNull(viewConfiguration.doubleTapTimeoutMillis) {
        val minUptime = firstUp.uptimeMillis + viewConfiguration.doubleTapMinTimeMillis
        var change: PointerInputChange
        // The second tap doesn't count if it happens before DoubleTapMinTime of the first tap
        do {
            change = awaitFirstDown()
        } while (change.uptimeMillis < minUptime)
        change
    }
