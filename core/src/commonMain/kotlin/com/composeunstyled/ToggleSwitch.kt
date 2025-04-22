package com.composeunstyled

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun ToggleSwitch(
    toggled: Boolean,
    modifier: Modifier = Modifier,
    onToggled: ((Boolean) -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape = RectangleShape,
    backgroundColor: Color = Color.Unspecified,
    contentPadding: PaddingValues = NoPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    indication: Indication = LocalIndication.current,
    thumb: @Composable () -> Unit,
) {
    var trackWidth by remember { mutableStateOf(0.dp) }
    var thumbWidth by remember { mutableStateOf(0.dp) }

    val layoutDirection = LocalLayoutDirection.current

    val paddingStart = contentPadding.calculateStartPadding(layoutDirection)
    val paddingEnd = contentPadding.calculateEndPadding(layoutDirection)

    val actualTrackWidth by derivedStateOf {
        trackWidth - paddingStart - paddingEnd
    }
    val offset by animateDpAsState(if (toggled) actualTrackWidth - thumbWidth else 0.dp)

    val density = LocalDensity.current

    Box(
        modifier = modifier
            .widthIn(min = 48.dp)
            .clip(shape)
            .background(backgroundColor, shape)
            .onPlaced { trackWidth = with(density) { it.size.width.toDp() } }
                then buildModifier {
            if (onToggled != null) {
                add(
                    Modifier.toggleable(
                        value = toggled,
                        enabled = enabled,
                        interactionSource = interactionSource,
                        indication = indication,
                        role = Role.Switch,
                        onValueChange = onToggled
                    )
                )
            }
        }
            .padding(contentPadding)
    ) {
        Box(
            Modifier
                .offset(x = offset)
                .onPlaced { thumbWidth = with(density) { it.size.width.toDp() } }
        ) {
            thumb()
        }
    }
}
