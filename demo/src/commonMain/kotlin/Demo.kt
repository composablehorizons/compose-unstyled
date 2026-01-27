package com.composeunstyled.demo

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import com.composeunstyled.*
import com.composeunstyled.platformtheme.*
import com.composeunstyled.theme.Theme

private val DemoTheme = buildPlatformTheme(
    webFontOptions = WebFontOptions(
        emojiVariant = EmojiVariant.None
    )
)

@Composable
fun Demo(demoId: String? = null) {
    DemoTheme {
        Box(Modifier.fillMaxSize().background(Color(0xFFFAFAFA))) {
            if (demoId == null) {
                DemoSelection()
            } else {
                (availableDemos.firstOrNull { it.id == demoId }
                    ?: error("Demo not found: $demoId"))
                    .demo()
            }
        }
    }
}

private data class DemoItem(val name: String, val id: String, val demo: @Composable () -> Unit)

private val availableComponents = listOf(
    DemoItem("Bottom Sheet", "bottom-sheet", { BottomSheetDemo() }),
    DemoItem("Bottom Sheet (Modal)", "modal-bottom-sheet", { ModalBottomSheetDemo() }),
    DemoItem("Top Bottom Sheet (Modal)", "modal-bottom-sheet-top", { TopModalBottomSheetDemo() }),
    DemoItem("Button", "button", { ButtonDemo() }),
    DemoItem("Checkbox", "checkbox", { CheckboxDemo() }),
    DemoItem("Checkbox (TriState)", "tristatecheckbox", { TriStateCheckboxDemo() }),
    DemoItem("Dialog", "dialog", { DialogDemo() }),
    DemoItem("Disclosure", "disclosure", { DisclosureDemo() }),
    DemoItem("Dropdown Menu", "dropdown-menu", { DropdownMenuDemo() }),
    DemoItem("Icon", "icon", { IconDemo() }),
    DemoItem("Progress Indicator", "progressindicator", { ProgressIndicatorDemo() }),
    DemoItem("Radio Group", "radiogroup", { RadioGroupDemo() }),
    DemoItem("Scroll Area", "scrollarea", { ScrollAreaDemo() }),
    DemoItem("Separators", "separators", { SeparatorsDemo() }),
    DemoItem("Slider", "slider", { SliderDemo() }),
    DemoItem("Tab Group", "tabgroup", { TabGroupDemo() }),
    DemoItem("Text", "text", { TextDemo() }),
    DemoItem("Tooltip", "tooltip", { TooltipDemo() }),
    DemoItem("Text Field", "textfield", { TextFieldDemo() }),
    DemoItem("Toggle Switch", "toggleswitch", { ToggleSwitchDemo() })
)

private val availableModifiers = listOf(
    DemoItem("Outline Basic", "outline-basic", { OutlineBasicDemo() }),
    DemoItem("Outline Width", "outline-width", { OutlineWidthDemo() }),
    DemoItem("Outline Shape", "outline-shape", { OutlineShapeDemo() }),
    DemoItem("Outline Offset", "outline-offset", { OutlineOffsetDemo() }),
    DemoItem("Outline Color", "outline-color", { OutlineColorDemo() }),
    DemoItem("Focus Ring Basic", "focus-ring-basic", { FocusRingBasicDemo() }),
    DemoItem("Focus Ring Width", "focus-ring-width", { FocusRingWidthDemo() }),
    DemoItem("Focus Ring Shape", "focus-ring-shape", { FocusRingShapeDemo() }),
    DemoItem("Focus Ring Offset", "focus-ring-offset", { FocusRingOffsetDemo() }),
    DemoItem("Focus Ring Color", "focus-ring-color", { FocusRingColorDemo() }),
).map { it ->
    it.copy(demo = {
        ModifierDemo {
            it.demo()
        }
    })
}

private val themingDemos = listOf(
    DemoItem("Theming", "theme") { ThemingDemo() },
    DemoItem("Platform Theme", "platform-theme") { PlatformThemeDemo() },
)
private val availableDemos: List<DemoItem> =
    availableComponents + availableModifiers + themingDemos

@Composable
fun ModifierDemo(content: @Composable () -> Unit) {
    val size = currentWindowContainerSize()
    val isWide = size.width > 600.dp
    val spacedBy = if (isWide) 60.dp else 30.dp
    Flex(
        modifier = Modifier.fillMaxSize().background(Color.White),
        orientation = if (isWide) FlexOrientation.Horizontal else FlexOrientation.Vertical,
        spacedBy = spacedBy
    ) {
        content()
    }
}

@Composable
private fun DemoSelection() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home", enterTransition = {
        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300)) + fadeIn(tween(150))
    }, exitTransition = {
        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300)) + fadeOut(tween(150))
    }, popEnterTransition = {
        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300)) + fadeIn(tween(150))
    }, popExitTransition = {
        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300)) + fadeOut(tween(150))
    }) {
        composable("home") {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Column(
                    Modifier.verticalScroll(rememberScrollState()).systemBarsPadding().padding(16.dp)
                        .widthIn(max = 600.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Title("Theme")
                    themingDemos.forEach { demo ->
                        OutlinedButton(
                            onClick = { navController.navigate(demo.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(demo.name)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Title("Components")
                    availableComponents.forEach { demo ->
                        OutlinedButton(
                            onClick = { navController.navigate(demo.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(demo.name)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Title("Modifiers")
                    availableModifiers.forEach { demo ->
                        OutlinedButton(
                            onClick = { navController.navigate(demo.id) }, modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(demo.name)
                        }
                    }
                }
            }
        }

        availableDemos.forEach { component ->
            composable(component.id) {
                Column {
                    AppBar(onUpClick = { navController.navigateUp() }, title = component.name)
                    Box(
                        Modifier.fillMaxWidth().weight(1f)
                    ) {
                        component.demo()
                    }
                }
            }
        }
    }
}

@Composable
private fun Title(text: String) {
    Text(text, modifier = Modifier.padding(bottom = 8.dp), style = Theme[textStyles][text5])
}

@Composable
private fun AppBar(onUpClick: () -> Unit, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp)
            .background(Color.White)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        Button(
            onClick = onUpClick,
            interactionSource = interactionSource,
            shape = CircleShape,
            contentPadding = PaddingValues(12.dp),
            indication = Theme[indications][dimmed],
            modifier = Modifier
                .focusRing(interactionSource, 1.dp, Color.Blue, CircleShape)
        ) {
            Icon(Lucide.ArrowLeft, contentDescription = "Go back")
        }
        Spacer(Modifier.width(8.dp))
        Text(title, style = Theme[textStyles][text5])
    }
}

@Composable
private fun OutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .focusRing(interactionSource, 1.dp, Color.Blue, RoundedCornerShape(8.dp))
            .shadow(2.dp, RoundedCornerShape(8.dp))
            .interactiveSize(Theme[interactiveSizes][sizeDefault])
            .outline(1.dp, Color.Black.copy(0.1f)),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color.White,
        contentColor = Color(0xFF1A1A1A),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        indication = Theme[indications][dimmed]
    ) {
        content()
    }
}

sealed interface FlexOrientation {
    object Horizontal : FlexOrientation
    object Vertical : FlexOrientation
}

@Composable
private fun Flex(
    orientation: FlexOrientation = FlexOrientation.Horizontal,
    spacedBy: Dp = 0.dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val movableContent = remember { movableContentOf { content() } }

    if (orientation == FlexOrientation.Vertical) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(spacedBy, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            movableContent()
        }
    } else {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(spacedBy, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            movableContent()
        }
    }
}
