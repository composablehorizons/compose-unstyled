@file:OptIn(ExperimentalWasmJsInterop::class)

import androidx.compose.ui.window.ComposeViewport
import com.composeunstyled.demo.Demo

fun main() {
    ComposeViewport {
        Demo()
    }
}
