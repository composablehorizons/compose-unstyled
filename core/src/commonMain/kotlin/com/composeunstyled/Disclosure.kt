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

@Composable
fun rememberDisclosureState(initiallyExpanded: Boolean = false): DisclosureState {
    return remember { DisclosureState(initiallyExpanded) }
}

@Stable
class DisclosureScope internal constructor(state: DisclosureState) {
    internal var state by mutableStateOf(state)
}

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
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
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
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
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
