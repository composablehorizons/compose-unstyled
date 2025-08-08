package com.composeunstyled.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composeunstyled.Icon
import com.composeunstyled.TopAppBar
import com.composeunstyled.Text

/**
 * Demo showcasing various configurations of the TopAppBar composable.
 *
 * This demo demonstrates different ways to use the TopAppBar component
 * with various titles, icons, colors, and styling options.
 */
@Composable
fun TopAppBarDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2)
                    )
                )
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Demo title - No MaterialTheme used
            Text(
                text = "TopAppBar Demo",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Basic TopAppBar with default settings
            DemoCard("Basic TopAppBar") {
                TopAppBar(
                    title = "Home",
                    backgroundColor = Color.White,
                    contentColor = Color.Black
                )
            }

            // TopAppBar with back arrow
            DemoCard("With Back Arrow") {
                TopAppBar(
                    title = "Profile",
                    navigationIcon = {
                        Icon(
                            Person,
                            contentDescription = null,
                            tint = Color(0xFF212121),
                            modifier = Modifier.requiredSize(84.dp)
                        )
                    },                    backgroundColor = Color(0xFF4CAF50),
                    contentColor = Color.White,
                    iconContentDescription = "Back"
                )
            }

            // TopAppBar with search icon
            DemoCard("With Search Icon") {
                TopAppBar(
                    title = "Search",
                    navigationIcon = {
                        Icon(
                            Search,
                            contentDescription = null,
                            tint = Color(0xFF212121),
                            modifier = Modifier.requiredSize(84.dp)
                        )
                    },                    backgroundColor = Color(0xFFFF5722),
                    contentColor = Color.White
                )
            }

            // TopAppBar with settings icon and custom height
            DemoCard("Custom Height & Settings") {
                TopAppBar(
                    title = "Settings",
                    navigationIcon = {
                        Icon(
                            Settings,
                            contentDescription = null,
                            tint = Color(0xFF212121),
                            modifier = Modifier.requiredSize(84.dp)
                        )
                    },
                    backgroundColor = Color(0xFF9C27B0),
                    contentColor = Color.White,
                    height = 72.dp
                )
            }

            // TopAppBar with favourite icon
            DemoCard("Favorite") {
                TopAppBar(
                    title = "Favorite",
                    navigationIcon = {
                        Icon(
                            Favorite,
                            contentDescription = null,
                            tint = Color(0xFF212121),
                            modifier = Modifier.requiredSize(84.dp)
                        )
                    },                    backgroundColor = Color.Transparent,
                    contentColor = Color.Black
                )
            }

            // TopAppBar with different text styling - No MaterialTheme used
            DemoCard("Custom Text Style") {
                TopAppBar(
                    title = "Custom Style",
                    backgroundColor = Color(0xFF607D8B),
                    contentColor = Color.White,
                    textStyle = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }

            // TopAppBar with larger icon
            DemoCard("Large Icon Size") {
                TopAppBar(
                    title = "Large Icon",
                    backgroundColor = Color(0xFFFF9800),
                    contentColor = Color.White,
                    iconSize = 32.dp
                )
            }
        }
    }
}

/**
 * Custom card implementation without Material dependencies.
 * Creates a card-like appearance using Box with background, shadow, and border.
 */
@Composable
private fun DemoCard(
    title: String,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false
            )
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.95f))
            .border(
                width = 1.dp,
                color = Color.Black.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Custom text styling without MaterialTheme
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            ) {
                content()
            }
        }
    }
}
