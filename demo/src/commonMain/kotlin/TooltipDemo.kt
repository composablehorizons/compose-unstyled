package com.composeunstyled.demo

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.BellDot
import com.composables.icons.lucide.Lucide
import com.composeunstyled.Button
import com.composeunstyled.Icon
import com.composeunstyled.ProvideContentColor
import com.composeunstyled.RelativeAlignment
import com.composeunstyled.Text
import com.composeunstyled.Tooltip
import com.composeunstyled.TooltipArrowDirection
import com.composeunstyled.TooltipPanel
import com.composeunstyled.focusRing

@Composable
fun TooltipDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFFFED359), Color(0xFFFFBD66)))),
        contentAlignment = Alignment.Center
    ) {
        Row {
            Tooltip(
                placement = RelativeAlignment.TopCenter,
                panel = {
                    TooltipPanel(
                        enter = slideInVertically(tween(150), initialOffsetY = { (it * 0.25).toInt() }) +
                                scaleIn(
                                    animationSpec = tween(150),
                                    transformOrigin = TransformOrigin(0.5f, 1f),
                                    initialScale = 0.65f
                                ) + fadeIn(tween(150)),
                        exit = fadeOut(tween(250)),
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
                                    .clip(RoundedCornerShape(100))
                                    .background(Color.Black.copy(0.8f))
                                    .padding(vertical = 8.dp, horizontal = 12.dp),
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
                    modifier = Modifier.focusRing(interactionSource, 1.dp, Color(0xFF3B82F6), CircleShape),
                    interactionSource = interactionSource,
                    backgroundColor = Color.White
                ) {
                    Icon(Lucide.BellDot, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun ArrowUp(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier.size(8.dp, 4.dp)) {
        val path = Path().apply {
            moveTo(size.width / 2f, 0f)
            lineTo(0f, size.height)
            lineTo(size.width, size.height)
            close()
        }
        drawPath(path, color = color)
    }
}
