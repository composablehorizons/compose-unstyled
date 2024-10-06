package com.composables.core.demo

import androidx.compose.ui.window.singleWindowApplication

fun main() = singleWindowApplication(
    title = "Compose Unstyled",
) {
    ScrollAreaDemo()
}
