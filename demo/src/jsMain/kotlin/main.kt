package com.composeunstyled.demo

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.skiko.wasm.onWasmReady
import org.w3c.dom.HTMLStyleElement
import org.w3c.dom.url.URLSearchParams

@OptIn(ExperimentalWasmJsInterop::class)
fun main() {
    val iFrameParams = URLSearchParams(document.location?.search)
    val id = iFrameParams.get("id")
    onWasmReady {
        val composeTarget = document.getElementById("ComposeTarget") ?: error("No ComposeTarget")
        ComposeViewport(composeTarget) {
            LaunchedEffect(Unit) {
                composeTarget.shadowRoot?.let { shadowRoot ->
                    val style = document.createElement("style") as HTMLStyleElement
                    style.textContent = """
                            canvas {
                               outline: none;
                            }
                          """.trimIndent()
                    shadowRoot.appendChild(style)
                }
            }

            var demoId by remember { mutableStateOf(id) }

            LaunchedEffect(Unit) {
                window.asDynamic().loadDemo = { id: String ->
                    demoId = id
                }
            }


            Demo(demoId = demoId)
        }
    }
}