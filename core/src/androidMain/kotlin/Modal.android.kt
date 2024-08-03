package com.composables.core

import android.view.WindowManager
import androidx.activity.ComponentDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
import java.util.UUID

@Composable
internal actual fun Modal(
    protectNavBars: Boolean,
    content: @Composable () -> Unit
) {
    val parentView = LocalView.current
    val context = LocalContext.current
    val composition = rememberCompositionContext()
    val id = rememberSaveable { UUID.randomUUID() }

    DisposableEffect(parentView) {
        val contentView = ComposeView(context).apply {
            setTag(androidx.compose.ui.R.id.compose_view_saveable_id_tag, "modal_$id")
            setParentCompositionContext(composition)
            setContent(content)
        }

        val dialog = ComponentDialog(context, R.style.TranslucentDialog).apply {
            val window =
                requireNotNull(this.window) { "Tried to use a Modal without a window. Is your parent composable attached to an Activity?" }
            WindowCompat.setDecorFitsSystemWindows(window, false)
            @Suppress("DEPRECATION") // applying View.OnApplyWindowInsetsListener doesn't seem to work
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

            window.setDimAmount(0f)
            window.setWindowAnimations(-1)

            setContentView(contentView)

            contentView.setViewTreeLifecycleOwner(parentView.findViewTreeLifecycleOwner())
            contentView.setViewTreeViewModelStoreOwner(parentView.findViewTreeViewModelStoreOwner())
            contentView.setViewTreeSavedStateRegistryOwner(parentView.findViewTreeSavedStateRegistryOwner())

            setCancelable(false)
            setCanceledOnTouchOutside(false)

            if (protectNavBars) {
                window.navigationBarColor = Color.Black.copy(alpha = 0.33f).toArgb()
                WindowInsetsControllerCompat(window, contentView).isAppearanceLightNavigationBars = false
            }
        }

        dialog.show()

        onDispose {
            contentView.disposeComposition()
            dialog.dismiss()
        }
    }
}
