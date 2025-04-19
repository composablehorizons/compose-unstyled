package com.composeunstyled.demo

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.composables.core.Icon
import com.composeunstyled.Slider
import com.composeunstyled.Thumb
import com.composeunstyled.rememberSliderState

@Composable
fun SliderDemo() {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFFED213A), Color(0xFF93291E)))),
        contentAlignment = Alignment.Center
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isFocused by interactionSource.collectIsFocusedAsState()
        val isPressed by interactionSource.collectIsPressedAsState()

        val state = rememberSliderState(0.7f)

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Button(
                onClick = { state.value -= 0.1f },
            ) {
                Icon(VolumeDown, "Decrease", tint = Color.Black)
            }

            Slider(
                interactionSource = interactionSource,
                state = state,
                modifier = Modifier.width(400.dp),
                track = {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(100.dp))
                    ) {
                        // the 'not yet completed' part of the track
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .fillMaxWidth()
                                .background(Color(0xFF93291E))
                        )
                        // the 'completed' part of the track
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(state.value)
                                .background(Color.White)
                        )
                    }
                },
                thumb = {
                    val thumbSize by animateDpAsState(targetValue = if (isPressed) 22.dp else 18.dp)

                    val thumbInteractionSource = remember { MutableInteractionSource() }
                    val isHovered by thumbInteractionSource.collectIsHoveredAsState()
                    val glowColor by animateColorAsState(
                        if (isFocused || isHovered) Color.White.copy(0.33f) else Color.Transparent
                    )
                    // keep the size fixed to ensure that the resizing animation is always centered
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(glowColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Thumb(
                            color = Color.White,
                            modifier = Modifier
                                .size(thumbSize)
                                .shadow(4.dp, CircleShape)
                                .hoverable(thumbInteractionSource),
                            shape = CircleShape,
                        )
                    }
                }
            )

            Button(
                onClick = { state.value += 0.1f },
            ) {
                Icon(VolumeUp, "Increase", tint = Color.Black)
            }
        }
    }
}

private val VolumeDown: ImageVector
    get() {
        if (_VolumeDown != null) {
            return _VolumeDown!!
        }
        _VolumeDown = ImageVector.Builder(
            name = "Volume1",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(11f, 5f)
                lineTo(6f, 9f)
                lineTo(2f, 9f)
                lineTo(2f, 15f)
                lineTo(6f, 15f)
                lineTo(11f, 19f)
                lineTo(11f, 5f)
                close()
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(15.54f, 8.46f)
                arcToRelative(5f, 5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, 7.07f)
            }
        }.build()
        return _VolumeDown!!
    }

private var _VolumeDown: ImageVector? = null


val VolumeUp: ImageVector
    get() {
        if (_VolumeUp != null) {
            return _VolumeUp!!
        }
        _VolumeUp = ImageVector.Builder(
            name = "Volume2",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(11f, 5f)
                lineTo(6f, 9f)
                lineTo(2f, 9f)
                lineTo(2f, 15f)
                lineTo(6f, 15f)
                lineTo(11f, 19f)
                lineTo(11f, 5f)
                close()
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(15.54f, 8.46f)
                arcToRelative(5f, 5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, 7.07f)
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(19.07f, 4.93f)
                arcToRelative(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, 14.14f)
            }
        }.build()
        return _VolumeUp!!
    }

private var _VolumeUp: ImageVector? = null

@Composable
private fun Button(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        Modifier.shadow(4.dp, CircleShape).clip(CircleShape).background(Color.White).clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        content()
    }
}

