@file:OptIn(ExperimentalWasmJsInterop::class)

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.composeunstyled.demo.Demo

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    println("Start main")
    ComposeViewport {
        Demo()
    }
}
