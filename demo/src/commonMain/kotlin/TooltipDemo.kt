package com.composeunstyled.demo

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.BellDot
import com.composables.icons.lucide.Lucide
import com.composeunstyled.*

@Composable
fun TooltipDemo() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Row {
            Tooltip(
                placement = RelativeAlignment.TopCenter,
                hoverDelayMillis = 500L,
                panel = {
                    TooltipPanel(
                        enter = fadeIn(tween(durationMillis = 200)),
                        exit = fadeOut(),
                        arrow = { side ->
                            val degrees = when (side) {
                                TooltipArrowDirection.Up -> 0f
                                TooltipArrowDirection.Down -> 180f
                                TooltipArrowDirection.Left -> 90f
                                TooltipArrowDirection.Right -> 270f
                            }
                            ArrowUp(Modifier.rotate(degrees), Color.Black.copy(0.8f))
                        }
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.Black.copy(0.8f))
                                    .padding(8.dp),
                            ) {
                                ProvideContentColor(Color.White) {
                                    Text("Notifications")
                                }
                            }
                        }
                    }
                }
            ) {
                val interactionSource = remember { MutableInteractionSource() }

                Button(
                    onClick = { },
                    contentPadding = PaddingValues(8.dp),
                    shape = CircleShape,
                    modifier = Modifier.focusRing(interactionSource, 1.dp, Color.Blue, CircleShape),
                    interactionSource = interactionSource
                ) {
                    Icon(Lucide.BellDot, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun ArrowUp(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(8.dp)) {
        val path = Path().apply {
            moveTo(size.width / 2f, 0f)
            lineTo(0f, size.height)
            lineTo(size.width, size.height)
            close()
        }
        drawPath(path, color = color)
    }
}
