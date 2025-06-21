package com.composables.core

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.composeunstyled.*

/**
 * An unstyled component for Compose Multiplatform that can be used to implement Dropdown Menus with the styling
 * of your choice. Fully accessible, supports keyboard navigation and open/close animations.
 *
 * For interactive preview & code examples, visit [Dropdown Menu Documentation](https://composeunstyled.com/dropdown-menu).
 *
 * ## Basic Example
 *
 * ```kotlin
 * val options = listOf("United States", "Greece", "Indonesia", "United Kingdom")
 * var selected by remember { mutableStateOf(0) }
 * val state = rememberMenuState(expanded = true)
 *
 * Column(Modifier.fillMaxSize()) {
 *     Menu(state, modifier = Modifier.align(Alignment.End)) {
 *         MenuButton(
 *             Modifier.clip(RoundedCornerShape(6.dp))
 *                 .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(6.dp))
 *         ) {
 *             Row(
 *                 modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
 *                 verticalAlignment = Alignment.CenterVertically,
 *             ) {
 *                 Text("Options", style = defaultTextStyle.copy(fontWeight = FontWeight(500)))
 *                 Spacer(Modifier.width(4.dp))
 *                 Image(ChevronDown, null)
 *             }
 *         }
 *
 *         MenuContent(
 *             modifier = Modifier.width(320.dp)
 *                 .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
 *                 .background(Color.White)
 *                 .padding(4.dp),
 *             exit = fadeOut()
 *         ) {
 *             options.forEachIndexed { index, option ->
 *                 MenuItem(
 *                     modifier = Modifier.clip(RoundedCornerShape(4.dp)),
 *                     onClick = { selected = index }
 *                 ) {
 *                     Text(option, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp))
 *                 }
 *             }
 *         }
 *     }
 *     Text("Selected = ${options[selected]}")
 * }
 * ```
 *
 * @param state The state object to be used to control the menu's expanded state.
 * @param modifier Modifier to be applied to the menu container.
 * @param content The content of the menu, typically containing a MenuButton and MenuContent.
 */
@Deprecated("Switch to DropdownMenu")
@Composable
fun Menu(state: MenuState, modifier: Modifier = Modifier, content: @Composable MenuScope.() -> Unit) {
    val scope = remember(state.expanded) { MenuScope(state) }

    Box(modifier.onKeyEvent { event ->
        if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
        when (event.key) {
            Key.DirectionDown -> {
                if (scope.menuState.expanded.not()) {
                    scope.menuState.expanded = true
                    true
                } else {
                    false
                }
            }

            else -> false
        }
    }) {
        state.currentFocusManager = LocalFocusManager.current
        scope.content()
    }
}

@Deprecated("Switch to DropdownMenu")
@Stable
 class MenuState(expanded: Boolean = false) {
    var expanded: Boolean by mutableStateOf(expanded)
    internal val menuFocusRequester = FocusRequester()
    internal var currentFocusManager by mutableStateOf<FocusManager?>(null)
    internal var hasMenuFocus by mutableStateOf(false)
}

@Composable
@Deprecated("Switch to DropdownMenu")
fun rememberMenuState(expanded: Boolean = false): MenuState {
    return remember { MenuState(expanded) }
}

/**
 * A button component that triggers the menu's expanded state when clicked.
 *
 * @param modifier Modifier to be applied to the button.
 * @param mutableInteractionSource The interaction source for the button.
 * @param indication The indication to be shown when the button is interacted with.
 * @param enabled Whether the button is enabled.
 * @param shape The shape of the button.
 * @param backgroundColor The background color of the button.
 * @param contentColor The color to apply to the contents of the button.
 * @param contentPadding Padding values for the content.
 * @param borderColor The color of the border.
 * @param borderWidth The width of the border.
 * @param horizontalArrangement The horizontal arrangement of the button's children.
 * @param verticalAlignment The vertical alignment of the button's children.
 * @param contents A composable function that defines the content of the button.
 */
@Deprecated("Switch to DropdownMenu")
@Composable
fun MenuScope.MenuButton(
    modifier: Modifier = Modifier,
    mutableInteractionSource: MutableInteractionSource? = null,
    indication: Indication = LocalIndication.current,
    enabled: Boolean = true,
    shape: Shape = RectangleShape,
    backgroundColor: Color = Color.Unspecified,
    contentColor: Color = LocalContentColor.current,
    contentPadding: PaddingValues = NoPadding,
    borderColor: Color = Color.Unspecified,
    borderWidth: Dp = 0.dp,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    contents: @Composable () -> Unit
) {
    Button(
        onClick = { menuState.expanded = menuState.expanded.not() },
        role = Role.DropdownList,
        enabled = enabled,
        contentColor = contentColor,
        contentPadding = contentPadding,
        borderColor = borderColor,
        borderWidth = borderWidth,
        interactionSource = mutableInteractionSource,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        indication = indication,
        modifier = modifier,
        shape = shape,
        backgroundColor = backgroundColor,
    ) {
        contents()
    }
}

@Deprecated("Switch to DropdownMenu")
@Stable
class MenuScope internal constructor(state: MenuState) {
    internal var menuState by mutableStateOf(state)
}


// Code modified from Material 3 DropdownMenu.kt
// https://github.com/JetBrains/compose-multiplatform-core/blob/e62838f496d592c019a3539669a9fbfd33928121/compose/material/material/src/commonMain/kotlin/androidx/compose/material/Menu.kt
@Immutable
internal data class MenuContentPositionProvider(val density: Density, val alignment: Alignment.Horizontal) :
    PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect, windowSize: IntSize, layoutDirection: LayoutDirection, popupContentSize: IntSize
    ): IntOffset { // The min margin above and below the menu, relative to the screen.
        // The content offset specified using the dropdown offset parameter.

        // Compute horizontal position.
        val toRight = anchorBounds.left
        val toLeft = anchorBounds.right - popupContentSize.width

        val toDisplayRight = windowSize.width - popupContentSize.width
        val toDisplayLeft = 0

        val x = (if (alignment == Alignment.Start) {
            sequenceOf(
                toRight, toLeft,
                // If the anchor gets outside of the window on the left, we want to position
                // toDisplayLeft for proximity to the anchor. Otherwise, toDisplayRight.
                if (anchorBounds.left >= 0) toDisplayRight else toDisplayLeft
            )
        } else if (alignment == Alignment.End) {
            sequenceOf(
                toLeft, toRight, // If the anchor gets outside of the window on the right, we want to position
                // toDisplayRight for proximity to the anchor. Otherwise, toDisplayLeft.
                if (anchorBounds.right <= windowSize.width) toDisplayLeft else toDisplayRight
            )
        } else { // middle
            sequenceOf(anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2)
        }).firstOrNull {
            it >= 0 && it + popupContentSize.width <= windowSize.width
        } ?: toLeft

        // Compute vertical position.
        val toBottom = maxOf(anchorBounds.bottom, 0)
        val toTop = anchorBounds.top - popupContentSize.height
        val toCenter = anchorBounds.top - popupContentSize.height / 2
        val toDisplayBottom = windowSize.height - popupContentSize.height
        val y = sequenceOf(toBottom, toTop, toCenter, toDisplayBottom).firstOrNull {
            it >= 0 && it + popupContentSize.height <= windowSize.height
        } ?: toTop

        return IntOffset(x, y)
    }
}

/**
 * The content container for the menu items. This composable handles the positioning and animation
 * of the menu content when it is expanded.
 *
 * @param modifier Modifier to be applied to the content container.
 * @param enter The enter transition for the content.
 * @param exit The exit transition for the content.
 * @param alignment The horizontal alignment of the content relative to the menu button.
 * @param contents A composable function that defines the content of the menu.
 */
@Deprecated("Switch to DropdownMenu")
@Composable
fun MenuScope.MenuContent(
    modifier: Modifier = Modifier,
    enter: EnterTransition = AppearInstantly,
    exit: ExitTransition = DisappearInstantly,
    alignment: Alignment.Horizontal = Alignment.Start,
    shape: Shape = RectangleShape,
    backgroundColor: Color = Color.Unspecified,
    contentColor: Color = LocalContentColor.current,
    contentPadding: PaddingValues = NoPadding,
    contents: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val positionProvider = MenuContentPositionProvider(density, alignment)
    val expandedState = remember { MutableTransitionState(false) }
    expandedState.targetState = menuState.expanded
    menuState.currentFocusManager = LocalFocusManager.current

    if (expandedState.currentState || expandedState.targetState || !expandedState.isIdle) {
        Popup(
            properties = PopupProperties(
                focusable = true, dismissOnBackPress = true, dismissOnClickOutside = true
            ),
            onDismissRequest = {
                menuState.expanded = false
                menuState.currentFocusManager?.clearFocus()
            },
            popupPositionProvider = positionProvider,
        ) {
            menuState.currentFocusManager = LocalFocusManager.current
            AnimatedVisibility(
                visibleState = expandedState,
                enter = enter,
                exit = exit,
                modifier = Modifier.onFocusChanged {
                    menuState.hasMenuFocus = it.hasFocus
                }.onKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) return@onKeyEvent false

                    return@onKeyEvent when (event.key) {
                        Key.DirectionDown -> {
                            menuState.currentFocusManager!!.moveFocus(FocusDirection.Next)
                            true
                        }

                        Key.DirectionUp -> {
                            menuState.currentFocusManager!!.moveFocus(FocusDirection.Previous)
                            true
                        }

                        Key.Escape -> {
                            menuState.expanded = false
                            true
                        }

                        else -> false
                    }
                }
            ) {
                Column(
                    modifier
                        .focusRequester(menuState.menuFocusRequester)
                        .clip(shape)
                        .background(backgroundColor)
                        .padding(contentPadding)
                ) {
                    LaunchedEffect(Unit) {
                        menuState.menuFocusRequester.requestFocus()
                    }
                    CompositionLocalProvider(LocalContentColor provides contentColor) {
                        contents()
                    }
                }
            }
        }
    }
}

/**
 * A menu item that can be clicked to perform an action and dismiss the menu.
 *
 * @param onClick The callback to be invoked when the menu item is clicked.
 * @param modifier Modifier to be applied to the menu item.
 * @param enabled Whether the menu item is enabled.
 * @param interactionSource The interaction source for the menu item.
 * @param indication The indication to be shown when the menu item is interacted with.
 * @param contentPadding Padding values for the content.
 * @param shape The shape of the menu item.
 * @param horizontalArrangement The horizontal arrangement of the menu item's children.
 * @param verticalAlignment The vertical alignment of the menu item's children.
 * @param contents A composable function that defines the content of the menu item.
 */
@Deprecated("Switch to DropdownMenu")
@Composable
fun MenuScope.MenuItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication = LocalIndication.current,
    contentPadding: PaddingValues = NoPadding,
    shape: Shape = RectangleShape,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    backgroundColor: Color = Color.Unspecified,
    contentColor: Color = LocalContentColor.current,
    contents: @Composable RowScope.() -> Unit
) {
    Button(
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        onClick = {
            onClick()
            menuState.expanded = false
            menuState.currentFocusManager?.clearFocus()
        },
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        indication = indication,
        shape = shape,
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        contents()
    }
}
