package com.composables.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Stable
public class MenuState(expanded: Boolean = false) {
    public var expanded: Boolean by mutableStateOf(expanded)
    internal val menuFocusRequester = FocusRequester()
    internal var currentFocusManager by mutableStateOf<FocusManager?>(null)
    internal var hasMenuFocus by mutableStateOf(false)
}


@Composable
public fun rememberMenuState(expanded: Boolean = false): MenuState {
    return remember { MenuState(expanded) }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
public fun Menu(
    state: MenuState = rememberMenuState(),
    modifier: Modifier = Modifier,
    contents: @Composable MenuScope.() -> Unit
) {
    val scope = remember(state.expanded) { MenuScope(state) }
    val coroutineScope = rememberCoroutineScope()
    var hasFocus by remember { mutableStateOf(false) }

    if (hasFocus) {
        KeyDownHandler { event ->
            when (event.key) {
                Key.DirectionDown -> {
                    if (scope.menuState.expanded.not()) {
                        scope.menuState.expanded = true
                        coroutineScope.launch {
                            // wait for the Popup to be displayed.
                            // There is no official API to wait for this to happen
                            delay(50)
                            state.menuFocusRequester.requestFocus()
                            state.currentFocusManager?.moveFocus(FocusDirection.Enter)
                        }
                        true
                    } else {
                        if (state.hasMenuFocus.not()) {
                            state.menuFocusRequester.requestFocus()
                            state.currentFocusManager?.moveFocus(FocusDirection.Enter)
                        } else
                            state.currentFocusManager?.moveFocus(FocusDirection.Next)
                        true
                    }
                }

                Key.DirectionUp -> {
                    state.currentFocusManager?.moveFocus(FocusDirection.Previous)
                    true
                }

                Key.Escape -> {
                    state.expanded = false
                    state.currentFocusManager?.clearFocus()
                    true
                }

                else -> false
            }
        }
    }
    Box(modifier.onFocusChanged {
        hasFocus = it.hasFocus
    }) {
        state.currentFocusManager = LocalFocusManager.current
        scope.contents()
    }
}


@Composable
public fun MenuScope.MenuButton(modifier: Modifier = Modifier, contents: @Composable () -> Unit) {
    Box(modifier = modifier.clickable(role = Role.DropdownList) {
        menuState.expanded = menuState.expanded.not()
    }) {
        contents()
    }
}

@Stable
public class MenuScope internal constructor(state: MenuState) {
    internal var menuState by mutableStateOf(state)
}


// Code taken from Material 3 DropdownMenu.kt
// https://github.com/JetBrains/compose-multiplatform-core/blob/e62838f496d592c019a3539669a9fbfd33928121/compose/material/material/src/commonMain/kotlin/androidx/compose/material/Menu.kt
@Immutable
internal data class DropdownMenuPositionProvider(
    val contentOffset: DpOffset,
    val density: Density,
    val onPositionCalculated: (IntRect, IntRect) -> Unit = { _, _ -> }
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        val MenuVerticalMargin = 0.dp
        // The min margin above and below the menu, relative to the screen.
        val verticalMargin = with(density) { MenuVerticalMargin.roundToPx() }
        // The content offset specified using the dropdown offset parameter.
        val contentOffsetX = with(density) { contentOffset.x.roundToPx() }
        val contentOffsetY = with(density) { contentOffset.y.roundToPx() }

        // Compute horizontal position.
        val toRight = anchorBounds.left + contentOffsetX
        val toLeft = anchorBounds.right - contentOffsetX - popupContentSize.width
        val toDisplayRight = windowSize.width - popupContentSize.width
        val toDisplayLeft = 0
        val x = if (layoutDirection == LayoutDirection.Ltr) {
            sequenceOf(
                toRight, toLeft,
                // If the anchor gets outside of the window on the left, we want to position
                // toDisplayLeft for proximity to the anchor. Otherwise, toDisplayRight.
                if (anchorBounds.left >= 0) toDisplayRight else toDisplayLeft
            )
        } else {
            sequenceOf(
                toLeft, toRight,
                // If the anchor gets outside of the window on the right, we want to position
                // toDisplayRight for proximity to the anchor. Otherwise, toDisplayLeft.
                if (anchorBounds.right <= windowSize.width) toDisplayLeft else toDisplayRight
            )
        }.firstOrNull {
            it >= 0 && it + popupContentSize.width <= windowSize.width
        } ?: toLeft

        // Compute vertical position.
        val toBottom = maxOf(anchorBounds.bottom + contentOffsetY, verticalMargin)
        val toTop = anchorBounds.top - contentOffsetY - popupContentSize.height
        val toCenter = anchorBounds.top - popupContentSize.height / 2
        val toDisplayBottom = windowSize.height - popupContentSize.height - verticalMargin
        val y = sequenceOf(toBottom, toTop, toCenter, toDisplayBottom).firstOrNull {
            it >= verticalMargin && it + popupContentSize.height <= windowSize.height - verticalMargin
        } ?: toTop

        onPositionCalculated(anchorBounds, IntRect(x, y, x + popupContentSize.width, y + popupContentSize.height))
        return IntOffset(x, y)
    }
}

@Composable
public fun MenuScope.MenuContent(
    modifier: Modifier = Modifier,
    showTransition: EnterTransition = fadeIn(animationSpec = tween(durationMillis = 0)),
    hideTransition: ExitTransition = fadeOut(animationSpec = tween(durationMillis = 0)),
    contents: @Composable () -> Unit
) {
    val offset = DpOffset.Zero
    val density = LocalDensity.current
    val popupPositionProvider = DropdownMenuPositionProvider(offset, density)
    val expandedState = remember { MutableTransitionState(false) }
    expandedState.targetState = menuState.expanded
    menuState.currentFocusManager = LocalFocusManager.current

    if (expandedState.currentState || expandedState.targetState || !expandedState.isIdle) {
        val groupRequester = remember { FocusRequester() }
        Popup(
            properties = PopupProperties(focusable = true, dismissOnBackPress = true, dismissOnClickOutside = true),
            onDismissRequest = {
                menuState.expanded = false
                menuState.currentFocusManager?.clearFocus()
            },
            popupPositionProvider = popupPositionProvider
        ) {
            menuState.currentFocusManager = LocalFocusManager.current
            AnimatedVisibility(
                visibleState = expandedState,
                enter = showTransition,
                exit = hideTransition,
                modifier = Modifier.focusRequester(groupRequester).onFocusChanged {
                    menuState.hasMenuFocus = it.hasFocus
                }
            ) {
                Column(modifier.focusRequester(menuState.menuFocusRequester)) {
                    contents()
                }
            }
        }
    }
}

@Composable
public fun MenuScope.MenuItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    contents: @Composable () -> Unit
) {
    Box(
        modifier
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                onClick = {
                    onClick()
                    menuState.expanded = false
                    menuState.currentFocusManager?.clearFocus()
                },
                indication = LocalIndication.current
            )
    ) {
        contents()
    }
}
