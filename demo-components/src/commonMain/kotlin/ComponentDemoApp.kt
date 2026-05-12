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
package com.composeunstyled.demo.components

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import com.composeunstyled.PortalHost
import com.composeunstyled.UnstyledIcon
import com.composeunstyled.demo.BottomSheetDemo
import com.composeunstyled.demo.ButtonDemo
import com.composeunstyled.demo.CheckboxDemo
import com.composeunstyled.demo.DialogDemo
import com.composeunstyled.demo.DisclosureDemo
import com.composeunstyled.demo.DropdownMenuDemo
import com.composeunstyled.demo.IconDemo
import com.composeunstyled.demo.ModalBottomSheetDemo
import com.composeunstyled.demo.ModalDemo
import com.composeunstyled.demo.ProgressIndicatorDemo
import com.composeunstyled.demo.RadioGroupDemo
import com.composeunstyled.demo.ScrollbarsDemo
import com.composeunstyled.demo.SeparatorsDemo
import com.composeunstyled.demo.SliderDemo
import com.composeunstyled.demo.TabGroupDemo
import com.composeunstyled.demo.TextFieldDemo
import com.composeunstyled.demo.ToggleSwitchDemo
import com.composeunstyled.demo.TooltipDemo
import com.composeunstyled.demo.TriStateCheckboxDemo
import com.composeunstyled.demo.demoOutline

@Composable
fun ComponentDemoApp(initialDemoId: String? = null) {
  PortalHost(Modifier.fillMaxSize()) {
    val navController = rememberNavController()
    val startDestination = remember(initialDemoId) {
      initialDemoId?.let(::demoRouteFor) ?: HomeRoute
    }

    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(ScreenBackground),
    ) {
      NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
      ) {
        composable(HomeRoute) {
          ComponentDemoSelection(
            onSelect = { navController.navigate(demoRouteFor(it.id)) },
          )
        }

        componentDemos.forEach { demo ->
          composable(demoRouteFor(demo.id)) {
            Column(Modifier.fillMaxSize()) {
              ComponentDemoTopBar(
                title = demo.name,
                onBack = { navController.popBackStack() },
              )
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .weight(1f),
              ) {
                demo.content()
              }
            }
          }
        }
      }
    }
  }
}

private data class ComponentDemo(
  val name: String,
  val id: String,
  val content: @Composable () -> Unit,
)

private const val HomeRoute = "home"

private fun demoRouteFor(demoId: String): String {
  return "demo/$demoId"
}

private val componentDemos = listOf(
  ComponentDemo("Bottom Sheet", "bottom-sheet", { BottomSheetDemo() }),
  ComponentDemo("Bottom Sheet (Modal)", "modal-bottom-sheet", { ModalBottomSheetDemo() }),
  ComponentDemo("Button", "button", { ButtonDemo() }),
  ComponentDemo("Checkbox", "checkbox", { CheckboxDemo() }),
  ComponentDemo("Checkbox (TriState)", "tristatecheckbox", { TriStateCheckboxDemo() }),
  ComponentDemo("Dialog", "dialog", { DialogDemo() }),
  ComponentDemo("Disclosure", "disclosure", { DisclosureDemo() }),
  ComponentDemo("Dropdown Menu", "dropdown-menu", { DropdownMenuDemo() }),
  ComponentDemo("Icon", "icon", { IconDemo() }),
  ComponentDemo("Modal", "modal", { ModalDemo() }),
  ComponentDemo("Progress Indicator", "progressindicator", { ProgressIndicatorDemo() }),
  ComponentDemo("Radio Group", "radiogroup", { RadioGroupDemo() }),
  ComponentDemo("Scrollbars", "scrollbars", { ScrollbarsDemo() }),
  ComponentDemo("Separators", "separators", { SeparatorsDemo() }),
  ComponentDemo("Slider", "slider", { SliderDemo() }),
  ComponentDemo("Tab Group", "tabgroup", { TabGroupDemo() }),
  ComponentDemo("Text Field", "textfield", { TextFieldDemo() }),
  ComponentDemo("Tooltip", "tooltip", { TooltipDemo() }),
  ComponentDemo("Toggle Switch", "toggleswitch", { ToggleSwitchDemo() }),
)

private val ScreenBackground = Color.White
private val PanelBackground = Color.White
private val AccentColor = Color.Black
private val RowTitleStyle = TextStyle(
  fontSize = 18.sp,
  lineHeight = 24.sp,
  fontWeight = FontWeight.Medium,
  color = Color(0xFF1A1A1A),
)
private val ActionStyle = TextStyle(
  fontSize = 18.sp,
  lineHeight = 24.sp,
  fontWeight = FontWeight.Medium,
  color = AccentColor,
)
private val TopBarTitleStyle = TextStyle(
  fontSize = 18.sp,
  lineHeight = 24.sp,
  fontWeight = FontWeight.Medium,
  color = Color(0xFF1A1A1A),
)

@Composable
private fun ComponentDemoSelection(onSelect: (ComponentDemo) -> Unit) {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .widthIn(max = 420.dp)
        .verticalScroll(rememberScrollState())
        .statusBarsPadding()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      componentDemos.forEach { demo ->
        ComponentDemoRow(
          demo = demo,
          onClick = { onSelect(demo) },
        )
      }
    }
  }
}

@Composable
private fun ComponentDemoRow(
  demo: ComponentDemo,
  onClick: () -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .demoOutline(RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 8.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    BasicText(
      text = demo.name,
      style = RowTitleStyle.copy(color = AccentColor),
    )
  }
}

@Composable
private fun ComponentDemoTopBar(
  title: String,
  onBack: () -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(PanelBackground)
      .statusBarsPadding()
      .padding(horizontal = 8.dp, vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(8.dp))
        .demoOutline(RoundedCornerShape(8.dp))
        .clickable(onClick = onBack)
        .padding(horizontal = 10.dp, vertical = 10.dp),
    ) {
      UnstyledIcon(
        imageVector = Lucide.ArrowLeft,
        contentDescription = "Back",
        tint = AccentColor,
      )
    }
    BasicText(
      text = title,
      style = TopBarTitleStyle,
      modifier = Modifier.padding(start = 4.dp),
    )
  }
}
