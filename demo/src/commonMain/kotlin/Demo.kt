package com.composeunstyled.demo

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import com.composeunstyled.Button
import com.composeunstyled.Icon
import com.composeunstyled.Text
import com.composeunstyled.currentWindowContainerSize
import com.composeunstyled.theme.buildTheme
import com.composeunstyled.theme.rememberColoredIndication

val DemoTheme = buildTheme {
    defaultIndication = rememberColoredIndication(
        hoveredColor = Color.LightGray.copy(alpha = 0.3f),
        pressedColor = Color.LightGray.copy(alpha = 0.5f),
        focusedColor = Color.LightGray.copy(alpha = 0.1f),
    )

//    defaultTextStyle = TextStyle(
//        fontFamily = FontFamily(Font(Res.font.Inter)),
//    )
}

@Composable
fun Demo(demoId: String? = null) {
    DemoTheme {
        if (demoId == null) {
            DemoSelection()
        } else {
            (availableDemos.firstOrNull { it.id == demoId }
                ?: error("Demo not found: $demoId"))
                .demo()
        }
    }
}

private data class DemoItem(val name: String, val id: String, val demo: @Composable () -> Unit)

private val availableComponents = listOf(
    DemoItem("Bottom Sheet", "bottom-sheet", { BottomSheetDemo() }),
    DemoItem("Bottom Sheet (Modal)", "modal-bottom-sheet", { ModalBottomSheetDemo() }),
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

private val availableDemos: List<DemoItem> = availableComponents + availableModifiers + DemoItem("Theme", "theme") {
    ThemeDemo()
}

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
    NavHost(
        navController = navController,
        startDestination = "home",
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300)) + fadeIn(tween(150))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300)) + fadeOut(tween(150))
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300)) + fadeIn(tween(150))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300)) + fadeOut(tween(150))
        }
    ) {
        composable("home") {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(Color(0xFFFAFAFAFA)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .systemBarsPadding()
                        .padding(16.dp)
                        .widthIn(max = 600.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Title("Theme")
                    OutlinedButton(
                        onClick = { navController.navigate("theme") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Theme", fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.height(8.dp))
                    Title("Components")
                    availableComponents.forEach { demo ->
                        OutlinedButton(
                            onClick = { navController.navigate(demo.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(demo.name, fontWeight = FontWeight.Medium)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Title("Modifiers")
                    availableModifiers.forEach { demo ->
                        OutlinedButton(
                            onClick = { navController.navigate(demo.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(demo.name, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        availableDemos
            .forEach { component ->
                composable(component.id) {
                    Column {
                        AppBar(onUpClick = { navController.navigateUp() }, title = component.name)
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
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
    Text(text, modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
private fun AppBar(onUpClick: () -> Unit, title: String) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .shadow(12.dp)
            .background(Color.White)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onUpClick,
            shape = CircleShape,
            contentPadding = PaddingValues(12.dp),
        ) {
            Icon(Lucide.ArrowLeft, contentDescription = "Go back")
        }
        Spacer(Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Medium, fontSize = 18.sp)
    }
}

@Composable
private fun OutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
    content: @Composable () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier.shadow(2.dp, shape),
        shape = shape,
        backgroundColor = Color.White,
        borderWidth = 1.dp,
        contentColor = Color(0xFF1A1A1A),
        contentPadding = PaddingValues(12.dp),
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
