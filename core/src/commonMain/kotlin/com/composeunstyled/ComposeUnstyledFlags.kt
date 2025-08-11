package com.composeunstyled

object ComposeUnstyledFlags {
    /**
     * Changes the order in which the text colors are resolved in the [Text] and [TextField] components.
     *
     * When setting to true, the order becomes:
     *
     * [androidx.compose.ui.graphics.Color] -> [androidx.compose.ui.text.TextStyle] -> [LocalContentColor]
     *
     * Will be enabled by default in 2.0
     */
    var strictTextColorResolutionOrder = false
}