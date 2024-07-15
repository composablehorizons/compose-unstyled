package com.composables.core

import androidx.activity.ComponentDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import com.composables.core.R

@Composable
internal actual fun NoScrimDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val composition = rememberCompositionContext()
    DisposableEffect(Unit) {
        val contentView = ComposeView(context).apply {
            setParentCompositionContext(composition)
            setContent(content)
        }

        val dialog = ComponentDialog(
            context,
            R.style.TranslucentDialog
        ).apply {
            val window =
                requireNotNull(this.window) { "Tried to use a NoScrimDialog without a window. Is your parent composable attached to an Activity?" }
            WindowCompat.setDecorFitsSystemWindows(
                window,
                false
            )
            window.setDimAmount(0f)
            window.setWindowAnimations(-1)
            setContentView(contentView)
        }

        dialog.show()
        onDispose {
            contentView.disposeComposition()
            dialog.dismiss()
        }
    }
}
