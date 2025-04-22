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
                verticalAlignment = Alignment.CenterVertically,
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
            verticalAlignment = Alignment.CenterVertically,
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
