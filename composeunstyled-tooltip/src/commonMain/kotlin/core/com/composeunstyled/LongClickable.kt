/*
 * Copyright (c) 2026 Composable Horizons
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
@file:Suppress("ktlint:standard:max-line-length")

package core.com.composeunstyled

import androidx.compose.foundation.gestures.GestureCancellationException
import androidx.compose.foundation.gestures.PressGestureScope
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.isOutOfBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Density
import androidx.compose.ui.util.fastAll
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

@Composable
internal fun Modifier.interceptingLongClickable(onLongPress: () -> Unit): Modifier {
  val hapticFeedback = LocalHapticFeedback.current

  return this then Modifier.pointerInput(Unit) {
    detectTapGestures(
      pass = PointerEventPass.Initial,
      onLongPress = {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        onLongPress()
      },
    )
  }
}

private val NoPressGesture: suspend PressGestureScope.(Offset) -> Unit = {}

internal sealed class LongPressResult {
  object Success : LongPressResult()

  class Released(val finalUpChange: PointerInputChange) : LongPressResult()

  object Canceled : LongPressResult()
}

internal suspend fun AwaitPointerEventScope.waitForLongPress(
  pass: PointerEventPass = PointerEventPass.Main,
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
    var resetJob = launch(start = CoroutineStart.UNDISPATCHED) { pressScope.reset() }
    val upOrCancel: PointerInputChange?

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

  fun cancel() {
    isCanceled = true
    if (mutex.isLocked) {
      mutex.unlock()
    }
  }

  fun release() {
    isReleased = true
    if (mutex.isLocked) {
      mutex.unlock()
    }
  }

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

private fun CoroutineScope.launchAwaitingReset(
  resetJob: Job,
  start: CoroutineStart = CoroutineStart.UNDISPATCHED,
  block: suspend CoroutineScope.() -> Unit,
): Job =
  launch(start = start) {
    resetJob.join()
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
  firstUp: PointerInputChange,
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
