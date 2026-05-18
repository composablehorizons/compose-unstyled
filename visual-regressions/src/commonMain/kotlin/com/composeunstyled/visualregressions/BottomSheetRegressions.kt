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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import com.composeunstyled.Sheet
import com.composeunstyled.SheetDetent
import com.composeunstyled.SheetDetent.Companion.FullyExpanded
import com.composeunstyled.UnstyledBottomSheet
import com.composeunstyled.rememberBottomSheetState

@Composable
fun BottomSheetExpandedFixedHeightRegression() {
  BottomSheetRegressionScaffold(initialDetent = FullyExpanded) {
    BottomSheetFixedHeightContent()
  }
}

@Composable
fun BottomSheetPeekFixedHeightRegression() {
  BottomSheetRegressionScaffold(initialDetent = PeekDetent) {
    BottomSheetFixedHeightContent()
  }
}

@Composable
fun BottomSheetExpandedWrapContentRegression() {
  BottomSheetRegressionScaffold(initialDetent = FullyExpanded) {
    BottomSheetWrapContent()
  }
}

@Composable
fun BottomSheetPeekWrapContentRegression() {
  BottomSheetRegressionScaffold(initialDetent = PeekDetent) {
    BottomSheetWrapContent()
  }
}

@Composable
fun BottomSheetExpandedLazyColumnWrapContentRegression() {
  BottomSheetRegressionScaffold(initialDetent = FullyExpanded) {
    BottomSheetLazyColumnWrapContent()
  }
}

@Composable
fun BottomSheetPeekLazyColumnWrapContentRegression() {
  BottomSheetRegressionScaffold(initialDetent = PeekDetent) {
    BottomSheetLazyColumnWrapContent()
  }
}

@Composable
fun BottomSheetExpandedVerticalScrollWrapContentRegression() {
  BottomSheetRegressionScaffold(initialDetent = FullyExpanded) {
    BottomSheetVerticalScrollWrapContent()
  }
}

@Composable
fun BottomSheetPeekVerticalScrollWrapContentRegression() {
  BottomSheetRegressionScaffold(initialDetent = PeekDetent) {
    BottomSheetVerticalScrollWrapContent()
  }
}

@Composable
fun BottomSheetExpandedScrollableContentFinalRowRegression() {
  BottomSheetRegressionScaffold(initialDetent = FullyExpanded) {
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .height(260.dp),
      contentPadding = PaddingValues(start = 24.dp, top = 48.dp, end = 24.dp, bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      items(2) {
        BottomSheetRow()
      }
      item {
        BottomSheetRow {
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
fun BottomSheetFixedWidthLandscapeRegression() {
  BottomSheetRegressionScaffold(
    initialDetent = FullyExpanded,
    sheetSizeModifier = Modifier.width(520.dp),
  ) {
    BottomSheetFixedHeightContent()
  }
}

@Composable
private fun BottomSheetRegressionScaffold(
  initialDetent: SheetDetent,
  sheetSizeModifier: Modifier = Modifier.widthIn(max = 640.dp).fillMaxWidth(),
  content: @Composable () -> Unit,
) {
  val sheetState = rememberBottomSheetState(
    initialDetent = initialDetent,
    detents = listOf(PeekDetent, FullyExpanded),
  )

  UnstyledBottomSheet(
    state = sheetState,
    modifier = Modifier.fillMaxSize(),
  ) {
    Box(
      modifier = Modifier.fillMaxWidth(),
      contentAlignment = Alignment.TopCenter,
    ) {
      Sheet(
        modifier = sheetSizeModifier
          .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
          .background(Color(0xFFF8FAFC))
          .border(1.dp, Color(0xFFCACACA), RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
      ) {
        Box(Modifier.fillMaxWidth()) {
          content()
          BottomSheetDragHandle()
        }
      }
    }
  }
}

@Composable
private fun BottomSheetFixedHeightContent() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .height(320.dp)
      .padding(start = 24.dp, top = 48.dp, end = 24.dp, bottom = 24.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    repeat(4) {
      BottomSheetRow()
    }
  }
}

@Composable
private fun BottomSheetWrapContent() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 24.dp, top = 48.dp, end = 24.dp, bottom = 24.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    repeat(3) {
      BottomSheetRow()
    }
  }
}

@Composable
private fun BottomSheetLazyColumnWrapContent() {
  LazyColumn(
    modifier = Modifier.fillMaxWidth(),
    contentPadding = PaddingValues(start = 24.dp, top = 48.dp, end = 24.dp, bottom = 24.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    items(3) {
      BottomSheetRow()
    }
  }
}

@Composable
private fun BottomSheetVerticalScrollWrapContent() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState())
      .padding(start = 24.dp, top = 48.dp, end = 24.dp, bottom = 24.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    repeat(3) {
      BottomSheetRow()
    }
  }
}

@Composable
private fun BottomSheetDragHandle() {
  Box(
    Modifier.fillMaxWidth().padding(top = 22.dp),
    contentAlignment = Alignment.Center,
  ) {
    Box(
      modifier = Modifier
        .background(Color(0xFFCACACA), RoundedCornerShape(100))
        .size(32.dp, 4.dp),
    )
  }
}

@Composable
private fun BottomSheetRow(
  content: @Composable BoxScope.() -> Unit = {},
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(48.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(Color.White)
      .border(1.dp, Color(0xFFE4E4E7), RoundedCornerShape(8.dp)),
    contentAlignment = Alignment.Center,
    content = content,
  )
}

private val PeekDetent = SheetDetent("peek") { _, _ -> 220.dp }
