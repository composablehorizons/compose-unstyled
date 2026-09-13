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
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composeunstyled.EscapeHandler
import com.composeunstyled.Modal
import com.composeunstyled.Scrim
import com.composeunstyled.Text
import com.composeunstyled.UnstyledButton
import com.composeunstyled.rememberModalState
import kotlinx.coroutines.launch

@Composable
fun ModalDemo() {
  val galleryItems = List(6) { "IMAGE ${it + 1}" }

  val modalState = rememberModalState(initiallyVisible = false)
  val modalFocusRequester = remember { FocusRequester() }
  val pagerState = rememberPagerState(pageCount = { galleryItems.size })
  val coroutineScope = rememberCoroutineScope()
  var selectedIndex by remember { mutableIntStateOf(0) }
  val canGoPrevious = pagerState.currentPage > 0
  val canGoNext = pagerState.currentPage < galleryItems.lastIndex

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
      Text(
        "SELECT A PHOTO TO PREVIEW",
        color = Color.Black,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
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
            modifier = Modifier
              .size(110.dp, 72.dp)
              .clip(RectangleShape)
              .background(Color.White)
              .border(1.dp, Color.Black, RectangleShape),
            indication = LocalIndication.current,
          ) {
            Image(
              painter = PrototypeImagePainter,
              contentDescription = item,
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
        scrimColor = Color.White,
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
                  painter = PrototypeImagePainter,
                  contentDescription = galleryItems[page],
                  modifier = Modifier
                    .fillMaxSize()
                    .clip(RectangleShape)
                    .background(Color.Black),
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
                  .clip(RectangleShape)
                  .background(Color.White)
                  .border(1.dp, Color.Black, RectangleShape),
                indication = LocalIndication.current,
              ) {
                Box(Modifier.padding(12.dp)) {
                  Text(if (canGoPrevious) "PREVIOUS" else "FIRST IMAGE")
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
                  .clip(RectangleShape)
                  .background(Color.White)
                  .border(1.dp, Color.Black, RectangleShape),
                indication = LocalIndication.current,
              ) {
                Box(Modifier.padding(12.dp)) {
                  Text(if (canGoNext) "NEXT" else "LAST IMAGE")
                }
              }
            }
          }
        }
      }
    }
  }
}
