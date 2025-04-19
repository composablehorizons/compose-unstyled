package com.composeunstyled.demo

import androidx.compose.ui.window.CanvasBasedWindow
import org.jetbrains.skiko.wasm.onWasmReady

fun main() {
    onWasmReady {
        CanvasBasedWindow(canvasElementId = "ComposeTarget") { SliderDemo() }
    }
}