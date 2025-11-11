package com.composables.core

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import com.composeunstyled.LocalContentColor

/**
 * A foundational component used to display icons.
 *
 * For interactive preview & code examples, visit [Icon Documentation](https://composeunstyled.com/icon).
 *
 * ## Basic Example
 *
 * ```kotlin
 * Icon(
 *     painter = painterResource("icon.xml"),
 *     contentDescription = "Settings",
 *     tint = Color.Black
 * )
 * ```
 *
 * @param painter The painter to draw the icon.
 * @param contentDescription The content description for accessibility.
 * @param modifier Modifier to be applied to the icon.
 * @param tint The tint color to be applied to the icon.
 */
@Composable
@Deprecated(
    "Use Icon from the com.composeunstyled package",
    ReplaceWith("com.composeunstyled.Icon(painter,contentDescription,modifier,tint)")
)
fun Icon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    val colorFilter = remember(tint) {
        if (tint == Color.Unspecified) null else ColorFilter.tint(tint)
    }
    Image(painter, contentDescription, modifier, colorFilter = colorFilter)
}

/**
 * A foundational component used to display icons.
 *
 * For interactive preview & code examples, visit [Icon Documentation](https://composeunstyled.com/icon).
 *
 * ## Basic Example
 *
 * ```kotlin
 * Icon(
 *     painter = painterResource("icon.xml"),
 *     contentDescription = "Settings",
 *     tint = Color.Black
 * )
 * ```
 *
 * @param imageBitmap The image bitmap to draw the icon.
 * @param contentDescription The content description for accessibility.
 * @param modifier Modifier to be applied to the icon.
 * @param tint The tint color to be applied to the icon.
 */
@Deprecated(
    "Use Icon from the com.composeunstyled package",
    ReplaceWith("com.composeunstyled.Icon(imageBitmap,contentDescription,modifier,tint)")
)
@Composable
fun Icon(
    imageBitmap: ImageBitmap,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    val colorFilter = remember(tint) {
        if (tint == Color.Unspecified) null else ColorFilter.tint(tint)
    }
    Image(imageBitmap, contentDescription, modifier, colorFilter = colorFilter)
}

/**
 * A foundational component used to display icons.
 *
 * For interactive preview & code examples, visit [Icon Documentation](https://composeunstyled.com/icon).
 *
 * ## Basic Example
 *
 * ```kotlin
 * Icon(
 *     painter = painterResource("icon.xml"),
 *     contentDescription = "Settings",
 *     tint = Color.Black
 * )
 * ```
 *
 * @param imageVector The image vector to draw the icon.
 * @param contentDescription The content description for accessibility.
 * @param modifier Modifier to be applied to the icon.
 * @param tint The tint color to be applied to the icon.
 */
@Composable
@Deprecated(
    "Use Icon from the com.composeunstyled package",
    ReplaceWith("com.composeunstyled.Icon(imageVector,contentDescription,modifier,tint)")
)
fun Icon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    val colorFilter = remember(tint) {
        if (tint == Color.Unspecified) null else ColorFilter.tint(tint)
    }
    Image(imageVector, contentDescription, modifier, colorFilter = colorFilter)
}
