/*
 * Copyright (c) 2026 Composable Horizons
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.composeunstyled.demo

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.composables.compose.ripple.rememberRippleIndication
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import com.composeunstyled.CrossAxisAlignment
import com.composeunstyled.MainAxisArrangement
import com.composeunstyled.Stack
import com.composeunstyled.StackOrientation
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledIcon
import com.composeunstyled.currentWindowContainerSize

@Composable
fun Demo(startDestination: String = "home") {
  CompositionLocalProvider(LocalIndication provides rememberRippleIndication()) {
    Box(Modifier.fillMaxSize().background(Color(0xFFFAFAFA))) {
      DemoSelection(startDestination)
    }
  }
}

private data class DemoItem(
  val name: String,
  val id: String,
  val demo: @Composable () -> Unit,
  val previewOptions: PreviewOptions = PreviewOptions(),
)

private data class PreviewOptions(
  val contentAlignment: Alignment = Alignment.Center,
  val padding: PaddingValues = PaddingValues(16.dp),
)

private val availablePrimitives = listOf(
  DemoItem("Avatar", "avatar", { AvatarDemo() }),
  DemoItem(
    "Bottom Sheet",
    "bottom-sheet",
    { BottomSheetDemo() },
    previewOptions = PreviewOptions(padding = PaddingValues(0.dp)),
  ),
  DemoItem(
    "Drawer",
    "drawer",
    { DrawerDemo() },
    previewOptions = PreviewOptions(padding = PaddingValues(0.dp)),
  ),
  DemoItem(
    "Drawer Side",
    "drawer-side",
    { DrawerSideDemo() },
    previewOptions = PreviewOptions(padding = PaddingValues(0.dp)),
  ),
  DemoItem(
    "Drawer Snap Points",
    "drawer-snap-points",
    { DrawerSnapPointsDemo() },
    previewOptions = PreviewOptions(padding = PaddingValues(0.dp)),
  ),
  DemoItem("Modal Bottom Sheet", "modal-bottom-sheet", { ModalBottomSheetDemo() }),
  DemoItem("Button", "button", { ButtonDemo() }),
  DemoItem("Checkbox", "checkbox", { CheckboxDemo() }),
  DemoItem("Tristate Checkbox", "tristatecheckbox", { TriStateCheckboxDemo() }),
  DemoItem("Dialog", "dialog", { DialogDemo() }),
  DemoItem(
    "Disclosure",
    "disclosure",
    { DisclosureDemo() },
    previewOptions = PreviewOptions(contentAlignment = Alignment.TopCenter),
  ),
  DemoItem(
    "Dropdown Menu",
    "dropdown-menu",
    { DropdownMenuDemo() },
    previewOptions = PreviewOptions(contentAlignment = Alignment.TopCenter),
  ),
  DemoItem("Icon", "icon", { IconDemo() }),
  DemoItem("Modal", "modal", { ModalDemo() }),
  DemoItem("Modal Render Host", "modal-render-host", { ModalRenderHostDemo() }),
  DemoItem("Progress Indicator", "progressindicator", { ProgressIndicatorDemo() }),
  DemoItem("Radio Group", "radiogroup", { RadioGroupDemo() }),
  DemoItem("Scrollbars", "scrollbars", { ScrollbarsDemo() }),
  DemoItem("Separators", "separators", { SeparatorsDemo() }),
  DemoItem("Slider", "slider", { SliderDemo() }),
  DemoItem("Tab Group", "tabgroup", { TabGroupDemo() }),
  DemoItem("Text Field", "textfield", { TextFieldDemo() }),
  DemoItem("Tooltip", "tooltip", { TooltipDemo() }),
  DemoItem("Toggle Switch", "toggleswitch", { ToggleSwitchDemo() }),
)

private val availableModifiers = listOf(
  DemoItem(
    "Focus Ring (FocusVisible)",
    "focus-ring-focus-visible",
    { FocusRingFocusVisibleDemo() },
  ),
  DemoItem("Focus Ring (Focused)", "focus-ring-focused", { FocusRingFocusedDemo() }),
  DemoItem("Outline", "outline", { OutlineDemo() }),
).map {
  it.copy(demo = {
    ModifierDemo {
      it.demo()
    }
  })
}

private val themingDemos = listOf(
  DemoItem("Platform Theme", "platform-theme", { PlatformThemeDemo() }),
  DemoItem("Theme Extend", "theme-extend", { ThemeExtendDemo() }),
  DemoItem("Theming", "theme", { ThemingDemo() }),
)

private val utilityDemos = listOf(
  DemoItem("Breakpoints", "breakpoints", { BreakpointsDemo() }),
  DemoItem("Window Container Size", "window-container-size", { WindowContainerSizeDemo() }),
)

private val availableDemos: List<DemoItem> =
  availablePrimitives + availableModifiers + themingDemos + utilityDemos

@Composable
fun ModifierDemo(content: @Composable () -> Unit) {
  val size = currentWindowContainerSize()
  val isWide = size.width > 600.dp
  val spacedBy = if (isWide) 60.dp else 30.dp
  Stack(
    modifier = Modifier.fillMaxSize().background(Color.White),
    orientation = if (isWide) StackOrientation.Horizontal else StackOrientation.Vertical,
    mainAxisArrangement = MainAxisArrangement.Center,
    crossAxisAlignment = CrossAxisAlignment.Center,
    spacing = spacedBy,
  ) {
    content()
  }
}

@Composable
private fun DemoSelection(startDestination: String) {
  val navController = rememberNavController()
  val initialDestination = availableDemos
    .firstOrNull { it.id == startDestination }
    ?.id
    ?: "home"

  NavHost(
    navController = navController,
    startDestination = initialDestination,
    enterTransition = {
      EnterTransition.None
    },
    exitTransition = {
      ExitTransition.None
    },
    popEnterTransition = {
      EnterTransition.None
    },
    popExitTransition = {
      ExitTransition.None
    },
  ) {
    composable("home") {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart,
      ) {
        Column(
          Modifier.verticalScroll(rememberScrollState()).systemBarsPadding().padding(8.dp)
            .fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          if (availablePrimitives.isNotEmpty()) {
            DemoSection("Components", availablePrimitives) { demo ->
              navController.navigate(demo.id)
            }
          }

          if (themingDemos.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            DemoSection("Theme", themingDemos) { demo ->
              navController.navigate(demo.id)
            }
          }

          if (availableModifiers.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            DemoSection("Modifiers", availableModifiers) { demo ->
              navController.navigate(demo.id)
            }
          }

          if (utilityDemos.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            DemoSection("Utilities", utilityDemos) { demo ->
              navController.navigate(demo.id)
            }
          }
        }
      }
    }

    availableDemos.forEach { component ->
      composable(component.id) {
        val launchedFromDemoList = initialDestination == "home"
        Column {
          if (launchedFromDemoList) {
            AppBar(onUpClick = { navController.navigateUp() }, title = component.name)
          }
          DemoContainer(component.previewOptions) {
            component.demo()
          }
        }
      }
    }
  }
}

@Composable
private fun ColumnScope.DemoContainer(
  previewOptions: PreviewOptions,
  content: @Composable () -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .weight(1f)
      .background(Color.White)
      .padding(previewOptions.padding),
    contentAlignment = previewOptions.contentAlignment,
  ) {
    content()
  }
}

@Composable
private fun DemoSection(
  title: String,
  demos: List<DemoItem>,
  onClick: (DemoItem) -> Unit,
) {
  BasicText(
    text = title,
    modifier = Modifier.padding(horizontal = 16.dp),
    style = TextStyle(fontWeight = FontWeight.SemiBold),
  )
  demos.forEach { demo ->
    DemoListButton(
      onClick = { onClick(demo) },
      modifier = Modifier.fillMaxWidth(),
    ) {
      BasicText(demo.name)
    }
  }
}

@Composable
private fun AppBar(onUpClick: () -> Unit, title: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color.White)
      .padding(WindowInsets.statusBars.asPaddingValues())
      .padding(4.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    UnstyledButton(
      onClick = onUpClick,
      modifier = Modifier
        .clip(CircleShape),
      indication = LocalIndication.current,
    ) {
      Box(Modifier.padding(12.dp)) {
        UnstyledIcon(Lucide.ArrowLeft, contentDescription = "Go back")
      }
    }
    Spacer(Modifier.width(8.dp))
    BasicText(title)
  }
}

@Composable
private fun DemoListButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  UnstyledButton(
    onClick = onClick,
    modifier = modifier
      .sizeIn(minWidth = 40.dp, minHeight = 48.dp)
      .clip(RoundedCornerShape(8.dp)),
    indication = LocalIndication.current,
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      contentAlignment = Alignment.CenterStart,
    ) {
      content()
    }
  }
}
