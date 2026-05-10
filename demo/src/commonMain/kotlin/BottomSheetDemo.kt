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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Play
import com.composables.uripainter.rememberUriPainter
import com.composeunstyled.Sheet
import com.composeunstyled.SheetDetent
import com.composeunstyled.SheetDetent.Companion.FullyExpanded
import com.composeunstyled.Text
import com.composeunstyled.UnstyledBottomSheet
import com.composeunstyled.UnstyledIcon
import com.composeunstyled.rememberBottomSheetState

@Composable
fun BottomSheetDemo() {
  val peekHeight = 74.dp
  val mini = SheetDetent("mini") { _, _ ->
    peekHeight
  }
  val sheetState = rememberBottomSheetState(
    initialDetent = mini,
    detents = listOf(mini, FullyExpanded),
  )
  LaunchedEffect(sheetState) {
    sheetState.targetDetent = FullyExpanded
  }
  val coverUrl = "https://images.unsplash.com/photo-1499364615650-ec38552f4f34?q=80&w=512"
  val tracks = listOf(
    "Memory Leak",
    "Out of Context",
    "Fragments of Time",
    "Android Love",
    "Mr. Roboto",
    "Invalidate",
  )

  UnstyledBottomSheet(
    state = sheetState,
    modifier = Modifier.fillMaxSize(),
  ) {
    Sheet(
      modifier = Modifier
        .padding(top = 24.dp)
        .dropShadow(
          shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
          shadow = Shadow(
            radius = 24.dp,
            spread = 0.dp,
            color = Color.Black.copy(alpha = 0.14f),
            offset = DpOffset(x = 0.dp, y = (-3).dp),
          ),
        )
        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
        .background(Color.White)
        .fillMaxWidth(),
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              if (sheetState.currentDetent != FullyExpanded) {
                sheetState.targetDetent = FullyExpanded
              }
            }
            .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Box(
            modifier = Modifier
              .size(52.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFE4E4E7)),
          ) {
            Image(
              painter = rememberUriPainter(coverUrl),
              contentDescription = "Album cover",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize(),
            )
          }
          Column(
            modifier = Modifier
              .weight(1f)
              .padding(horizontal = 12.dp),
          ) {
            Text(
              "Just hoist it!",
              fontWeight = FontWeight.SemiBold,
              fontSize = 15.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
            Text(
              "The @Deprecated",
              color = Color(0xFF71717A),
              fontSize = 13.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(RoundedCornerShape(100))
              .background(Color.White.copy(alpha = 0.92f)),
            contentAlignment = Alignment.Center,
          ) {
            UnstyledIcon(
              Lucide.Play,
              contentDescription = "Play",
              tint = Color(0xFF18181B),
              modifier = Modifier.size(18.dp),
            )
          }
        }
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFE4E4E7)),
        )

        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        ) {
          items(count = tracks.size) { index ->
            val title = tracks[index]
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 56.dp)
                .padding(horizontal = 16.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                "${index + 1}",
                color = Color(0xFF71717A),
                fontSize = 12.sp,
                modifier = Modifier.padding(end = 12.dp),
              )
              Text(
                title,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f),
              )
              Text(
                "3:${10 + index}",
                color = Color(0xFF71717A),
                fontSize = 12.sp,
              )
            }
          }
        }
      }
    }
  }
}
