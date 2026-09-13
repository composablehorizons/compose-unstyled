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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composeunstyled.Text
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledTab
import com.composeunstyled.UnstyledTabGroup
import com.composeunstyled.UnstyledTabList
import com.composeunstyled.UnstyledTabPanel

@Composable
fun TabGroupDemo() {
  class Article(val title: String, val relativeTime: String, val comments: Int, val points: Int)

  val categories = mapOf(
    "TRENDING" to listOf(
      Article(
        title = "I HOSTED MY STARTUP'S BACKEND ON A TAMAGOTCHI – AMA",
        relativeTime = "11 HOURS AGO",
        comments = 312,
        points = 1042,
      ),
      Article(
        title = "I FIRED MYSELF TO IMPROVE COMPANY CULTURE — IT WORKED",
        relativeTime = "9 HOURS AGO",
        comments = 264,
        points = 928,
      ),
    ),
    "LATEST" to listOf(
      Article(
        title = "THE OFFICE MICROWAVE IS NOW A KUBERNETES NODE",
        relativeTime = "2 HOURS AGO",
        comments = 87,
        points = 356,
      ),
      Article(
        title = "WE REPLACED SCRUM WITH INTERPRETIVE DANCING",
        relativeTime = "1 HOUR AGO",
        comments = 52,
        points = 198,
      ),
    ),
    "POPULAR" to listOf(
      Article(
        title = "SOCIAL NETWORK FOR ANTS IS GROWING FAST",
        relativeTime = "14 HOURS AGO",
        comments = 412,
        points = 1376,
      ),
      Article(
        title = "WHY I QUIT MY $800K FAANG JOB TO GROW MUSHROOMS",
        relativeTime = "16 HOURS AGO",
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
        UnstyledTabList(
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RectangleShape)
            .background(Color.White)
            .border(1.dp, Color.Black, RectangleShape),
        ) {
          Row(Modifier.fillMaxSize()) {
            categories.forEach { (key, _) ->
              UnstyledTab(
                key = key,
                modifier = Modifier.weight(1f).fillMaxHeight(),
                indication = LocalIndication.current,
              ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                  Text(
                    text = key,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    color = Color.Black,
                  )
                  if (selected) {
                    Box(
                      modifier = Modifier
                        .background(
                          color = Color.Black,
                          shape = RectangleShape,
                        )
                        .fillMaxWidth()
                        .height(1.dp)
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
          UnstyledTabPanel(
            key = key,
            modifier = Modifier
              .fillMaxWidth()
              .background(
                color = Color.White,
                shape = RectangleShape,
              )
              .border(1.dp, Color.Black, RectangleShape),
          ) {
            Column(Modifier.padding(16.dp)) {
              items.forEach { item ->
                UnstyledButton(
                  onClick = { /* TODO */ },
                  modifier = Modifier.clip(RectangleShape),
                  indication = LocalIndication.current,
                ) {
                  Column(Modifier.padding(12.dp)) {
                    Text(
                      item.title,
                      fontWeight = FontWeight.Medium,
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                      horizontalArrangement = Arrangement.spacedBy(4.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.fillMaxWidth(),
                    ) {
                      Text(item.relativeTime)
                      Text("·")
                      Text("${item.comments} COMMENTS")
                      Text("·")
                      Text("${item.points} SHARES")
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
