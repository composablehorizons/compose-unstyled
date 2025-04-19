package com.composeunstyled.demo

import androidx.compose.ui.window.CanvasBasedWindow

fun main() {
    CanvasBasedWindow(canvasElementId = "ComposeTarget") { SheetDemo() }
}