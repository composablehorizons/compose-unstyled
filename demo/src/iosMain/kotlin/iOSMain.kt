package com.composeunstyled.demo

import androidx.compose.foundation.LocalIndication
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    CompositionLocalProvider(LocalIndication provides LightIndication) {
        Demo()
    }
}