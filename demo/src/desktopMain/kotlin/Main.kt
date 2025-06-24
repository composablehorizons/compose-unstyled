package com.composeunstyled.demo

import androidx.compose.foundation.LocalIndication
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.singleWindowApplication

fun main() = singleWindowApplication(
    title = "Compose Unstyled"
) {
    CompositionLocalProvider(LocalIndication provides LightIndication) {
        Demo()
    }
}