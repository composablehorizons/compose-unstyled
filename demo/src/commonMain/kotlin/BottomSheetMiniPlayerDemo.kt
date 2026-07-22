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
package com.composeunstyled.demo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Bookmark
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pause
import com.composables.uripainter.rememberUriPainter
import com.composeunstyled.Sheet
import com.composeunstyled.SheetDetent
import com.composeunstyled.SheetDetent.Companion.FullyExpanded
import com.composeunstyled.UnstyledBottomSheet
import com.composeunstyled.rememberBottomSheetState

private val MiniPlayerSheetPadding = 20.dp
private val MiniPlayerArtworkSize = 48.dp

@Composable
fun BottomSheetMiniPlayerDemo() {
  val Mini = remember {
    SheetDetent("mini") { _, _ ->
      MiniPlayerSheetPadding + MiniPlayerArtworkSize + MiniPlayerSheetPadding
    }
  }
  val sheetState = rememberBottomSheetState(
    initialDetent = Mini,
    detents = listOf(Mini, FullyExpanded),
  )

  UnstyledBottomSheet(
    state = sheetState,
    modifier = Modifier.fillMaxHeight(),
    measureContentBeyondContainerBounds = true,
  ) {
    Sheet(
      modifier = Modifier
        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
        .background(Color(0xFF0F172A))
        .padding(MiniPlayerSheetPadding),
    ) {
      Column {
        NowPlayingRow()
        Spacer(Modifier.height(20.dp))
        EpisodeRow("The Future Was Already Here", "42 min")
        EpisodeRow("When APIs Become Product Design", "37 min")
        EpisodeRow("Designing for One More Drag", "29 min")
      }
    }
  }
}

@Composable
private fun NowPlayingRow() {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 12.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    CoverArt()
    Column(Modifier.weight(1f)) {
      BasicText(
        text = "Fragments of Time",
        style = TextStyle(
          color = Color.White,
          fontSize = 15.sp,
          fontWeight = FontWeight.SemiBold,
        ),
      )
      Spacer(Modifier.height(4.dp))
      BasicText(
        text = "The @Deprecated",
        style = TextStyle(
          color = Color.White.copy(alpha = 0.66f),
          fontSize = 13.sp,
        ),
      )
    }
    PlayerIconButton {
      Image(
        imageVector = Lucide.Pause,
        contentDescription = null,
        colorFilter = ColorFilter.tint(Color.White),
      )
    }
    PlayerIconButton {
      Image(
        imageVector = Lucide.ChevronRight,
        contentDescription = null,
        colorFilter = ColorFilter.tint(Color.White),
      )
    }
  }
}

@Composable
private fun EpisodeRow(
  title: String,
  duration: String,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    CoverArt()
    Column(Modifier.weight(1f)) {
      BasicText(
        text = title,
        style = TextStyle(
          color = Color.White,
          fontSize = 14.sp,
          fontWeight = FontWeight.Medium,
        ),
      )
      Spacer(Modifier.height(4.dp))
      BasicText(
        text = duration,
        style = TextStyle(
          color = Color.White.copy(alpha = 0.66f),
          fontSize = 12.sp,
        ),
      )
    }
    PlayerIconButton {
      Image(
        imageVector = Lucide.Bookmark,
        contentDescription = null,
        colorFilter = ColorFilter.tint(Color.White),
      )
    }
  }
}

@Composable
private fun CoverArt() {
  Image(
    painter = rememberUriPainter(
      "https://images.unsplash.com/photo-1499364615650-ec38552f4f34?q=80&w=1080",
    ),
    contentDescription = null,
    modifier = Modifier
      .size(MiniPlayerArtworkSize)
      .clip(RoundedCornerShape(10.dp)),
    contentScale = ContentScale.Crop,
  )
}

@Composable
private fun PlayerIconButton(content: @Composable () -> Unit) {
  Box(
    modifier = Modifier
      .size(36.dp)
      .clip(RoundedCornerShape(18.dp))
      .background(Color.White.copy(alpha = 0.11f))
      .padding(9.dp),
    contentAlignment = Alignment.Center,
  ) {
    content()
  }
}
