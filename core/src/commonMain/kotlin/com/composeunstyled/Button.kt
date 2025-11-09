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

/**
 * An accessible clickable component used to create buttons with the styling of your choice.
 *
 * For interactive preview & code examples, visit [Button Documentation](https://composeunstyled.com/button).
 *
 * ## Basic Example
 *
 * ```kotlin
 * Button(
 *     onClick = { /* TODO */ },
 *     backgroundColor = Color(0xFFFFFFFF),
 *     contentColor = Color(0xFF020817),
 *     contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
 *     shape = RoundedCornerShape(12.dp),
 * ) {
 *     Text("Submit")
 * }
 * ```
 *
 * @param onClick    The callback to be invoked when the button is clicked.
 * @param modifier    Modifier to be applied to the button.
 * @param enabled    Whether the button is enabled.
 * @param shape    The shape of the button.
 * @param backgroundColor    The background color of the button.
 * @param contentColor    The color to apply to the contents of the button.
 * @param contentPadding    Padding values for the content.
 * @param borderColor    The color of the border. Applied only if both borderColor is specified and borderWidth is > `0.dp`
 * @param borderWidth    The width of the border. Applied only if both borderColor is specified and borderWidth is > `0.dp`
 * @param role    The role of the button for accessibility purposes.
 * @param indication    The indication to be shown when the button is interacted with.
 * @param interactionSource    The interaction source for the button.
 * @param verticalAlignment The vertical alignment of the button's children.
 * @param horizontalArrangement The horizontal arrangement of the button's children.
 * @param content    A composable function that defines the content of the button.
 *
 */
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
            thisWillBreakTheCompilation()
            content()
        }
    }
}