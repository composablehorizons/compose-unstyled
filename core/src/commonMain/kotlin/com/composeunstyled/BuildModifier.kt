package com.composeunstyled

import androidx.compose.ui.Modifier

/**
 * Builds a new [Modifier] by combining all modifiers added to the [MutableList] via [builderAction],
 * concatenating them in order using [Modifier.then].
 *
 */
inline fun buildModifier(builderAction: MutableList<Modifier>.() -> Unit): Modifier {
    return buildList(builderAction).fold(Modifier as Modifier) { acc, modifier ->
        acc then modifier
    }
}