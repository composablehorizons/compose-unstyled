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
package com.composeunstyled.visualregressions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composeunstyled.Modal
import com.composeunstyled.Sheet
import com.composeunstyled.SheetDetent
import com.composeunstyled.SheetDetent.Companion.Hidden
import com.composeunstyled.UnstyledBottomSheet
import com.composeunstyled.rememberBottomSheetState
import com.composeunstyled.rememberModalState

val ModalBottomSheetRegressionScreenshots = listOf(
  VisualRegressionScreenshot(
    name = "modal-bottom-sheet-expanded-fixed-height",
    content = { ModalBottomSheetExpandedFixedHeight() },
  ),
  VisualRegressionScreenshot(
    name = "modal-bottom-sheet-peek-fixed-height",
    content = { ModalBottomSheetPeekFixedHeight() },
  ),
  VisualRegressionScreenshot(
    name = "modal-bottom-sheet-expanded-wrap-content",
    content = { ModalBottomSheetExpandedWrapContent() },
  ),
  VisualRegressionScreenshot(
    name = "modal-bottom-sheet-peek-wrap-content",
    content = { ModalBottomSheetPeekWrapContent() },
  ),
  VisualRegressionScreenshot(
    name = "modal-bottom-sheet-expanded-lazy-column-wrap-content",
    content = { ModalBottomSheetExpandedLazyColumnWrapContent() },
  ),
  VisualRegressionScreenshot(
    name = "modal-bottom-sheet-peek-lazy-column-wrap-content",
    content = { ModalBottomSheetPeekLazyColumnWrapContent() },
  ),
  VisualRegressionScreenshot(
    name = "modal-bottom-sheet-expanded-vertical-scroll-wrap-content",
    content = { ModalBottomSheetExpandedVerticalScrollWrapContent() },
  ),
  VisualRegressionScreenshot(
    name = "modal-bottom-sheet-peek-vertical-scroll-wrap-content",
    content = { ModalBottomSheetPeekVerticalScrollWrapContent() },
  ),
  VisualRegressionScreenshot(
    name = "modal-bottom-sheet-expanded-scrollable-content-final-row",
    content = { ModalBottomSheetExpandedScrollableContentFinalRow() },
  ),
  VisualRegressionScreenshot(
    name = "modal-bottom-sheet-expanded-fixed-height-top-padding",
    content = { ModalBottomSheetExpandedFixedHeightWithTopPadding() },
  ),
  VisualRegressionScreenshot(
    name = "modal-bottom-sheet-fixed-width-landscape",
    height = 480,
    content = { ModalBottomSheetFixedWidthLandscape() },
  ),
)

@Composable
fun ModalBottomSheetExpandedFixedHeight() {
  ModalBottomSheetScaffold(initialDetent = ExpandedDetent) {
    ModalBottomSheetFixedHeightContent()
  }
}

@Composable
fun ModalBottomSheetPeekFixedHeight() {
  ModalBottomSheetScaffold(initialDetent = PeekDetent) {
    ModalBottomSheetFixedHeightContent()
  }
}

@Composable
fun ModalBottomSheetExpandedWrapContent() {
  ModalBottomSheetScaffold(initialDetent = ExpandedDetent) {
    ModalBottomSheetWrapContent()
  }
}

@Composable
fun ModalBottomSheetPeekWrapContent() {
  ModalBottomSheetScaffold(initialDetent = PeekDetent) {
    ModalBottomSheetWrapContent()
  }
}

@Composable
fun ModalBottomSheetExpandedLazyColumnWrapContent() {
  ModalBottomSheetScaffold(initialDetent = ExpandedDetent) {
    ModalBottomSheetLazyColumnWrapContent()
  }
}

@Composable
fun ModalBottomSheetPeekLazyColumnWrapContent() {
  ModalBottomSheetScaffold(initialDetent = PeekDetent) {
    ModalBottomSheetLazyColumnWrapContent()
  }
}

@Composable
fun ModalBottomSheetExpandedVerticalScrollWrapContent() {
  ModalBottomSheetScaffold(initialDetent = ExpandedDetent) {
    ModalBottomSheetVerticalScrollWrapContent()
  }
}

@Composable
fun ModalBottomSheetPeekVerticalScrollWrapContent() {
  ModalBottomSheetScaffold(initialDetent = PeekDetent) {
    ModalBottomSheetVerticalScrollWrapContent()
  }
}

@Composable
fun ModalBottomSheetExpandedScrollableContentFinalRow() {
  ModalBottomSheetScaffold(initialDetent = ExpandedDetent) {
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .height(180.dp),
    ) {
      items(2) {
        ModalBottomSheetRow()
      }
      item {
        ModalBottomSheetRow {
          BasicText(
            text = "This row must not be clipped",
            style = TextStyle(fontWeight = FontWeight.Medium),
          )
        }
      }
    }
  }
}

@Composable
fun ModalBottomSheetExpandedFixedHeightWithTopPadding() {
  ModalBottomSheetScaffold(
    initialDetent = ExpandedDetent,
    sheetModifier = Modifier.padding(top = 64.dp),
  ) {
    ModalBottomSheetFinalRowContent()
  }
}

@Composable
fun ModalBottomSheetFixedWidthLandscape() {
  ModalBottomSheetScaffold(
    initialDetent = ExpandedDetent,
    screenshotHeight = 480.dp,
    sheetSizeModifier = Modifier.width(520.dp),
  ) {
    ModalBottomSheetFixedHeightContent()
  }
}

@Composable
private fun ModalBottomSheetScaffold(
  initialDetent: SheetDetent,
  screenshotWidth: Dp = 1024.dp,
  screenshotHeight: Dp = 600.dp,
  sheetModifier: Modifier = Modifier,
  sheetSizeModifier: Modifier = Modifier.widthIn(max = 640.dp).fillMaxWidth(),
  content: @Composable () -> Unit,
) {
  val modalState = rememberModalState(initiallyVisible = true)
  val sheetState = rememberBottomSheetState(
    initialDetent = initialDetent,
    detents = listOf(Hidden, PeekDetent, ExpandedDetent),
  )

  Modal(state = modalState) {
    Box(
      modifier = Modifier
        .requiredSize(width = screenshotWidth, height = screenshotHeight)
        .background(Color.Transparent),
    ) {
      UnstyledBottomSheet(
        state = sheetState,
        modifier = Modifier.fillMaxSize(),
      ) {
        Box(
          modifier = Modifier.fillMaxWidth(),
          contentAlignment = Alignment.TopCenter,
        ) {
          Sheet(
            modifier = sheetModifier
              .then(sheetSizeModifier)
              .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
              .background(Color.White)
              .border(1.dp, Color(0xFFFFD60A), RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)),
          ) {
            content()
          }
        }
      }
    }
  }
}

@Composable
private fun ModalBottomSheetFixedHeightContent() {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(300.dp)
      .background(Color(0xFFF3F4F6))
      .border(1.dp, Color(0xFF111827)),
  )
}

@Composable
private fun ModalBottomSheetFinalRowContent() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .height(180.dp),
  ) {
    repeat(2) {
      ModalBottomSheetRow()
    }
    ModalBottomSheetRow {
      BasicText(
        text = "This row must not be clipped",
        style = TextStyle(fontWeight = FontWeight.Medium),
      )
    }
  }
}

@Composable
private fun ModalBottomSheetWrapContent() {
  Column(
    modifier = Modifier.fillMaxWidth(),
  ) {
    repeat(3) {
      ModalBottomSheetRow()
    }
  }
}

@Composable
private fun ModalBottomSheetLazyColumnWrapContent() {
  LazyColumn(
    modifier = Modifier.fillMaxWidth(),
  ) {
    items(3) {
      ModalBottomSheetRow()
    }
  }
}

@Composable
private fun ModalBottomSheetVerticalScrollWrapContent() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
  ) {
    repeat(3) {
      ModalBottomSheetRow()
    }
  }
}

@Composable
private fun ModalBottomSheetRow(
  content: @Composable BoxScope.() -> Unit = {},
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(60.dp)
      .background(Color(0xFFF3F4F6))
      .border(1.dp, Color(0xFF111827)),
    contentAlignment = Alignment.Center,
    content = content,
  )
}

private val PeekDetent = SheetDetent("peek") { _, _ -> 180.dp }
private val ExpandedDetent = SheetDetent("expanded") { _, _ -> 300.dp }
