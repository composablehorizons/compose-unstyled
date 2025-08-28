package com.composeunstyled

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Stack places its children either horizontally or vertically depending on the given [orientation].
 *
 * In other words, it works as a [Row] or [Column] depending on the [orientation].
 *
 * @param mainAxisArrangement controls the arrangement of the stack's children on the main axis. ie if the [orientation] is Horizontal, this will control the children on the **horizontal** axis.
 * @param crossAxisAlignment controls the arrangement of the stack's children on the opposite axis of [orientation]. ie if the [orientation] is Horizontal, this will control the children on the **vertical** axis.
 * @param spacing Spacing between the children. **This does nothing if** the given [mainAxisArrangement] is [MainAxisArrangement.SpaceEvenly], [MainAxisArrangement.SpaceBetween] or [MainAxisArrangement.SpaceAround]
 */
@Composable
fun Stack(
    modifier: Modifier = Modifier,
    orientation: StackOrientation = StackOrientation.Horizontal,
    mainAxisArrangement: MainAxisArrangement = MainAxisArrangement.Start,
    crossAxisAlignment: CrossAxisAlignment = CrossAxisAlignment.Start,
    spacing: Dp = 0.dp,
    content: @Composable StackScope.() -> Unit,
) {
    when (orientation) {
        StackOrientation.Vertical -> {
            Column(
                modifier = modifier,
                verticalArrangement = rememberVerticalArrangement(mainAxisArrangement, spacing),
                horizontalAlignment = rememberHorizontalAlignment(crossAxisAlignment)
            ) {
                val scope = remember(this) { ColumnStackScope(this) }
                scope.content()
            }
        }

        StackOrientation.Horizontal -> {
            Row(
                modifier = modifier,
                horizontalArrangement = rememberHorizontalArrangement(mainAxisArrangement, spacing),
                verticalAlignment = rememberVerticalAlignment(crossAxisAlignment)
            ) {
                val scope = remember(this) { RowStackScope(this) }
                scope.content()
            }
        }
    }
}

sealed class MainAxisArrangement {
    data object Start : MainAxisArrangement()
    data object Center : MainAxisArrangement()
    data object End : MainAxisArrangement()

    data object SpaceEvenly : MainAxisArrangement()
    data object SpaceBetween : MainAxisArrangement()
    data object SpaceAround : MainAxisArrangement()
}

sealed class CrossAxisAlignment {
    data object Start : CrossAxisAlignment()
    data object Center : CrossAxisAlignment()
    data object End : CrossAxisAlignment()
}

enum class StackOrientation { Vertical, Horizontal }

@Composable
private fun rememberHorizontalArrangement(mainAxisAlignment: MainAxisArrangement, spacing: Dp): Arrangement.Horizontal {
    return remember(mainAxisAlignment, spacing) {
        mainAxisToHorizontalArrangement(mainAxisAlignment, spacing)
    }
}

@Composable
private fun rememberVerticalArrangement(mainAxisAlignment: MainAxisArrangement, spacing: Dp): Arrangement.Vertical {
    return remember(mainAxisAlignment, spacing) {
        mainAxisToVerticalArrangement(mainAxisAlignment, spacing)
    }
}

private fun mainAxisToHorizontalArrangement(mainAxisAlignment: MainAxisArrangement, spacing: Dp): Arrangement.Horizontal {
    return when (mainAxisAlignment) {
        MainAxisArrangement.Start -> Arrangement.spacedBy(spacing)
        MainAxisArrangement.Center -> Arrangement.spacedBy(spacing, Alignment.CenterHorizontally)
        MainAxisArrangement.End -> Arrangement.spacedBy(spacing, Alignment.End)
        MainAxisArrangement.SpaceEvenly -> Arrangement.SpaceEvenly
        MainAxisArrangement.SpaceBetween -> Arrangement.SpaceBetween
        MainAxisArrangement.SpaceAround -> Arrangement.SpaceAround
    }
}

private fun mainAxisToVerticalArrangement(mainAxisAlignment: MainAxisArrangement, spacing: Dp): Arrangement.Vertical {
    return when (mainAxisAlignment) {
        MainAxisArrangement.Start -> Arrangement.spacedBy(spacing)
        MainAxisArrangement.Center -> Arrangement.spacedBy(spacing, Alignment.CenterVertically)
        MainAxisArrangement.End -> Arrangement.spacedBy(spacing, Alignment.Bottom)
        MainAxisArrangement.SpaceEvenly -> Arrangement.SpaceEvenly
        MainAxisArrangement.SpaceBetween -> Arrangement.SpaceBetween
        MainAxisArrangement.SpaceAround -> Arrangement.SpaceAround
    }
}

@Composable
private fun rememberHorizontalAlignment(crossAxisAlignment: CrossAxisAlignment): Alignment.Horizontal {
    return remember(crossAxisAlignment) {
        crossAxisToHorizontalAlignment(crossAxisAlignment)
    }
}

@Composable
private fun rememberVerticalAlignment(crossAxisAlignment: CrossAxisAlignment): Alignment.Vertical {
    return remember(crossAxisAlignment) {
        crossAxisToVerticalAlignment(crossAxisAlignment)
    }
}

private fun crossAxisToHorizontalAlignment(crossAxisAlignment: CrossAxisAlignment): Alignment.Horizontal {
    return when (crossAxisAlignment) {
        CrossAxisAlignment.Start -> Alignment.Start
        CrossAxisAlignment.Center -> Alignment.CenterHorizontally
        CrossAxisAlignment.End -> Alignment.End
    }
}

private fun crossAxisToVerticalAlignment(crossAxisAlignment: CrossAxisAlignment): Alignment.Vertical {
    return when (crossAxisAlignment) {
        CrossAxisAlignment.Start -> Alignment.Top
        CrossAxisAlignment.Center -> Alignment.CenterVertically
        CrossAxisAlignment.End -> Alignment.Bottom
    }
}

interface StackScope {
    fun Modifier.weight(weight: Float, fill: Boolean): Modifier
}

private class RowStackScope(private val rowScope: RowScope) : StackScope {
    override fun Modifier.weight(weight: Float, fill: Boolean): Modifier {
        return with(rowScope) { this@weight.weight(weight, fill) }
    }
}

private class ColumnStackScope(private val columnScope: ColumnScope) : StackScope {
    override fun Modifier.weight(weight: Float, fill: Boolean): Modifier {
        return with(columnScope) { this@weight.weight(weight, fill) }
    }
}
