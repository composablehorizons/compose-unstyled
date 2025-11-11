package com.composeunstyled

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp


/**
 * A foundational component used to build toggle switches.
 *
 * For interactive preview & code examples, visit [Toggle Switch Documentation](https://composeunstyled.com/toggleswitch).
 *
 * ## Basic Example
 *
 * ```kotlin
 * var toggled by remember { mutableStateOf(false) }
 *
 * ToggleSwitch(
 *     toggled = toggled,
 *     onToggled = { toggled = it },
 *     backgroundColor = Color.Gray,
 *     thumb = {
 *         Box(
 *             modifier = Modifier
 *                 .size(24.dp)
 *                 .background(Color.White, CircleShape)
 *         )
 *     }
 * )
 * ```
 *
 * @param toggled Whether the switch is currently toggled on.
 * @param modifier Modifier to be applied to the switch.
 * @param onToggled Callback that is called when the switch is toggled.
 * @param enabled Whether the switch is enabled.
 * @param shape The shape of the switch's track.
 * @param backgroundColor The background color of the switch's track.
 * @param contentPadding The padding to be applied to the switch's content.
 * @param interactionSource The [MutableInteractionSource] that will be used to dispatch interaction events.
 * @param indication The indication to be shown when the switch is interacted with.
 * @param thumb The composable that represents the thumb of the switch.
 */
@Composable
fun ToggleSwitch(
    toggled: Boolean,
    modifier: Modifier = Modifier,
    onToggled: ((Boolean) -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape = RectangleShape,
    backgroundColor: Color = Color.Unspecified,
    contentPadding: PaddingValues = NoPadding,
    interactionSource: MutableInteractionSource? = null,
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

    val hasMeasured by derivedStateOf {
        trackWidth > 0.dp && thumbWidth > 0.dp
    }

    val targetOffset = if (toggled) actualTrackWidth - thumbWidth else 0.dp
    val offset by if (hasMeasured) {
        animateDpAsState(targetValue = targetOffset, animationSpec = tween())
    } else {
        remember { mutableStateOf(0.dp) }
    }

    val density = LocalDensity.current

    Box(
        modifier = modifier
            .widthIn(min = 48.dp)
            .clip(shape)
            .background(backgroundColor, shape)
            .onSizeChanged { trackWidth = with(density) { it.width.toDp() } }
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
                .offset { IntOffset(offset.roundToPx(), 0) }
                .onSizeChanged { thumbWidth = with(density) { it.width.toDp() } }
                .alpha(if (hasMeasured) 1f else 0f)
        ) {
            thumb()
        }
    }
}
