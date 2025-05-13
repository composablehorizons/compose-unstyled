package com.composeunstyled.demo

import androidx.compose.ui.window.CanvasBasedWindow
import kotlinx.browser.document
import org.jetbrains.skiko.wasm.onWasmReady
import org.w3c.dom.url.URLSearchParams

fun main() {
    val iFrameParams = URLSearchParams(document.location?.search)
    val id = iFrameParams.get("id") ?: error("Required id is missing")
    onWasmReady {
        CanvasBasedWindow(canvasElementId = "ComposeTarget") { Demo(demoId = id) }
    }
}