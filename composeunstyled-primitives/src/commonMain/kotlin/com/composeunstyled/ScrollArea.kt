package com.composeunstyled

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.composables.core.ScrollAreaScope
import com.composables.core.ScrollAreaState
import com.composables.core.ScrollbarScope
import com.composables.core.ThumbVisibility
import com.composables.core.LazyGridScrollAreaScrollAreaState
import com.composables.core.LazyListScrollAreaState
import com.composables.core.ScrollStateScrollAreaState
import com.composables.core.HorizontalScrollbar as CoreHorizontalScrollbar
import com.composables.core.Thumb as CoreThumb
import com.composables.core.VerticalScrollbar as CoreVerticalScrollbar

@Composable
fun rememberScrollAreaState(scrollState: ScrollState): ScrollAreaState = remember(scrollState) {
    ScrollStateScrollAreaState(scrollState)
}

@Composable
fun rememberScrollAreaState(lazyListState: LazyListState): ScrollAreaState = remember(lazyListState) {
    LazyListScrollAreaState(lazyListState)
}

@Composable
fun rememberScrollAreaState(lazyGridState: LazyGridState): ScrollAreaState = remember(lazyGridState) {
    LazyGridScrollAreaScrollAreaState(lazyGridState)
}

@Deprecated("Use UnstyledScrollArea instead", ReplaceWith("UnstyledScrollArea(state, modifier, content)"))
@Composable
fun ScrollArea(
    state: ScrollAreaState,
    modifier: Modifier = Modifier,
    content: @Composable ScrollAreaScope.() -> Unit
) {
    UnstyledScrollArea(state, modifier, content)
}

@Composable
fun UnstyledScrollArea(
    state: ScrollAreaState,
    modifier: Modifier = Modifier,
    content: @Composable ScrollAreaScope.() -> Unit
) {
    Box(modifier) {
        val boxScope = this
        val scrollAreaScope = remember {
            ScrollAreaScope(boxScope, state)
        }
        scrollAreaScope.content()
    }
}

@Deprecated(
    "Use UnstyledVerticalScrollbar instead",
    ReplaceWith("UnstyledVerticalScrollbar(modifier, enabled, interactionSource, reverseLayout, thumb)")
)
@Composable
fun ScrollAreaScope.VerticalScrollbar(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    reverseLayout: Boolean = false,
    thumb: @Composable (ScrollbarScope.() -> Unit),
) {
    UnstyledVerticalScrollbar(modifier, enabled, interactionSource, reverseLayout, thumb)
}

@Composable
fun ScrollAreaScope.UnstyledVerticalScrollbar(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    reverseLayout: Boolean = false,
    thumb: @Composable (ScrollbarScope.() -> Unit),
) {
    CoreVerticalScrollbar(
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        reverseLayout = reverseLayout,
        thumb = thumb
    )
}

@Deprecated(
    "Use UnstyledHorizontalScrollbar instead",
    ReplaceWith("UnstyledHorizontalScrollbar(modifier, enabled, interactionSource, reverseLayout2, thumb)")
)
@Composable
fun ScrollAreaScope.HorizontalScrollbar(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    reverseLayout2: Boolean = false,
    thumb: @Composable (ScrollbarScope.() -> Unit),
) {
    UnstyledHorizontalScrollbar(modifier, enabled, interactionSource, reverseLayout2, thumb)
}

@Composable
fun ScrollAreaScope.UnstyledHorizontalScrollbar(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    reverseLayout2: Boolean = false,
    thumb: @Composable (ScrollbarScope.() -> Unit),
) {
    CoreHorizontalScrollbar(
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        reverseLayout2 = reverseLayout2,
        thumb = thumb
    )
}

@Deprecated("Use UnstyledThumb instead", ReplaceWith("UnstyledThumb(modifier, thumbVisibility, enabled)"))
@Composable
fun ScrollbarScope.Thumb(
    modifier: Modifier = Modifier,
    thumbVisibility: ThumbVisibility = ThumbVisibility.AlwaysVisible,
    enabled: Boolean = true,
) {
    UnstyledThumb(modifier, thumbVisibility, enabled)
}

@Composable
fun ScrollbarScope.UnstyledThumb(
    modifier: Modifier = Modifier,
    thumbVisibility: ThumbVisibility = ThumbVisibility.AlwaysVisible,
    enabled: Boolean = true,
) {
    CoreThumb(
        modifier = modifier,
        thumbVisibility = thumbVisibility,
        enabled = enabled
    )
}
