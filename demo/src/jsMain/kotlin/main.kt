package com.composeunstyled.demo

import androidx.compose.foundation.LocalIndication
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.CanvasBasedWindow
import kotlinx.browser.document
import org.jetbrains.skiko.wasm.onWasmReady
import org.w3c.dom.url.URLSearchParams

fun main() {
    val iFrameParams = URLSearchParams(document.location?.search)
    val id = iFrameParams.get("id")
    onWasmReady {
        CanvasBasedWindow(canvasElementId = "ComposeTarget") {
            CompositionLocalProvider(LocalIndication provides NoIndication) {
                Demo(demoId = id)
            }
        }
    }
}