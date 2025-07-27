package com.composeunstyled.demo

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.composables.core.Icon
import com.composeunstyled.Button
import com.composeunstyled.Text
import com.composeunstyled.theme.buildTheme
import com.composeunstyled.theme.rememberColoredIndication
import composeunstyled.demo.generated.resources.Inter
import composeunstyled.demo.generated.resources.Res
import org.jetbrains.compose.resources.Font

val DemoTheme = buildTheme {
    defaultIndication = rememberColoredIndication(
        hoveredColor = Color.White.copy(alpha = 0.3f),
        pressedColor = Color.White.copy(alpha = 0.5f),
        focusedColor = Color.Black.copy(alpha = 0.1f),
    )

    defaultTextStyle = TextStyle(
        fontFamily = FontFamily(Font(Res.font.Inter)),
    )
}

@Composable
fun Demo(demoId: String? = null) {
    DemoTheme {
        if (demoId == null) {
            DemoSelection()
        } else {
            availableComponents.first { it.id == demoId }.demo()
        }
    }
}

private data class AvailableComponent(val name: String, val id: String, val demo: @Composable () -> Unit)

private val availableComponents = listOf(
    AvailableComponent("Button", "button", { ButtonDemo() }),
    AvailableComponent("Bottom Sheet", "bottom-sheet", { BottomSheetDemo() }),
    AvailableComponent("Modal Bottom Sheet", "modal-bottom-sheet", { ModalBottomSheetDemo() }),
    AvailableComponent("Checkbox", "checkbox", { CheckboxDemo() }),
    AvailableComponent("TriState Checkbox", "checkbox", { TriStateCheckboxDemo() }),
    AvailableComponent("Dialog", "dialog", { DialogDemo() }),
    AvailableComponent("Disclosure", "disclosure", { DisclosureDemo() }),
    AvailableComponent("Dropdown Menu", "dropdown-menu", { DropdownMenuDemo() }),
    AvailableComponent("Icon", "icon", { IconDemo() }),
    AvailableComponent("Progress Indicator", "progressindicator", { ProgressIndicatorDemo() }),
    AvailableComponent("Radio Group", "radiogroup", { RadioGroupDemo() }),
    AvailableComponent("Scroll Area", "scrollarea", { ScrollAreaDemo() }),
    AvailableComponent("Separators", "separators", { SeparatorsDemo() }),
    AvailableComponent("Slider", "slider", { SliderDemo() }),
    AvailableComponent("Tab Group", "tabgroup", { TabGroupDemo() }),
    AvailableComponent("Text", "text", { TextDemo() }),
    AvailableComponent("Text Field", "textfield", { TextFieldDemo() }),
    AvailableComponent("Toggle Switch", "toggleswitch", { ToggleSwitchDemo() })
)

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
                    availableComponents.forEach { demo ->
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

        availableComponents.forEach { component ->
            composable(component.id) {
                Column {
                    AppBar(onUpClick = { navController.navigateUp() }, title = component.name)
                    val focusRequester = remember { FocusRequester() }

                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }
                    Box(
                        Modifier
                            .focusRequester(focusRequester)
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
            Icon(ArrowLeft, contentDescription = "Go back")
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
    content: @Composable () -> Unit
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


private val ArrowLeft: ImageVector
    get() {
        if (_ArrowLeft != null) {
            return _ArrowLeft!!
        }
        _ArrowLeft = ImageVector.Builder(
            name = "ArrowLeft",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(12f, 19f)
                lineToRelative(-7f, -7f)
                lineToRelative(7f, -7f)
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(19f, 12f)
                horizontalLineTo(5f)
            }
        }.build()
        return _ArrowLeft!!
    }

private var _ArrowLeft: ImageVector? = null
