package com.composeunstyled.demo

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Volume1
import com.composables.icons.lucide.Volume2
import com.composeunstyled.Button
import com.composeunstyled.Icon
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

        val state = rememberSliderState(initialValue = 0.7f)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 16.dp).widthIn(max = 480.dp).fillMaxWidth()
        ) {
            Button(
                onClick = { state.value -= 0.1f },
                modifier = Modifier.shadow(4.dp, CircleShape),
                shape = CircleShape,
                backgroundColor = Color.White,
                contentPadding = PaddingValues(8.dp),
            ) {
                Icon(Lucide.Volume1, "Decrease")
            }

            Slider(
                interactionSource = interactionSource,
                state = state,
                modifier = Modifier.weight(1f),
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
                modifier = Modifier.shadow(4.dp, CircleShape),
                shape = CircleShape,
                backgroundColor = Color.White,
                contentPadding = PaddingValues(8.dp),
            ) {
                Icon(Lucide.Volume2, "Increase")
            }
        }
    }
}
