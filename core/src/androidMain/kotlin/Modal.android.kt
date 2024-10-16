package com.composables.core

import android.os.Build
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentDialog
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import java.util.*

@Composable
internal actual fun Modal(
    protectNavBars: Boolean,
    onKeyEvent: (KeyEvent) -> Boolean,
    content: @Composable () -> Unit
) {
    val parentView = LocalView.current
    val context = LocalContext.current
    val composition = rememberCompositionContext()
    val id = rememberSaveable { UUID.randomUUID() }

    DisposableEffect(parentView) {
        val contentView: ComposeView

        val dialog = ComponentDialog(context, R.style.TranslucentDialog).apply {
            contentView = ComposeView(context).apply {
                setTag(androidx.compose.ui.R.id.compose_view_saveable_id_tag, "modal_$id")
                setParentCompositionContext(composition)
                setContent {
                    val localWindow = window
                        ?: error("Attempted to get the dialog's window without content. This should never happen and it's a bug in the library. Kindly open an issue with the steps to reproduce so that we fix it ASAP: https://github.com/composablehorizons/compose-unstyled/issues/new")
                    CompositionLocalProvider(LocalModalWindow provides localWindow) {
                        Box(Modifier.onKeyEvent(onKeyEvent)) {
                            BackHandler {
                                val backKeyDown = NativeKeyEvent(NativeKeyEvent.ACTION_DOWN, NativeKeyEvent.KEYCODE_BACK)
                                val backPress = KeyEvent(backKeyDown)
                                onKeyEvent(backPress)
                            }
                            content()
                        }
                    }
                }
            }

            setContentView(contentView)

            contentView.setViewTreeLifecycleOwner(parentView.findViewTreeLifecycleOwner())
            contentView.setViewTreeViewModelStoreOwner(parentView.findViewTreeViewModelStoreOwner())
            contentView.setViewTreeSavedStateRegistryOwner(parentView.findViewTreeSavedStateRegistryOwner())

            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }

        val window = requireNotNull(dialog.window) {
            "Tried to use a Modal without a window. Is your parent composable attached to an Activity?"
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        } else {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }

        if (protectNavBars) {
            window.navigationBarColor = Color.Black.copy(alpha = 0.33f).toArgb()
            WindowInsetsControllerCompat(window, contentView).isAppearanceLightNavigationBars = false
        }

        window.setDimAmount(0f)
        window.setWindowAnimations(-1)

        dialog.show()

        onDispose {
            contentView.disposeComposition()
            dialog.dismiss()
        }
    }
}

val LocalModalWindow = staticCompositionLocalOf<Window> {
    error("CompositionLocal LocalModalWindow not present – did you try to access the modal window without a modal visible on the screen?")
}