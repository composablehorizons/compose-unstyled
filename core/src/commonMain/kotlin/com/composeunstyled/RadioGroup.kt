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

/**
 * Creates a [RadioGroupState] that can be used to manually control a [RadioGroup].
 *
 * @param initialValue The initially selected option.
 */
@Composable
fun rememberRadioGroupState(initialValue: String): RadioGroupState {
    return remember {
        RadioGroupState(selectedOption = initialValue)
    }
}

class RadioGroupScope(val state: RadioGroupState)

/**
 * A foundational component used to build radio groups.
 *
 * For interactive preview & code examples, visit [Radio Group Documentation](https://composeunstyled.com/radiogroup).
 *
 * ## Basic Example
 *
 * ```kotlin
 * val radioGroupState = rememberRadioGroupState("option1")
 *
 * RadioGroup(state = radioGroupState, contentDescription = "Options") {
 *     Radio(
 *         value = "option1",
 *         shape = CircleShape,
 *         backgroundColor = Color.White,
 *         borderColor = Color(0xFFE4E4E4),
 *         borderWidth = 1.dp
 *     ) {
 *         Text("Option 1")
 *     }
 *     Radio(
 *         value = "option2",
 *         shape = CircleShape,
 *         backgroundColor = Color.White,
 *         borderColor = Color(0xFFE4E4E4),
 *         borderWidth = 1.dp
 *     ) {
 *         Text("Option 2")
 *     }
 * }
 * ```
 *
 * @param state The [RadioGroupState] that controls the selected option.
 * @param contentDescription The content description for accessibility.
 * @param modifier Modifier to be applied to the radio group.
 * @param content The content of the radio group, which should contain multiple [Radio] components.
 */
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

/**
 * A radio button component that can be selected within a [RadioGroup].
 *
 * This is a managed version of this component. It will automatically handle the state for you.
 *
 * A stateless version is also available within a different overload.
 *
 * @param value The value of this radio button.
 * @param modifier Modifier to be applied to the radio button.
 * @param backgroundColor The background color of the radio button.
 * @param contentColor The color of the content when selected.
 * @param selectedColor The color of the content when not selected.
 * @param enabled Whether the radio button is enabled.
 * @param shape The shape of the radio button.
 * @param borderColor The color of the border.
 * @param borderWidth The width of the border.
 * @param contentPadding Padding values for the content.
 * @param interactionSource The interaction source for the radio button.
 * @param indication The indication to be shown when the radio button is interacted with.
 * @param horizontalArrangement The horizontal arrangement of the content.
 * @param verticalAlignment The vertical alignment of the content.
 * @param content The content of the radio button.
 */
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
    interactionSource: MutableInteractionSource? = null,
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

/**
 * A stateless version of [Radio] component.
 *
 * There is also a managed version of this component, that will automatically manage its state within a [RadioGroup].
 *
 * @param selected Whether the radio button is selected.
 * @param onSelectedChange Callback when the selected state changes.
 * @param modifier Modifier to be applied to the radio button.
 * @param backgroundColor The background color of the radio button.
 * @param contentColor The color of the content when selected.
 * @param selectedColor The color of the content when not selected.
 * @param enabled Whether the radio button is enabled.
 * @param shape The shape of the radio button.
 * @param borderColor The color of the border.
 * @param borderWidth The width of the border.
 * @param contentPadding Padding values for the content.
 * @param interactionSource The interaction source for the radio button.
 * @param indication The indication to be shown when the radio button is interacted with.
 * @param horizontalArrangement The horizontal arrangement of the content.
 * @param verticalAlignment The vertical alignment of the content.
 * @param content The content of the radio button.
 */
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
    interactionSource: MutableInteractionSource? = null,
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

