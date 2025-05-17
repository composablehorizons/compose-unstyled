package com.composeunstyled

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified

/**
 * A foundational component used to build text fields.
 *
 * For interactive preview & code examples, visit [Text Field Documentation](https://composeunstyled.com/textfield).
 *
 * ## Basic Example
 *
 * ```kotlin
 * var text by remember { mutableStateOf("") }
 *
 * TextField(
 *     value = text,
 *     onValueChange = { text = it },
 *     placeholder = "Enter text",
 *     shape = RoundedCornerShape(8.dp),
 *     backgroundColor = Color.White,
 *     borderColor = Color(0xFFE4E4E4),
 *     borderWidth = 1.dp
 * )
 * ```
 *
 * @param value The input text to be shown in the text field.
 * @param onValueChange The callback that is triggered when the input service updates the text.
 * @param editable Whether the text field is editable.
 * @param modifier Modifier to be applied to the text field.
 * @param contentPadding Padding values for the content.
 * @param leadingIcon Optional composable to be shown at the start of the text field.
 * @param trailingIcon Optional composable to be shown at the end of the text field.
 * @param placeholder The placeholder composable to be shown when the text field is empty.
 * @param contentColor The color of the text.
 * @param disabledColor The color of the text when the text field is disabled.
 * @param backgroundColor The background color of the text field.
 * @param borderWidth The width of the border.
 * @param borderColor The color of the border.
 * @param shape The shape of the text field.
 * @param textStyle The style of the text.
 * @param textAlign The alignment of the text.
 * @param fontSize The size of the text.
 * @param fontWeight The weight of the text.
 * @param fontFamily The font family of the text.
 * @param singleLine Whether the text field should be constrained to a single line.
 * @param minLines The minimum number of lines to be shown.
 * @param maxLines The maximum number of lines to be shown.
 * @param keyboardOptions The keyboard options for the text field.
 * @param keyboardActions The keyboard actions for the text field.
 * @param interactionSource The interaction source for the text field.
 * @param spacing The spacing between the leading icon, text, and trailing icon.
 * @param visualTransformation The visual transformation to be applied to the text.
 * @param verticalAlignment The vertical alignment of the content.
 */
@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    editable: Boolean = true,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = NoPadding,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    placeholder: String = "",
    contentColor: Color = LocalContentColor.current,
    disabledColor: Color = contentColor.copy(0.66f),
    backgroundColor: Color = Color.Unspecified,
    borderWidth: Dp = 1.dp,
    borderColor: Color = Color.Unspecified,
    shape: Shape = RectangleShape,
    textStyle: TextStyle = LocalTextStyle.current,
    textAlign: TextAlign = TextAlign.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    singleLine: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    spacing: Dp = 8.dp,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    val overrideContentColor = if (contentColor.isSpecified) {
        contentColor
    } else if (textStyle.color.isSpecified) {
        textStyle.color
    } else {
        LocalContentColor.current
    }

    val overrideTextAlign = if (textAlign != TextAlign.Unspecified) {
        textAlign
    } else textStyle.textAlign

    val overrideFontSize = if (fontSize != TextUnit.Unspecified) {
        fontSize
    } else textStyle.fontSize

    val overrideFontWeight = fontWeight ?: textStyle.fontWeight
    val overrideFontFamily = fontFamily ?: textStyle.fontFamily

    val overriddenStyle = textStyle.merge(
        fontWeight = overrideFontWeight,
        fontSize = overrideFontSize,
        fontFamily = overrideFontFamily,
        textAlign = overrideTextAlign,
        color = overrideContentColor
    )

    var wasEditable by remember { mutableStateOf(editable) }
    var textRange by remember { mutableStateOf(TextRange(value.length, value.length)) }
    var isFocusedOnIcon by remember { mutableStateOf(false) }

    LaunchedEffect(editable) {
        if (wasEditable.not() && editable) {
            // just changed to editable. select all text
            textRange = TextRange(0, value.length)
        }
        wasEditable = editable
    }

    if (editable) {
        val textFieldValue by derivedStateOf { TextFieldValue(value, textRange) }

        BasicTextField(
            value = textFieldValue,
            onValueChange = {
                if (isFocusedOnIcon.not()) {
                    onValueChange(it.text)
                    textRange = it.selection
                }
            },
            textStyle = overriddenStyle,
            modifier = modifier,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            interactionSource = interactionSource,
            visualTransformation = visualTransformation
        ) { innerTextField ->
            Row(
                verticalAlignment = verticalAlignment,
                modifier = buildModifier {
                    if (borderWidth.isSpecified && borderWidth > 0.dp && borderColor.isSpecified) {
                        add(Modifier.border(borderWidth, borderColor, shape))
                    }
                    add(Modifier.background(backgroundColor, shape))

                    if (editable.not()) {
                        add(Modifier.pointerHoverIcon(PointerIcon.Default))
                    }
                    add(Modifier.padding(contentPadding))
                },
                horizontalArrangement = Arrangement.spacedBy(spacing),
            ) {
                if (leadingIcon != null) {
                    Box(Modifier.onFocusChanged { isFocusedOnIcon = it.hasFocus }) {
                        leadingIcon()
                    }
                }
                Box(Modifier.weight(1f).semantics(mergeDescendants = true) { }) {
                    innerTextField()
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = overriddenStyle,
                            minLines = minLines,
                            maxLines = maxLines,
                            color = overrideContentColor.copy(alpha = 0.66f)
                        )
                    }
                }
                if (trailingIcon != null) {
                    Box(Modifier.onFocusChanged { isFocusedOnIcon = it.hasFocus }) {
                        trailingIcon()
                    }
                }
            }
        }
    } else {
        Row(
            verticalAlignment = verticalAlignment,
            modifier = modifier then buildModifier {
                if (borderWidth.isSpecified && borderWidth > 0.dp && borderColor.isSpecified) {
                    add(Modifier.border(borderWidth, borderColor, shape))
                }
                add(Modifier.background(backgroundColor, shape))
            }.widthIn(min = 2.dp) // width for the cursor blink
                .focusable(interactionSource = interactionSource).padding(contentPadding),
            horizontalArrangement = Arrangement.spacedBy(spacing),
        ) {
            if (leadingIcon != null) {
                leadingIcon()
            }
            Text(
                text = value.ifBlank { placeholder },
                style = overriddenStyle,
                color = disabledColor,
                modifier = Modifier.weight(1f),
                minLines = minLines,
                maxLines = maxLines,
            )
            if (trailingIcon != null) {
                trailingIcon()
            }
        }
    }
}
