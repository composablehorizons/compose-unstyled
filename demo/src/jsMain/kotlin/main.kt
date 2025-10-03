package com.composeunstyled.demo

import androidx.compose.ui.window.CanvasBasedWindow
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import org.jetbrains.skiko.wasm.onWasmReady
import org.w3c.dom.url.URLSearchParams

fun main() {
    val iFrameParams = URLSearchParams(document.location?.search)
    val id = iFrameParams.get("id")
    onWasmReady {
        ComposeViewport {
            Demo(demoId = id)
        }
    }
}