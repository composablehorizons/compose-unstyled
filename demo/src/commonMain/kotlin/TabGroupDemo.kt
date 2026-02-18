package com.composeunstyled.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composeunstyled.Tab
import com.composeunstyled.TabGroup
import com.composeunstyled.TabList
import com.composeunstyled.TabPanel
import com.composeunstyled.Text
import com.composeunstyled.UnstyledButton
import com.composeunstyled.platformtheme.dimmed
import com.composeunstyled.platformtheme.indications
import com.composeunstyled.theme.Theme

@Composable
fun TabGroupDemo() {
    class Article(val title: String, val relativeTime: String, val comments: Int, val points: Int)

    val categories = mapOf<String, List<Article>>(
        "Trending" to listOf(
            Article(
                title = "I hosted my startup's backend on a Tamagotchi – AMA",
                relativeTime = "11 hours ago",
                comments = 312,
                points = 1042
            ),
            Article(
                title = "I fired myself to improve company culture — it worked",
                relativeTime = "9 hours ago",
                comments = 264,
                points = 928
            )
        ),
        "Latest" to listOf(
            Article(
                title = "The office microwave is now a Kubernetes node",
                relativeTime = "2 hours ago",
                comments = 87,
                points = 356
            ),
            Article(
                title = "We replaced scrum with interpretive dancing",
                relativeTime = "1 hour ago",
                comments = 52,
                points = 198
            )
        ),
        "Popular" to listOf(
            Article(
                title = "Social network for ants is growing fast",
                relativeTime = "14 hours ago",
                comments = 412,
                points = 1376
            ),
            Article(
                title = "Why I quit my $800K FAANG job to grow mushrooms",
                relativeTime = "16 hours ago",
                comments = 391,
                points = 1204
            )
        )
    )


    var selectedTab by remember { mutableStateOf(categories.keys.first()) }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFF00D2FF), Color(0xFF3A7BD5))))
            .padding(16.dp)
            .padding(top = 90.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        TabGroup(selectedTab = selectedTab, tabs = categories.keys.toList()) {
            TabList(
                modifier = Modifier.fillMaxWidth().height(48.dp).shadow(4.dp, RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
                backgroundColor = Color.White,
            ) {
                categories.forEach { (key, articles) ->
                    val selected = key == selectedTab
                    Tab(
                        key = key,
                        selected = selected,
                        onSelected = { selectedTab = key },
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        indication = Theme[indications][dimmed]
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = key,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) {
                                    Color(0xFF2196F3)
                                } else {
                                    Color(0xFF757575)
                                }
                            )
                            if (selected) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = Color(0xFF2196F3),
                                            shape = RoundedCornerShape(2.dp)
                                        )
                                        .fillMaxWidth()
                                        .height(3.dp)
                                        .align(Alignment.BottomCenter)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(8.dp))
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp)
            ) {
                categories.forEach { (key, items) ->
                    TabPanel(key = key) {
                        Column {
                            items.forEach { item ->
                                UnstyledButton(
                                    onClick = { /* TODO */ },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(12.dp)
                                ) {
                                    Column {
                                        Text(item.title, fontWeight = FontWeight.Medium)
                                        Spacer(Modifier.height(4.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth().alpha(0.6f)
                                        ) {
                                            Text(item.relativeTime)
                                            Text("·")
                                            Text("${item.comments} comments")
                                            Text("·")
                                            Text("${item.points} shares")
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

