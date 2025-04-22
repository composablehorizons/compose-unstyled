package com.composeunstyled

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified

@Composable
fun Checkbox(
    checked: Boolean,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    contentColor: Color = LocalContentColor.current,
    enabled: Boolean = true,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    shape: Shape = RectangleShape,
    borderColor: Color = Color.Unspecified,
    borderWidth: Dp = 1.dp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    indication: Indication? = LocalIndication.current,
    contentDescription: String? = null,
    checkIcon: @Composable () -> Unit,
) {
    Box(
        modifier = modifier then buildModifier {
            if (borderColor.isSpecified && borderWidth.isSpecified) {
                add(Modifier.border(borderWidth, borderColor, shape))
            }
            add(Modifier.clip(shape).background(backgroundColor))

            if (onCheckedChange != null) {
                add(
                    Modifier.toggleable(
                        enabled = enabled,
                        value = checked,
                        interactionSource = interactionSource,
                        role = Role.Checkbox,
                        indication = indication,
                        onValueChange = onCheckedChange,
                    )
                )
            }
            if (contentDescription != null) {
                add(Modifier.semantics {
                    this.contentDescription = contentDescription
                }
                )
            }
        },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                checkIcon()
            }
        }
    }
}
