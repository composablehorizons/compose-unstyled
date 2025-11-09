package com.composeunstyled

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Stable
class DisclosureState(expanded: Boolean = false) {
    var expanded: Boolean by mutableStateOf(expanded)
}

/**
 * Creates a [DisclosureState] that can be used to manually control a [Disclosure].
 *
 * @param initiallyExpanded Whether the disclosure should be initially expanded.
 */
@Composable
fun rememberDisclosureState(initiallyExpanded: Boolean = false): DisclosureState {
    return remember { DisclosureState(initiallyExpanded) }
}

@Stable
class DisclosureScope internal constructor(state: DisclosureState) {
    internal var state by mutableStateOf(state)
}

/**
 * A foundational component used to build disclosure widgets.
 *
 * For interactive preview & code examples, visit [Disclosure Documentation](https://composeunstyled.com/disclosure).
 *
 * ## Basic Example
 *
 * ```kotlin
 * val disclosureState = rememberDisclosureState()
 *
 * Disclosure(state = disclosureState) {
 *     DisclosureHeading(
 *         shape = RoundedCornerShape(8.dp),
 *         backgroundColor = Color.White,
 *         borderColor = Color(0xFFE4E4E4),
 *         borderWidth = 1.dp
 *     ) {
 *         Text("Click to expand")
 *     }
 *     DisclosurePanel {
 *         Text("This is the expanded content")
 *     }
 * }
 * ```
 *
 * @param state The [DisclosureState] that controls the expanded state of the disclosure.
 * @param modifier Modifier to be applied to the disclosure.
 * @param content The content of the disclosure, which should contain a [DisclosureHeading] and a [DisclosurePanel].
 */
@Composable
fun Disclosure(
    state: DisclosureState = rememberDisclosureState(),
    modifier: Modifier = Modifier,
    content: @Composable DisclosureScope.() -> Unit
) {
    val scope = remember { DisclosureScope(state) }

    Column(modifier) {
        scope.content()
    }
}

/**
 * A heading component that can be clicked to expand or collapse the [DisclosurePanel].
 *
 * @param modifier Modifier to be applied to the heading.
 * @param enabled Whether the heading is clickable.
 * @param shape The shape of the heading.
 * @param backgroundColor The background color of the heading.
 * @param contentColor The color of the content.
 * @param contentPadding Padding values for the content.
 * @param borderColor The color of the border.
 * @param borderWidth The width of the border.
 * @param indication The indication to be shown when the heading is interacted with.
 * @param interactionSource The interaction source for the heading.
 * @param verticalAlignment The vertical alignment of the content.
 * @param horizontalArrangement The horizontal arrangement of the content.
 * @param content The content of the heading.
 */
@Composable
fun DisclosureScope.DisclosureHeading(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RectangleShape,
    backgroundColor: Color = Color.Unspecified,
    contentColor: Color = LocalContentColor.current,
    contentPadding: PaddingValues = NoPadding,
    borderColor: Color = Color.Unspecified,
    borderWidth: Dp = 0.dp,
    indication: Indication = LocalIndication.current,
    interactionSource: MutableInteractionSource? = null,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable () -> Unit
) {
    Button(
        modifier = modifier.semantics {
            heading()
            if (state.expanded) {
                collapse {
                    state.expanded = false
                    true
                }
            } else {
                expand {
                    state.expanded = true
                    true
                }
            }
        },
        onClick = { state.expanded = state.expanded.not() },
        interactionSource = interactionSource,
        indication = indication,
        enabled = enabled,
        shape = shape,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        borderColor = borderColor,
        borderWidth = borderWidth,
        contentPadding = contentPadding,
        verticalAlignment = verticalAlignment,
        horizontalArrangement = horizontalArrangement
    ) {
        content()
    }
}

@Composable
fun DisclosureScope.DisclosureHeading(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RectangleShape,
    backgroundColor: Color = Color.Unspecified,
    contentColor: Color = LocalContentColor.current,
    contentPadding: PaddingValues = NoPadding,
    borderColor: Color = Color.Unspecified,
    borderWidth: Dp = 0.dp,
    indication: Indication = LocalIndication.current,
    interactionSource: MutableInteractionSource? = null,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        interactionSource = interactionSource,
        indication = indication,
        enabled = enabled,
        shape = shape,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        borderColor = borderColor,
        borderWidth = borderWidth,
        contentPadding = contentPadding,
        verticalAlignment = verticalAlignment,
        horizontalArrangement = horizontalArrangement
    ) {
        content()
    }
}

/**
 * A panel component that is shown when the [Disclosure] is expanded.
 *
 * @param modifier Modifier to be applied to the panel.
 * @param enter The enter transition for the panel.
 * @param exit The exit transition for the panel.
 * @param content The content of the panel.
 */
@Composable
fun DisclosureScope.DisclosurePanel(
    modifier: Modifier = Modifier,
    enter: EnterTransition = AppearInstantly,
    exit: ExitTransition = DisappearInstantly,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = state.expanded,
        enter = enter,
        exit = exit
    ) {
        content()
    }
}
