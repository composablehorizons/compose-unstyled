package com.composeunstyled

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Button(
    onClick: () -> Unit,
    enabled: Boolean = true,
    shape: Shape = RectangleShape,
    backgroundColor: Color = Color.Unspecified,
    contentColor: Color = LocalContentColor.current,
    contentPadding: PaddingValues = NoPadding,
    borderColor: Color = Color.Unspecified,
    borderWidth: Dp = 0.dp,
    modifier: Modifier = Modifier,
    role: Role = Role.Button,
    indication: Indication? = LocalIndication.current,
    interactionSource: MutableInteractionSource? = null,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable (RowScope.() -> Unit)
) {
    Row(
        modifier = modifier then buildModifier {
            add(
                Modifier.clip(shape)
                    .background(backgroundColor)
                    .clickable(
                        onClick = onClick,
                        role = role,
                        enabled = enabled,
                        indication = indication,
                        interactionSource = interactionSource
                    )
            )
            if (borderWidth > 0.dp && borderColor.isSpecified) {
                add(Modifier.border(borderWidth, borderColor, shape))
            }
            if (enabled) {
                add(Modifier.pointerHoverIcon(PointerIcon.Default))
            }
            add(Modifier.padding(contentPadding))
        },
        verticalAlignment = verticalAlignment,
        horizontalArrangement = horizontalArrangement
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            content()
        }
    }
}
