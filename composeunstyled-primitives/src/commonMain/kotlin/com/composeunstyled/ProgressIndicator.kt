package com.composeunstyled

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import com.composables.core.androidx.annotation.FloatRange

class ProgressIndicatorScope {
    var progress by mutableStateOf(0f)
        internal set
}

/**
 * A foundational component used to build progress indicators.
 *
 * For interactive preview & code examples, visit [Progress Indicator Documentation](https://composeunstyled.com/progressindicator).
 *
 * ## Basic Example
 *
 * ```kotlin
 * ProgressIndicator(
 *     progress = 0.5f,
 *     shape = RoundedCornerShape(4.dp),
 *     backgroundColor = Color(0xFFE4E4E4),
 *     contentColor = Color(0xFF6699FF)
 * ) {
 *     ProgressBar()
 * }
 * ```
 *
 * @param progress The progress value between 0.0 and 1.0.
 * @param modifier Modifier to be applied to the progress indicator.
 * @param shape The shape of the progress indicator.
 * @param backgroundColor The background color of the progress indicator.
 * @param contentColor The color of the content.
 * @param content The content of the progress indicator. For a batteries included component see [ProgressBar].
 */
@Composable
fun ProgressIndicator(
    @FloatRange(from = 0.0, to = 1.0) progress: Float,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    backgroundColor: Color = Color.Unspecified,
    contentColor: Color = LocalContentColor.current,
    content: @Composable ProgressIndicatorScope.() -> Unit
) {
    val scope = remember { ProgressIndicatorScope() }
    SideEffect { scope.progress = progress }
    Box(
        modifier
            .then(IncreaseVerticalSemanticsBounds)
            .progressSemantics(progress, 0f..1f)
            .clip(shape)
            .background(backgroundColor)
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            with(scope) {
                content()
            }
        }
    }
}

/**
 * A foundational component used to build progress indicators.
 *
 * For interactive preview & code examples, visit [Progress Indicator Documentation](https://composeunstyled.com/progressindicator).
 *
 * ## Basic Example
 *
 * ```kotlin
 * ProgressIndicator(
 *     progress = 0.5f,
 *     shape = RoundedCornerShape(4.dp),
 *     backgroundColor = Color(0xFFE4E4E4),
 *     contentColor = Color(0xFF6699FF)
 * ) {
 *     ProgressBar()
 * }
 * ```
 *
 * @param modifier Modifier to be applied to the progress indicator.
 * @param shape The shape of the progress indicator.
 * @param backgroundColor The background color of the progress indicator.
 * @param contentColor The color of the content.
 * @param content The content of the progress indicator.
 */
@Composable
fun ProgressIndicator(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    backgroundColor: Color = Color.Unspecified,
    contentColor: Color = LocalContentColor.current,
    content: @Composable () -> Unit
) {
    Box(
        modifier
            .then(IncreaseVerticalSemanticsBounds)
            .progressSemantics()
            .clip(shape)
            .background(backgroundColor)
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            content()
        }
    }
}

/**
 * A progress bar component that automatically fills the width of its parent based on the progress value.
 *
 * @param shape The shape of the progress bar.
 * @param color The color of the progress bar.
 */
@Composable
fun ProgressIndicatorScope.ProgressBar(
    shape: Shape = RectangleShape,
    color: Color = LocalContentColor.current
) {
    Box(Modifier.fillMaxWidth(progress).fillMaxHeight().background(color, shape))
}
