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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlin.js.JsName
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun rememberScrollbarState(scrollState: ScrollState): ScrollbarState = remember(scrollState) {
  FoundationScrollStateScrollbarState(scrollState)
}

@Composable
fun rememberScrollbarState(lazyListState: LazyListState): ScrollbarState =
  remember(lazyListState) {
    LazyListStateScrollbarState(lazyListState)
  }

@Composable
fun rememberScrollbarState(lazyGridState: LazyGridState): ScrollbarState =
  remember(lazyGridState) {
    LazyGridStateScrollbarState(lazyGridState)
  }

interface ScrollbarState {

  val scrollOffset: Double

  val contentSize: Double

  val viewportSize: Double

  suspend fun scrollTo(scrollOffset: Double)

  val interactionSource: InteractionSource

  val isScrollInProgress: Boolean
}

val ScrollbarState.maxScrollOffset: Double
  get() = (contentSize - viewportSize).coerceAtLeast(0.0)

internal class FoundationScrollStateScrollbarState(
  private val scrollState: ScrollState,
) : ScrollbarState {

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

internal abstract class LazyLineContentScrollbarState : ScrollbarState {

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
    val distance = scrollOffset - this@LazyLineContentScrollbarState.scrollOffset
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

internal class LazyListStateScrollbarState(
  private val scrollState: LazyListState,
) : LazyLineContentScrollbarState() {

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

internal class LazyGridStateScrollbarState(
  private val scrollState: LazyGridState,
) : LazyLineContentScrollbarState() {

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
