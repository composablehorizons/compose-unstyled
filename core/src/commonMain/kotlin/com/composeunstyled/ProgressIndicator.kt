package com.composeunstyled

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.internal.IncreaseVerticalSemanticsBounds
import androidx.compose.runtime.*
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

@Composable
fun ProgressIndicatorScope.ProgressBar(
    shape: Shape = RectangleShape,
    color: Color = LocalContentColor.current
) {
    Box(Modifier.fillMaxWidth(progress).fillMaxHeight().background(color, shape))
}
