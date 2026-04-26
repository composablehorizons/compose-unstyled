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

package com.composeunstyled

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.js.JsName
import kotlin.jvm.JvmInline
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun rememberScrollAreaState(scrollState: ScrollState): ScrollAreaState = remember(scrollState) {
  ScrollStateScrollAreaState(scrollState)
}

@Composable
fun rememberScrollAreaState(lazyListState: LazyListState): ScrollAreaState =
  remember(lazyListState) {
    LazyListScrollAreaState(lazyListState)
  }

@Composable
fun rememberScrollAreaState(lazyGridState: LazyGridState): ScrollAreaState =
  remember(lazyGridState) {
    LazyGridScrollAreaScrollAreaState(lazyGridState)
  }

@Composable
fun ScrollArea(
  state: ScrollAreaState,
  modifier: Modifier = Modifier,
  content: @Composable ScrollAreaScope.() -> Unit,
) {
  Box(modifier) {
    val boxScope = this
    val scrollAreaScope = remember {
      ScrollAreaScope(boxScope, state)
    }
    scrollAreaScope.content()
  }
}

@JvmInline
@Immutable
value class OverscrollSides private constructor(private val id: Int) {
  companion object {
    val Top = OverscrollSides(0)
    val Bottom = OverscrollSides(1)
    val Left = OverscrollSides(2)
    val Right = OverscrollSides(3)
    val Vertical = OverscrollSides(4)
    val Horizontal = OverscrollSides(5)
  }
}

class ScrollAreaScope internal constructor(
  private val boxScope: BoxScope,
  internal val scrollAreaState: ScrollAreaState,
) {
  fun Modifier.align(alignment: Alignment): Modifier {
    return with(boxScope) {
      align(alignment)
    }
  }
}

interface ScrollAreaState {

  val scrollOffset: Double

  val contentSize: Double

  val viewportSize: Double

  suspend fun scrollTo(scrollOffset: Double)

  val interactionSource: InteractionSource

  val isScrollInProgress: Boolean
}

val ScrollAreaState.maxScrollOffset: Double
  get() = (contentSize - viewportSize).coerceAtLeast(0.0)

internal class ScrollStateScrollAreaState(
  private val scrollState: ScrollState,
) : ScrollAreaState {

  override val isScrollInProgress: Boolean
    get() = scrollState.isScrollInProgress

  override val interactionSource: InteractionSource
    get() = scrollState.interactionSource

  override val scrollOffset: Double get() = scrollState.value.toDouble()

  override suspend fun scrollTo(scrollOffset: Double) {
    scrollState.scrollTo(scrollOffset.roundToInt())
  }

  override val contentSize: Double
    get() = scrollState.maxValue + viewportSize

  override val viewportSize: Double
    get() = scrollState.viewportSize.toDouble()
}

internal abstract class LazyLineContentScrollAreaState : ScrollAreaState {

  class VisibleLine(
    val index: Int,
    val offset: Int,
  )

  protected abstract fun firstVisibleLine(): VisibleLine?

  protected abstract fun totalLineCount(): Int

  protected abstract fun contentPadding(): Int

  protected abstract suspend fun snapToLine(lineIndex: Int, scrollOffset: Int)

  protected abstract suspend fun scrollBy(value: Float)

  protected abstract fun averageVisibleLineSize(): Double

  protected abstract val lineSpacing: Int

  @JsName("averageVisibleLineSizeProperty")
  private val averageVisibleLineSize by derivedStateOf {
    if (totalLineCount() == 0) {
      0.0
    } else {
      averageVisibleLineSize()
    }
  }

  private val averageVisibleLineSizeWithSpacing get() = averageVisibleLineSize + lineSpacing

  override val scrollOffset: Double
    get() {
      val firstVisibleLine = firstVisibleLine()
      return if (firstVisibleLine == null) {
        0.0
      } else {
        val index = firstVisibleLine.index
        val offset = firstVisibleLine.offset

        index * averageVisibleLineSizeWithSpacing - offset
      }
    }

  override val contentSize: Double
    get() {
      val totalLineCount = totalLineCount()
      return averageVisibleLineSize * totalLineCount + lineSpacing * (totalLineCount - 1).coerceAtLeast(
        0,
      ) + contentPadding()
    }

  override suspend fun scrollTo(scrollOffset: Double) {
    val distance = scrollOffset - this@LazyLineContentScrollAreaState.scrollOffset
    if (abs(distance) <= viewportSize) {
      scrollBy(distance.toFloat())
    } else {
      snapTo(scrollOffset)
    }
  }

  private suspend fun snapTo(scrollOffset: Double) {
    val scrollOffsetCoerced = scrollOffset.coerceIn(0.0, maxScrollOffset)

    val index = (scrollOffsetCoerced / averageVisibleLineSizeWithSpacing).toInt().coerceAtLeast(0)
      .coerceAtMost(totalLineCount() - 1)

    val offset =
      (scrollOffsetCoerced - index * averageVisibleLineSizeWithSpacing).toInt().coerceAtLeast(0)

    snapToLine(lineIndex = index, scrollOffset = offset)
  }
}

internal class LazyListScrollAreaState(
  private val scrollState: LazyListState,
) : LazyLineContentScrollAreaState() {

  override val interactionSource: InteractionSource
    get() = scrollState.interactionSource

  override val isScrollInProgress: Boolean
    get() = scrollState.isScrollInProgress

  override val viewportSize: Double
    get() = with(scrollState.layoutInfo) {
      if (orientation == Orientation.Vertical) {
        viewportSize.height
      } else {
        viewportSize.width
      }
    }.toDouble()

  private fun firstFloatingVisibleItemIndex() = with(scrollState.layoutInfo.visibleItemsInfo) {
    when (size) {
      0 -> null
      1 -> 0
      else -> {
        val first = this[0]
        val second = this[1]
        if ((first.index < second.index - 1) || (first.offset + first.size + lineSpacing > second.offset)) {
          1
        } else {
          0
        }
      }
    }
  }

  override fun firstVisibleLine(): VisibleLine? {
    val firstFloatingVisibleIndex = firstFloatingVisibleItemIndex() ?: return null
    val firstFloatingItem = scrollState.layoutInfo.visibleItemsInfo[firstFloatingVisibleIndex]
    return VisibleLine(
      index = firstFloatingItem.index,
      offset = firstFloatingItem.offset,
    )
  }

  override fun totalLineCount() = scrollState.layoutInfo.totalItemsCount

  override fun contentPadding() = with(scrollState.layoutInfo) {
    beforeContentPadding + afterContentPadding
  }

  override suspend fun snapToLine(lineIndex: Int, scrollOffset: Int) {
    scrollState.scrollToItem(lineIndex, scrollOffset)
  }

  override suspend fun scrollBy(value: Float) {
    scrollState.scrollBy(value)
  }

  override fun averageVisibleLineSize() = with(scrollState.layoutInfo.visibleItemsInfo) {
    val firstFloatingIndex = firstFloatingVisibleItemIndex() ?: return@with 0.0
    val first = this[firstFloatingIndex]
    val last = last()
    val count = size - firstFloatingIndex
    (last.offset + last.size - first.offset - (count - 1) * lineSpacing).toDouble() / count
  }

  override val lineSpacing get() = scrollState.layoutInfo.mainAxisItemSpacing
}

internal class LazyGridScrollAreaScrollAreaState(
  private val scrollState: LazyGridState,
) : LazyLineContentScrollAreaState() {

  override val isScrollInProgress: Boolean
    get() = scrollState.isScrollInProgress

  override val interactionSource: InteractionSource
    get() = scrollState.interactionSource

  override val viewportSize: Double
    get() = with(scrollState.layoutInfo) {
      if (orientation == Orientation.Vertical) {
        viewportSize.height
      } else {
        viewportSize.width
      }
    }.toDouble()

  private val isVertical: Boolean
    get() = scrollState.layoutInfo.orientation == Orientation.Vertical

  private val unknownLine: Int
    get() = with(LazyGridItemInfo) {
      if (isVertical) UnknownRow else UnknownColumn
    }

  private fun LazyGridItemInfo.line() = if (isVertical) row else column

  private fun LazyGridItemInfo.mainAxisSize() = with(size) {
    if (isVertical) height else width
  }

  private fun LazyGridItemInfo.mainAxisOffset() = with(offset) {
    if (isVertical) y else x
  }

  private val slotsPerLine by derivedStateOf {
    val orientation = scrollState.layoutInfo.orientation
    scrollState.layoutInfo.visibleItemsInfo.distinctBy {
      if (orientation == Orientation.Vertical) it.column else it.row
    }
      .count()
  }

  private fun lineOfIndex(index: Int): Int = index / slotsPerLine.coerceAtLeast(1)

  private fun indexOfFirstInLine(line: Int): Int = line * slotsPerLine

  override fun firstVisibleLine(): VisibleLine? {
    return scrollState.layoutInfo.visibleItemsInfo.firstOrNull { it.line() != unknownLine }
      ?.let { firstVisibleItem ->
        VisibleLine(
          index = firstVisibleItem.line(),
          offset = firstVisibleItem.mainAxisOffset(),
        )
      }
  }

  override fun totalLineCount(): Int {
    val itemCount = scrollState.layoutInfo.totalItemsCount
    return if (itemCount == 0) {
      0
    } else {
      lineOfIndex(itemCount - 1) + 1
    }
  }

  override fun contentPadding() = with(scrollState.layoutInfo) {
    beforeContentPadding + afterContentPadding
  }

  override suspend fun snapToLine(lineIndex: Int, scrollOffset: Int) {
    scrollState.scrollToItem(
      index = indexOfFirstInLine(lineIndex),
      scrollOffset = scrollOffset,
    )
  }

  override suspend fun scrollBy(value: Float) {
    scrollState.scrollBy(value)
  }

  override fun averageVisibleLineSize(): Double {
    val visibleItemsInfo = scrollState.layoutInfo.visibleItemsInfo
    val indexOfFirstKnownLineItem = visibleItemsInfo.indexOfFirst { it.line() != unknownLine }
    if (indexOfFirstKnownLineItem == -1) return 0.0
    val reallyVisibleItemsInfo =
      visibleItemsInfo.subList(indexOfFirstKnownLineItem, visibleItemsInfo.size)
    val lastLine = reallyVisibleItemsInfo.last().line()
    val lastLineSize =
      reallyVisibleItemsInfo.asReversed().asSequence().takeWhile { it.line() == lastLine }
        .maxOf { it.mainAxisSize() }

    val first = reallyVisibleItemsInfo.first()
    val last = reallyVisibleItemsInfo.last()
    val lineCount = last.line() - first.line() + 1
    val lineSpacingSum = (lineCount - 1) * lineSpacing
    return (last.mainAxisOffset() + lastLineSize - first.mainAxisOffset() - lineSpacingSum).toDouble() / lineCount
  }

  override val lineSpacing get() = scrollState.layoutInfo.mainAxisItemSpacing
}

internal class SliderAdapter internal constructor(
  val adapter: ScrollAreaState,
  private val trackSize: Int,
  private val minHeight: Float,
  private val reverseLayout: Boolean,
  private val isVertical: Boolean,
  private val coroutineScope: CoroutineScope,
) {
  private val contentSize get() = adapter.contentSize
  private val visiblePart: Double
    get() {
      val contentSize = contentSize
      return if (contentSize == 0.0) {
        1.0
      } else {
        (adapter.viewportSize / contentSize).coerceAtMost(1.0)
      }
    }

  val thumbSize
    get() = (trackSize * visiblePart).coerceAtLeast(minHeight.toDouble())

  private val scrollScale: Double
    get() {
      val extraScrollbarSpace = trackSize - thumbSize
      val extraContentSpace = adapter.maxScrollOffset
      return if (extraContentSpace == 0.0) 1.0 else extraScrollbarSpace / extraContentSpace
    }

  private val rawPosition: Double
    get() = scrollScale * adapter.scrollOffset

  val position: Double
    get() = if (reverseLayout) trackSize - thumbSize - rawPosition else rawPosition

  val bounds get() = position..position + thumbSize
  private var unscrolledDragDistance = 0.0

  fun onDragStarted() {
    unscrolledDragDistance = 0.0
  }

  private suspend fun setPosition(value: Double) {
    val rawPosition = if (reverseLayout) {
      trackSize - thumbSize - value
    } else {
      value
    }
    adapter.scrollTo(rawPosition / scrollScale)
  }

  private val dragMutex = Mutex()

  fun onDragDelta(offset: Offset) {
    coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
      dragMutex.withLock {
        val dragDelta = if (isVertical) offset.y else offset.x
        val maxScrollPosition = adapter.maxScrollOffset * scrollScale
        val currentPosition = position
        val targetPosition = (currentPosition + dragDelta + unscrolledDragDistance).coerceIn(
          0.0,
          maxScrollPosition,
        )
        val sliderDelta = targetPosition - currentPosition
        val newPos = position + sliderDelta
        setPosition(newPos)
        unscrolledDragDistance += dragDelta - sliderDelta
      }
    }
  }
}
