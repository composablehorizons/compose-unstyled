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

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composeunstyled.Tab
import com.composeunstyled.TabList
import com.composeunstyled.TabPanel
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledTabGroup

@Composable
fun TabGroupDemo() {
  class Article(val title: String, val relativeTime: String, val comments: Int, val points: Int)

  val categories = mapOf<String, List<Article>>(
    "Trending" to listOf(
      Article(
        title = "I hosted my startup's backend on a Tamagotchi – AMA",
        relativeTime = "11 hours ago",
        comments = 312,
        points = 1042,
      ),
      Article(
        title = "I fired myself to improve company culture — it worked",
        relativeTime = "9 hours ago",
        comments = 264,
        points = 928,
      ),
    ),
    "Latest" to listOf(
      Article(
        title = "The office microwave is now a Kubernetes node",
        relativeTime = "2 hours ago",
        comments = 87,
        points = 356,
      ),
      Article(
        title = "We replaced scrum with interpretive dancing",
        relativeTime = "1 hour ago",
        comments = 52,
        points = 198,
      ),
    ),
    "Popular" to listOf(
      Article(
        title = "Social network for ants is growing fast",
        relativeTime = "14 hours ago",
        comments = 412,
        points = 1376,
      ),
      Article(
        title = "Why I quit my $800K FAANG job to grow mushrooms",
        relativeTime = "16 hours ago",
        comments = 391,
        points = 1204,
      ),
    ),
  )

  var selectedTab by remember { mutableStateOf(categories.keys.first()) }

  Box(
    modifier = Modifier.fillMaxSize()
      .padding(16.dp)
      .padding(top = 90.dp),
    contentAlignment = Alignment.TopCenter,
  ) {
    UnstyledTabGroup(
      selectedTab = selectedTab,
      onSelectedTabChange = { selectedTab = it },
      tabs = categories.keys.toList(),
      modifier = Modifier.widthIn(max = 450.dp),
    ) {
      Column {
        TabList(
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, Color(0xFFCACACA), RoundedCornerShape(8.dp)),
        ) {
          Row(Modifier.fillMaxSize()) {
            categories.forEach { (key, _) ->
              Tab(
                key = key,
                modifier = Modifier.weight(1f).fillMaxHeight(),
                indication = LocalIndication.current,
              ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                  BasicText(
                    text = key,
                    style = TextStyle(
                      fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                      color = if (selected) {
                        Color.Black
                      } else {
                        Color(0xFF757575)
                      },
                    ),
                  )
                  if (selected) {
                    Box(
                      modifier = Modifier
                        .background(
                          color = Color.Black,
                          shape = RoundedCornerShape(2.dp),
                        )
                        .fillMaxWidth()
                        .height(3.dp)
                        .align(Alignment.BottomCenter),
                    )
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))
        categories.forEach { (key, items) ->
          TabPanel(
            key = key,
            modifier = Modifier
              .fillMaxWidth()
              .background(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(8.dp),
              )
              .border(1.dp, Color(0xFFCACACA), RoundedCornerShape(8.dp)),
          ) {
            Column(Modifier.padding(16.dp)) {
              items.forEach { item ->
                UnstyledButton(
                  onClick = { /* TODO */ },
                  modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                  indication = LocalIndication.current,
                ) {
                  Column(Modifier.padding(12.dp)) {
                    BasicText(item.title, style = TextStyle(fontWeight = FontWeight.Medium))
                    Spacer(Modifier.height(4.dp))
                    Row(
                      horizontalArrangement = Arrangement.spacedBy(4.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.fillMaxWidth().alpha(0.6f),
                    ) {
                      BasicText(item.relativeTime)
                      BasicText("·")
                      BasicText("${item.comments} comments")
                      BasicText("·")
                      BasicText("${item.points} shares")
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
