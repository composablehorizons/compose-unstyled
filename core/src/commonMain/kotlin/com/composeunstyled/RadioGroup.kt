package com.composeunstyled

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

private val LocalInnerRadioGroupState = staticCompositionLocalOf { InnerRadioGroupState() }

@Composable
fun RadioGroup(
    value: String?,
    onValueChange: (String) -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val focusManager = LocalFocusManager.current
    val state = remember { InnerRadioGroupState() }

    SideEffect {
        state.value = value
        state.onValueChange = onValueChange
    }

    Column(
        modifier
            .selectableGroup()
            .semantics {
                if (contentDescription != null) {
                    this.contentDescription = contentDescription
                }
            }
            .onKeyEvent { event ->
                when (event.key) {
                    Key.DirectionDown, Key.DirectionRight -> {
                        if (event.isKeyDown) {
                            focusManager.moveFocus(FocusDirection.Next)
                        }
                        true
                    }

                    Key.DirectionUp, Key.DirectionLeft -> {
                        if (event.isKeyDown) {
                            focusManager.moveFocus(FocusDirection.Previous)
                        }
                        true
                    }

                    else -> false
                }
            }
    ) {
        CompositionLocalProvider(LocalInnerRadioGroupState provides state) {
            content()
        }
    }
}

private class InnerRadioGroupState {
    var value by mutableStateOf<String?>(null)
    var onValueChange by mutableStateOf<(String) -> Unit>({})
}

@Composable
fun RadioButton(
    value: String,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    backgroundColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    selectedColor: Color = Color.Unspecified,
    enabled: Boolean = true,
    contentPadding: PaddingValues = NoPadding,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = LocalIndication.current,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable (RowScope.() -> Unit),
) {
    val state = LocalInnerRadioGroupState.current
    val selected = state.value == value

    Row(
        modifier = modifier
            .semantics(mergeDescendants = true) { }
            .clip(shape)
            .background(backgroundColor)
            .toggleable(
                value = selected,
                onValueChange = { selected ->
                    if (selected) {
                        state.onValueChange(value)
                    }
                },
                role = Role.RadioButton,
                enabled = enabled,
                indication = indication,
                interactionSource = interactionSource,
            )
            .padding(contentPadding),
        verticalAlignment = verticalAlignment,
        horizontalArrangement = horizontalArrangement
    ) {
        CompositionLocalProvider(LocalContentColor provides if (selected) contentColor else selectedColor) {
            this@Row.content()
        }
    }
}

