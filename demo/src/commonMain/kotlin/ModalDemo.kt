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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.ArrowRight
import com.composables.icons.lucide.Lucide
import com.composables.uripainter.rememberUriPainter
import com.composeunstyled.EscapeHandler
import com.composeunstyled.Modal
import com.composeunstyled.Scrim
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledIcon
import com.composeunstyled.rememberModalState
import kotlinx.coroutines.launch

@Composable
fun ModalDemo() {
  data class GalleryItem(val url: String, val description: String)

  val galleryItems = listOf(
    GalleryItem(
      "https://images.unsplash.com/photo-1472214103451-9374bd1c798e" +
        "?q=80&w=1080&auto=format&fit=crop",
      "Golden wheat field",
    ),
    GalleryItem(
      "https://images.unsplash.com/photo-1469474968028-56623f02e42e" +
        "?q=80&w=1080&auto=format&fit=crop",
      "Mountain landscape",
    ),
    GalleryItem(
      "https://images.unsplash.com/photo-1500534623283-312aade485b7" +
        "?q=80&w=1080&auto=format&fit=crop",
      "Sunlit forest",
    ),
    GalleryItem(
      "https://images.unsplash.com/photo-1507525428034-b723cf961d3e" +
        "?q=80&w=1080&auto=format&fit=crop",
      "Ocean wave",
    ),
    GalleryItem(
      "https://images.unsplash.com/photo-1501785888041-af3ef285b470" +
        "?q=80&w=1080&auto=format&fit=crop",
      "Mountain lake at dawn",
    ),
    GalleryItem(
      "https://images.unsplash.com/photo-1448375240586-882707db888b" +
        "?q=80&w=1080&auto=format&fit=crop",
      "Misty pine forest",
    ),
  )
  val modalState = rememberModalState(initiallyVisible = false)
  val modalFocusRequester = remember { FocusRequester() }
  val pagerState = rememberPagerState(pageCount = { galleryItems.size })
  val coroutineScope = rememberCoroutineScope()
  var selectedIndex by remember { mutableIntStateOf(0) }
  val canGoPrevious = pagerState.currentPage > 0
  val canGoNext = pagerState.currentPage < galleryItems.lastIndex
  val previousButtonAlpha by animateFloatAsState(
    targetValue = if (canGoPrevious) 1f else 0.33f,
    animationSpec = tween(durationMillis = 180),
  )
  val nextButtonAlpha by animateFloatAsState(
    targetValue = if (canGoNext) 1f else 0.33f,
    animationSpec = tween(durationMillis = 180),
  )

  LaunchedEffect(modalState.transitionState.targetState, selectedIndex) {
    if (modalState.transitionState.targetState) {
      pagerState.scrollToPage(selectedIndex)
      modalFocusRequester.requestFocus()
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(24.dp),
    contentAlignment = Alignment.Center,
  ) {
    Column(
      modifier = Modifier.widthIn(max = 420.dp).fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      BasicText(
        "Select a photo to preview",
        style = TextStyle(
          color = Color(0xFF18181B),
          fontSize = 14.sp,
          fontWeight = FontWeight.Medium,
        ),
      )

      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
      ) {
        galleryItems.forEachIndexed { index, item ->
          UnstyledButton(
            onClick = {
              selectedIndex = index
              modalState.transitionState.targetState = true
            },
            modifier = Modifier.size(110.dp, 72.dp).clip(RoundedCornerShape(8.dp)),
          ) {
            Image(
              painter = rememberUriPainter(item.url),
              contentDescription = item.description,
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop,
            )
          }
        }
      }
    }

    Modal(
      state = modalState,
      onKeyEvent = { event ->
        if (event.type != KeyEventType.KeyDown) return@Modal false

        when (event.key) {
          Key.DirectionLeft -> {
            if (canGoPrevious) {
              coroutineScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage - 1)
              }
            }
            true
          }

          Key.DirectionRight -> {
            if (canGoNext) {
              coroutineScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
              }
            }
            true
          }

          else -> false
        }
      },
    ) {
      EscapeHandler {
        modalState.transitionState.targetState = false
      }
      Scrim(
        enter = fadeIn(tween(durationMillis = 220)),
        exit = fadeOut(tween(durationMillis = 180)),
      )

      Box(
        modifier = Modifier
          .fillMaxSize()
          .focusRequester(modalFocusRequester)
          .focusable()
          .pointerInput(Unit) {
            detectTapGestures { modalState.transitionState.targetState = false }
          },
        contentAlignment = Alignment.Center,
      ) {
        AnimatedVisibility(
          visibleState = modalState.transitionState,
          enter = scaleIn(animationSpec = tween(220), initialScale = 0.97f) + fadeIn(tween(220)),
          exit = scaleOut(animationSpec = tween(180), targetScale = 0.98f) + fadeOut(tween(180)),
        ) {
          BoxWithConstraints(
            modifier = Modifier
              .fillMaxSize()
              .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center,
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 900.dp)
                .pointerInput(Unit) { detectTapGestures { } },
              contentAlignment = Alignment.Center,
            ) {
              HorizontalPager(
                state = pagerState,
                pageSize = PageSize.Fill,
                pageSpacing = 18.dp,
                contentPadding = PaddingValues(horizontal = 34.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 20.dp),
              ) { page ->
                Image(
                  painter = rememberUriPainter(galleryItems[page].url),
                  contentDescription = galleryItems[page].description,
                  modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF27272A)),
                  contentScale = ContentScale.Crop,
                )
              }

              UnstyledButton(
                onClick = {
                  coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                  }
                },
                enabled = canGoPrevious,
                interactionSource = remember { MutableInteractionSource() },
                modifier = Modifier
                  .align(Alignment.CenterStart)
                  .padding(start = 16.dp)
                  .clip(CircleShape)
                  .background(Color.White)
                  .alpha(previousButtonAlpha),
              ) {
                Box(Modifier.padding(12.dp)) {
                  UnstyledIcon(
                    imageVector = Lucide.ArrowLeft,
                    contentDescription = "Previous image",
                  )
                }
              }

              UnstyledButton(
                onClick = {
                  coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                  }
                },
                enabled = canGoNext,
                interactionSource = remember { MutableInteractionSource() },
                modifier = Modifier
                  .align(Alignment.CenterEnd)
                  .padding(end = 16.dp)
                  .clip(CircleShape)
                  .background(Color.White)
                  .alpha(nextButtonAlpha),
              ) {
                Box(Modifier.padding(12.dp)) {
                  UnstyledIcon(
                    imageVector = Lucide.ArrowRight,
                    contentDescription = "Next image",
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
