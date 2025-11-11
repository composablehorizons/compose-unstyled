package com.composeunstyled

import android.os.Build
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentDialog
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.composeunstyled.primitives.R
import java.util.*

/**
 * Modals are the building blocks for components such as dialogs, alerts and modal bottom sheets.
 *
 * They create their own window and block interaction with the rest of the interface until removed from the composition.
 *
 * Modals in Android create their own [Window]. To access it use [LocalModalWindow].current.
 *
 * @param onKeyEvent Allows the caller to intercept [KeyEvent]s happening within the contents of the modal. Return `true` if you handled the event.
 * @param content The content of the modal
 */
@Composable
actual fun Modal(
    onKeyEvent: (KeyEvent) -> Boolean,
    content: @Composable () -> Unit
) {
    val parentView = LocalView.current
    val context = LocalContext.current
    val composition = rememberCompositionContext()
    val id = rememberSaveable { UUID.randomUUID() }

    DisposableEffect(parentView) {
        val contentView: ComposeView

        val dialog = ComponentDialog(context, R.style.Modal).apply {
            contentView = ComposeView(context).apply {
                setTag(androidx.compose.ui.R.id.compose_view_saveable_id_tag, "modal_$id")
                setParentCompositionContext(composition)
                setContent {
                    val localWindow = window
                        ?: error("Attempted to get the dialog's window without content. This should never happen and it's a bug in the library. Kindly open an issue with the steps to reproduce so that we fix it ASAP: https://github.com/composablehorizons/compose-unstyled/issues/new")

                    Box(
                        Modifier
                            .fillMaxSize()
                            .onKeyEvent(onKeyEvent)
                    ) {
                        CompositionLocalProvider(LocalModalWindow provides localWindow) {
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
            @Suppress("DEPRECATION")
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }

        dialog.show()

        onDispose {
            contentView.disposeComposition()
            dialog.dismiss()
        }
    }
}

/**
 * The CompositionLocal containing the current [Window].
 */
val LocalModalWindow = staticCompositionLocalOf<Window> {
    error("CompositionLocal LocalModalWindow not present – did you try to access the modal window without a modal visible on the screen?")
}