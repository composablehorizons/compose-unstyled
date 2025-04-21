package com.composeunstyled

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Stable
class RadioGroupState(selectedOption: String? = null) {
    var selectedOption by mutableStateOf(selectedOption)
}

@Composable
fun rememberRadioGroupState(initialValue: String): RadioGroupState {
    return remember {
        RadioGroupState(selectedOption = initialValue)
    }
}


class RadioGroupScope(val state: RadioGroupState)

@Composable
fun RadioGroup(
    state: RadioGroupState,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    content: @Composable RadioGroupScope.() -> Unit
) {
    val focusManager = LocalFocusManager.current
    val scope = remember { RadioGroupScope(state) }

    Box(modifier.selectableGroup().semantics {
        if (contentDescription != null) {
            this.contentDescription = contentDescription
        }
    }.onKeyEvent {
        when (it.key) {
            Key.DirectionDown, Key.DirectionRight -> {
                if (it.isKeyDown) {
                    focusManager.moveFocus(FocusDirection.Next)
                }
                true
            }

            Key.DirectionUp, Key.DirectionLeft -> {
                if (it.isKeyDown) {
                    focusManager.moveFocus(FocusDirection.Previous)
                }
                true
            }

            else -> false
        }
    }) {
        with(scope) {
            content()
        }
    }
}

@Composable
fun RadioGroupScope.Radio(
    value: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    selectedColor: Color = Color.Unspecified,
    enabled: Boolean = true,
    shape: Shape = RectangleShape,
    borderColor: Color = Color.Unspecified,
    borderWidth: Dp = Dp.Unspecified,
    contentPadding: PaddingValues = NoPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    indication: Indication? = LocalIndication.current,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable (RowScope.() -> Unit),
) {
    val selected = state.selectedOption == value

    Radio(
        selected = selected,
        modifier = modifier,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        selectedColor = selectedColor,
        enabled = enabled,
        onSelectedChange = { state.selectedOption = value },
        shape = shape,
        borderColor = borderColor,
        borderWidth = borderWidth,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        indication = indication,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        content = content
    )
}

@Composable
fun Radio(
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    selectedColor: Color = Color.Unspecified,
    enabled: Boolean = true,
    shape: Shape = RectangleShape,
    borderColor: Color = Color.Unspecified,
    borderWidth: Dp = Dp.Unspecified,
    contentPadding: PaddingValues = NoPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    indication: Indication? = LocalIndication.current,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable (RowScope.() -> Unit),
) {
    Row(
        modifier = modifier.semantics(mergeDescendants = true) { } then buildModifier {
            add(
                Modifier
                    .clip(shape)
                    .background(backgroundColor)
                    .toggleable(
                        value = selected,
                        onValueChange = onSelectedChange,
                        role = Role.RadioButton,
                        enabled = enabled,
                        indication = indication,
                        interactionSource = interactionSource,
                    )
            )
            if (borderWidth > 0.dp && borderColor.isSpecified) {
                add(Modifier.border(borderWidth, borderColor, shape))
            }

            add(Modifier.padding(contentPadding))
        }, verticalAlignment = verticalAlignment, horizontalArrangement = horizontalArrangement
    ) {
        CompositionLocalProvider(LocalContentColor provides if (selected) contentColor else selectedColor) {
            this@Row.content()
        }
    }
}

