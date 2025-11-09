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

    /**
     * Controls the default [androidx.compose.foundation.Indication] in [com.composeunstyled.theme.Theme]s created by the [com.composeunstyled.theme.buildTheme] function.
     *
     * When set to true, the default indication becomes [com.composeunstyled.theme.NoIndication]
     *
     * When false, the default indication is the current value of [androidx.compose.foundation.LocalIndication].
     *
     *
     * Will be enabled by default in 2.0
     */
    var noDefaultThemeIndication = false
}
