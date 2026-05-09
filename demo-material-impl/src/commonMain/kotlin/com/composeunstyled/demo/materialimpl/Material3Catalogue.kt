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
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.composeunstyled.demo.material3api

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Add
import com.composables.icons.materialsymbols.rounded.Apps
import com.composables.icons.materialsymbols.rounded.Arrow_back
import com.composables.icons.materialsymbols.rounded.Help
import com.composables.icons.materialsymbols.rounded.Info
import com.composables.icons.materialsymbols.rounded.Settings
import com.composables.icons.materialsymbols.rounded.Star
import com.composeunstyled.PortalHost
import com.composeunstyled.SheetDetent
import com.composeunstyled.rememberModalBottomSheetState
import androidx.compose.material3.AlertDialog as M3AlertDialog
import androidx.compose.material3.Button as M3Button
import androidx.compose.material3.Checkbox as M3Checkbox
import androidx.compose.material3.DropdownMenu as M3DropdownMenu
import androidx.compose.material3.DropdownMenuItem as M3DropdownMenuItem
import androidx.compose.material3.ElevatedButton as M3ElevatedButton
import androidx.compose.material3.FilledIconButton as M3FilledIconButton
import androidx.compose.material3.FilledTonalButton as M3FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton as M3FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton as M3FloatingActionButton
import androidx.compose.material3.HorizontalDivider as M3HorizontalDivider
import androidx.compose.material3.Icon as M3Icon
import androidx.compose.material3.IconButton as M3IconButton
import androidx.compose.material3.IconToggleButton as M3IconToggleButton
import androidx.compose.material3.LargeFloatingActionButton as M3LargeFloatingActionButton
import androidx.compose.material3.LinearProgressIndicator as M3LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet as M3ModalBottomSheet
import androidx.compose.material3.OutlinedButton as M3OutlinedButton
import androidx.compose.material3.OutlinedIconButton as M3OutlinedIconButton
import androidx.compose.material3.OutlinedTextField as M3OutlinedTextField
import androidx.compose.material3.PlainTooltip as M3PlainTooltip
import androidx.compose.material3.PrimaryTabRow as M3PrimaryTabRow
import androidx.compose.material3.RadioButton as M3RadioButton
import androidx.compose.material3.SecondaryTabRow as M3SecondaryTabRow
import androidx.compose.material3.Slider as M3Slider
import androidx.compose.material3.SmallFloatingActionButton as M3SmallFloatingActionButton
import androidx.compose.material3.Surface as M3Surface
import androidx.compose.material3.Switch as M3Switch
import androidx.compose.material3.Tab as M3Tab
import androidx.compose.material3.TextButton as M3TextButton
import androidx.compose.material3.TextField as M3TextField
import androidx.compose.material3.TooltipBox as M3TooltipBox
import androidx.compose.material3.TriStateCheckbox as M3TriStateCheckbox
import androidx.compose.material3.VerticalDivider as M3VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState as rememberM3ModalBottomSheetState

@Composable
fun Material3ImplementationDemo() {
  MaterialTheme {
    PortalHost(Modifier.fillMaxSize()) {
      val navController = rememberNavController()
      Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
      ) {
        NavHost(
          navController = navController,
          startDestination = CatalogueRoute,
          enterTransition = { EnterTransition.None },
          exitTransition = { ExitTransition.None },
          popEnterTransition = { EnterTransition.None },
          popExitTransition = { ExitTransition.None },
        ) {
          composable(CatalogueRoute) {
            Catalogue(onOpenDemo = { navController.navigate(it.id) })
          }
          componentDemos.forEach { demo ->
            composable(demo.id) {
              DemoDetail(
                demo = demo,
                onBack = { navController.navigateUp() },
              )
            }
          }
        }
      }
    }
  }
}

private const val CatalogueRoute = "catalogue"

private data class ComponentDemo(
  val id: String,
  val group: String,
  val name: String,
  val content: @Composable () -> Unit,
)

private val componentDemos = listOf(
  ComponentDemo("buttons", "Actions", "Buttons", { ButtonsDemo() }),
  ComponentDemo("icon-buttons", "Actions", "Icon buttons", { IconButtonsDemo() }),
  ComponentDemo("floating-action-buttons", "Actions", "Floating action buttons", {
    FloatingActionButtonsDemo()
  }),
  ComponentDemo("checkbox", "Input", "Checkbox", { CheckboxDemo() }),
  ComponentDemo("tri-state-checkbox", "Input", "Tri-state checkbox", { TriStateCheckboxDemo() }),
  ComponentDemo("radio-button", "Input", "Radio button", { RadioButtonDemo() }),
  ComponentDemo("switch", "Input", "Switch", { SwitchDemo() }),
  ComponentDemo("slider", "Input", "Slider", { SliderComponentDemo() }),
  ComponentDemo("text-field", "Input", "Text field", { TextFieldDemo() }),
  ComponentDemo("outlined-text-field", "Input", "Outlined text field", { OutlinedTextFieldDemo() }),
  ComponentDemo("linear-progress-indicator", "Feedback", "Linear progress indicator", {
    LinearProgressDemo()
  }),
  ComponentDemo("primary-tab-row", "Navigation", "Primary tab row", { PrimaryTabRowDemo() }),
  ComponentDemo("secondary-tab-row", "Navigation", "Secondary tab row", { SecondaryTabRowDemo() }),
  ComponentDemo("dropdown-menu", "Menus", "Dropdown menu", { DropdownMenuDemo() }),
  ComponentDemo("alert-dialog", "Overlays", "Alert dialog", { AlertDialogDemo() }),
  ComponentDemo("modal-bottom-sheet", "Overlays", "Modal bottom sheet", { ModalBottomSheetDemo() }),
  ComponentDemo("tooltip", "Overlays", "Tooltip", { TooltipDemo() }),
  ComponentDemo(
    "horizontal-divider",
    "Structure",
    "Horizontal divider",
    { HorizontalDividerDemo() },
  ),
  ComponentDemo("vertical-divider", "Structure", "Vertical divider", { VerticalDividerDemo() }),
)

@Composable
private fun Catalogue(onOpenDemo: (ComponentDemo) -> Unit) {
  Column(
    Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(24.dp),
    verticalArrangement = Arrangement.spacedBy(20.dp),
  ) {
    Text(
      "Material3 implementation",
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.SemiBold,
    )
    Text(
      "Supported component APIs backed by Compose Unstyled primitives.",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    componentDemos.groupBy { it.group }.forEach { (group, demos) ->
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(group, style = MaterialTheme.typography.titleMedium)
        demos.forEach { demo ->
          OutlinedButton(
            onClick = { onOpenDemo(demo) },
            modifier = Modifier.fillMaxWidth().widthIn(max = 560.dp),
          ) {
            Text(demo.name)
          }
        }
      }
    }
  }
}

@Composable
private fun DemoDetail(demo: ComponentDemo, onBack: () -> Unit) {
  Column(Modifier.fillMaxSize()) {
    Material3XAppBar(
      title = demo.name,
      navigationIcon = {
        IconButton(onClick = onBack) {
          Icon(MaterialSymbols.Rounded.Arrow_back, contentDescription = "Back")
        }
      },
    )
    Column(
      Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      demo.content()
    }
  }
}

@Composable
private fun Material3XAppBar(
  title: String,
  modifier: Modifier = Modifier,
  navigationIcon: @Composable () -> Unit = {},
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surface,
    contentColor = MaterialTheme.colorScheme.onSurface,
  ) {
    Row(
      Modifier.height(64.dp).padding(horizontal = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      navigationIcon()
      Text(
        title,
        modifier = Modifier.padding(start = 4.dp),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
      )
    }
  }
}

@Composable
private fun ComparisonLayout(
  top: @Composable () -> Unit,
  bottom: @Composable () -> Unit,
) {
  Column(
    Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(32.dp),
  ) {
    ComparisonPane(title = "Compose Unstyled", content = top)
    ComparisonPane(title = "Material 3", content = bottom)
  }
}

@Composable
private fun ComparisonPane(title: String, content: @Composable () -> Unit) {
  Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    content()
  }
}

@Composable
private fun SampleBlock(title: String, content: @Composable () -> Unit) {
  Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    Text(title, style = MaterialTheme.typography.titleMedium)
    Surface(
      modifier = Modifier.fillMaxWidth().widthIn(max = 720.dp),
      shape = MaterialTheme.shapes.medium,
      color = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
      Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        content()
      }
    }
  }
}

@Composable
private fun ButtonsDemo() {
  ComparisonLayout(
    top = {
      SampleBlock("Button variants") {
        FlowRow {
          Button(onClick = {}) { Text("Filled") }
          ElevatedButton(onClick = {}) { Text("Elevated") }
          FilledTonalButton(onClick = {}) { Text("Tonal") }
          OutlinedButton(onClick = {}) { Text("Outlined") }
          TextButton(onClick = {}) { Text("Text") }
        }
        FlowRow {
          Button(onClick = {}, enabled = false) { Text("Filled") }
          ElevatedButton(onClick = {}, enabled = false) { Text("Elevated") }
          FilledTonalButton(onClick = {}, enabled = false) { Text("Tonal") }
          OutlinedButton(onClick = {}, enabled = false) { Text("Outlined") }
          TextButton(onClick = {}, enabled = false) { Text("Text") }
        }
      }
    },
    bottom = {
      MaterialSampleBlock("Button variants") {
        FlowRow {
          M3Button(onClick = {}) { Text("Filled") }
          M3ElevatedButton(onClick = {}) { Text("Elevated") }
          M3FilledTonalButton(onClick = {}) { Text("Tonal") }
          M3OutlinedButton(onClick = {}) { Text("Outlined") }
          M3TextButton(onClick = {}) { Text("Text") }
        }
        FlowRow {
          M3Button(onClick = {}, enabled = false) { Text("Filled") }
          M3ElevatedButton(onClick = {}, enabled = false) { Text("Elevated") }
          M3FilledTonalButton(onClick = {}, enabled = false) { Text("Tonal") }
          M3OutlinedButton(onClick = {}, enabled = false) { Text("Outlined") }
          M3TextButton(onClick = {}, enabled = false) { Text("Text") }
        }
      }
    },
  )
}

@Composable
private fun IconButtonsDemo() {
  ComparisonLayout(
    top = {
      var checked by remember { mutableStateOf(false) }
      SampleBlock("Icon buttons") {
        FlowRow {
          IconButton(onClick = {}) {
            Icon(MaterialSymbols.Rounded.Info, contentDescription = "Info")
          }
          FilledIconButton(onClick = {}) {
            Icon(MaterialSymbols.Rounded.Add, contentDescription = "Add")
          }
          FilledTonalIconButton(onClick = {}) {
            Icon(MaterialSymbols.Rounded.Star, contentDescription = "Star")
          }
          OutlinedIconButton(onClick = {}) {
            Icon(MaterialSymbols.Rounded.Help, contentDescription = "Help")
          }
          IconToggleButton(checked = checked, onCheckedChange = { checked = it }) {
            Icon(MaterialSymbols.Rounded.Star, contentDescription = if (checked) "On" else "Off")
          }
        }
      }
    },
    bottom = {
      var checked by remember { mutableStateOf(false) }
      MaterialSampleBlock("Icon buttons") {
        FlowRow {
          M3IconButton(onClick = {}) {
            M3Icon(MaterialSymbols.Rounded.Info, contentDescription = "Info")
          }
          M3FilledIconButton(onClick = {}) {
            M3Icon(MaterialSymbols.Rounded.Add, contentDescription = "Add")
          }
          M3FilledTonalIconButton(onClick = {}) {
            M3Icon(MaterialSymbols.Rounded.Star, contentDescription = "Star")
          }
          M3OutlinedIconButton(onClick = {}) {
            M3Icon(MaterialSymbols.Rounded.Help, contentDescription = "Help")
          }
          M3IconToggleButton(checked = checked, onCheckedChange = { checked = it }) {
            M3Icon(MaterialSymbols.Rounded.Star, contentDescription = if (checked) "On" else "Off")
          }
        }
      }
    },
  )
}

@Composable
private fun FloatingActionButtonsDemo() {
  ComparisonLayout(
    top = {
      SampleBlock("Floating action buttons") {
        FlowRow {
          SmallFloatingActionButton(onClick = {}) {
            Icon(MaterialSymbols.Rounded.Add, contentDescription = "Add")
          }
          FloatingActionButton(onClick = {}) {
            Icon(MaterialSymbols.Rounded.Add, contentDescription = "Add")
          }
          LargeFloatingActionButton(onClick = {}) {
            Icon(MaterialSymbols.Rounded.Add, contentDescription = "Add")
          }
        }
      }
    },
    bottom = {
      MaterialSampleBlock("Floating action buttons") {
        FlowRow {
          M3SmallFloatingActionButton(onClick = {}) {
            M3Icon(MaterialSymbols.Rounded.Add, contentDescription = "Add")
          }
          M3FloatingActionButton(onClick = {}) {
            M3Icon(MaterialSymbols.Rounded.Add, contentDescription = "Add")
          }
          M3LargeFloatingActionButton(onClick = {}) {
            M3Icon(MaterialSymbols.Rounded.Add, contentDescription = "Add")
          }
        }
      }
    },
  )
}

@Composable
private fun CheckboxDemo() {
  ComparisonLayout(
    top = {
      var checked by remember { mutableStateOf(true) }
      SampleBlock("Checkbox") {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Checkbox(checked = checked, onCheckedChange = { checked = it })
          Spacer(Modifier.width(8.dp))
          Text("Checkbox")
        }
      }
    },
    bottom = {
      var checked by remember { mutableStateOf(true) }
      MaterialSampleBlock("Checkbox") {
        Row(verticalAlignment = Alignment.CenterVertically) {
          M3Checkbox(checked = checked, onCheckedChange = { checked = it })
          Spacer(Modifier.width(8.dp))
          Text("Checkbox")
        }
      }
    },
  )
}

@Composable
private fun TriStateCheckboxDemo() {
  ComparisonLayout(
    top = {
      var triState by remember { mutableStateOf(ToggleableState.Indeterminate) }
      SampleBlock("Tri-state checkbox") {
        Row(verticalAlignment = Alignment.CenterVertically) {
          TriStateCheckbox(
            state = triState,
            onClick = {
              triState = nextToggleableState(triState)
            },
          )
          Spacer(Modifier.width(8.dp))
          Text("Tri-state checkbox")
        }
      }
    },
    bottom = {
      var triState by remember { mutableStateOf(ToggleableState.Indeterminate) }
      MaterialSampleBlock("Tri-state checkbox") {
        Row(verticalAlignment = Alignment.CenterVertically) {
          M3TriStateCheckbox(
            state = triState,
            onClick = {
              triState = nextToggleableState(triState)
            },
          )
          Spacer(Modifier.width(8.dp))
          Text("Tri-state checkbox")
        }
      }
    },
  )
}

private fun nextToggleableState(state: ToggleableState): ToggleableState =
  when (state) {
    ToggleableState.Off -> ToggleableState.On
    ToggleableState.On -> ToggleableState.Indeterminate
    ToggleableState.Indeterminate -> ToggleableState.Off
  }

@Composable
private fun RadioButtonDemo() {
  ComparisonLayout(
    top = {
      var radio by remember { mutableStateOf("A") }
      SampleBlock("Radio button") {
        FlowRow {
          Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = radio == "A", onClick = { radio = "A" })
            Text("Option A")
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = radio == "B", onClick = { radio = "B" })
            Text("Option B")
          }
        }
      }
    },
    bottom = {
      var radio by remember { mutableStateOf("A") }
      MaterialSampleBlock("Radio button") {
        FlowRow {
          Row(verticalAlignment = Alignment.CenterVertically) {
            M3RadioButton(selected = radio == "A", onClick = { radio = "A" })
            Text("Option A")
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            M3RadioButton(selected = radio == "B", onClick = { radio = "B" })
            Text("Option B")
          }
        }
      }
    },
  )
}

@Composable
private fun SwitchDemo() {
  ComparisonLayout(
    top = {
      var checked by remember { mutableStateOf(true) }
      SampleBlock("Switch") {
        SwitchSampleRow(
          label = "Enabled on",
          checked = checked,
          onCheckedChange = { checked = it },
        )
        SwitchSampleRow(
          label = "Enabled off",
          checked = false,
          onCheckedChange = {},
        )
        SwitchSampleRow(
          label = "Disabled on",
          checked = true,
          enabled = false,
        )
        SwitchSampleRow(
          label = "Disabled off",
          checked = false,
          enabled = false,
        )
      }
    },
    bottom = {
      var checked by remember { mutableStateOf(true) }
      MaterialSampleBlock("Switch") {
        MaterialSwitchSampleRow(
          label = "Enabled on",
          checked = checked,
          onCheckedChange = { checked = it },
        )
        MaterialSwitchSampleRow(
          label = "Enabled off",
          checked = false,
          onCheckedChange = {},
        )
        MaterialSwitchSampleRow(
          label = "Disabled on",
          checked = true,
          enabled = false,
        )
        MaterialSwitchSampleRow(
          label = "Disabled off",
          checked = false,
          enabled = false,
        )
      }
    },
  )
}

@Composable
private fun SwitchSampleRow(
  label: String,
  checked: Boolean,
  enabled: Boolean = true,
  onCheckedChange: ((Boolean) -> Unit)? = null,
) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      enabled = enabled,
    )
    Spacer(Modifier.width(8.dp))
    Text(label)
  }
}

@Composable
private fun MaterialSwitchSampleRow(
  label: String,
  checked: Boolean,
  enabled: Boolean = true,
  onCheckedChange: ((Boolean) -> Unit)? = null,
) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    M3Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      enabled = enabled,
    )
    Spacer(Modifier.width(8.dp))
    Text(label)
  }
}

@Composable
private fun SliderComponentDemo() {
  ComparisonLayout(
    top = {
      var value by remember { mutableFloatStateOf(0.35f) }
      SampleBlock("Slider") {
        Slider(value = value, onValueChange = { value = it })
        Text("Value: ${value.toString().take(4)}")
      }
    },
    bottom = {
      var value by remember { mutableFloatStateOf(0.35f) }
      MaterialSampleBlock("Slider") {
        M3Slider(value = value, onValueChange = { value = it })
        Text("Value: ${value.toString().take(4)}")
      }
    },
  )
}

@Composable
private fun TextFieldDemo() {
  ComparisonLayout(
    top = {
      val state = rememberTextFieldState("Compose Unstyled")
      SampleBlock("Text field") {
        TextField(
          state = state,
          label = { Text("Label") },
          supportingText = { Text("State-based TextField API") },
          modifier = Modifier.fillMaxWidth(),
        )
      }
    },
    bottom = {
      val state = rememberTextFieldState("Compose Unstyled")
      MaterialSampleBlock("Text field") {
        M3TextField(
          state = state,
          label = { Text("Label") },
          supportingText = { Text("State-based TextField API") },
          modifier = Modifier.fillMaxWidth(),
        )
      }
    },
  )
}

@Composable
private fun OutlinedTextFieldDemo() {
  ComparisonLayout(
    top = {
      val state = rememberTextFieldState()
      SampleBlock("Outlined text field") {
        OutlinedTextField(
          state = state,
          label = { Text("Outlined") },
          placeholder = { Text("Type here") },
          modifier = Modifier.fillMaxWidth(),
        )
      }
    },
    bottom = {
      val state = rememberTextFieldState()
      MaterialSampleBlock("Outlined text field") {
        M3OutlinedTextField(
          state = state,
          label = { Text("Outlined") },
          placeholder = { Text("Type here") },
          modifier = Modifier.fillMaxWidth(),
        )
      }
    },
  )
}

@Composable
private fun LinearProgressDemo() {
  ComparisonLayout(
    top = {
      SampleBlock("Linear progress indicator") {
        LinearProgressIndicator(progress = { 0.62f })
      }
    },
    bottom = {
      MaterialSampleBlock("Linear progress indicator") {
        M3LinearProgressIndicator(progress = { 0.62f })
      }
    },
  )
}

@Composable
private fun PrimaryTabRowDemo() {
  ComparisonLayout(
    top = {
      var selected by remember { mutableIntStateOf(0) }
      SampleBlock("Primary tab row") {
        PrimaryTabRow(selectedTabIndex = selected, tabKeys = listOf("one", "two", "three")) {
          Tab(selected = selected == 0, onClick = { selected = 0 }, text = { Text("One") })
          Tab(selected = selected == 1, onClick = { selected = 1 }, text = { Text("Two") })
          Tab(selected = selected == 2, onClick = { selected = 2 }, text = { Text("Three") })
        }
      }
    },
    bottom = {
      var selected by remember { mutableIntStateOf(0) }
      MaterialSampleBlock("Primary tab row") {
        M3PrimaryTabRow(selectedTabIndex = selected) {
          M3Tab(selected = selected == 0, onClick = { selected = 0 }, text = { Text("One") })
          M3Tab(selected = selected == 1, onClick = { selected = 1 }, text = { Text("Two") })
          M3Tab(selected = selected == 2, onClick = { selected = 2 }, text = { Text("Three") })
        }
      }
    },
  )
}

@Composable
private fun SecondaryTabRowDemo() {
  ComparisonLayout(
    top = {
      var selected by remember { mutableIntStateOf(0) }
      SampleBlock("Secondary tab row") {
        SecondaryTabRow(selectedTabIndex = selected, tabKeys = listOf("apps", "settings")) {
          Tab(selected = selected == 0, onClick = { selected = 0 }) {
            Icon(MaterialSymbols.Rounded.Apps, contentDescription = "Apps")
            Text("Apps")
          }
          Tab(selected = selected == 1, onClick = { selected = 1 }) {
            Icon(MaterialSymbols.Rounded.Settings, contentDescription = "Settings")
            Text("Settings")
          }
        }
      }
    },
    bottom = {
      var selected by remember { mutableIntStateOf(0) }
      MaterialSampleBlock("Secondary tab row") {
        M3SecondaryTabRow(selectedTabIndex = selected) {
          M3Tab(selected = selected == 0, onClick = { selected = 0 }) {
            M3Icon(MaterialSymbols.Rounded.Apps, contentDescription = "Apps")
            Text("Apps")
          }
          M3Tab(selected = selected == 1, onClick = { selected = 1 }) {
            M3Icon(MaterialSymbols.Rounded.Settings, contentDescription = "Settings")
            Text("Settings")
          }
        }
      }
    },
  )
}

@Composable
private fun DropdownMenuDemo() {
  ComparisonLayout(
    top = {
      var expanded by remember { mutableStateOf(false) }
      SampleBlock("Dropdown menu") {
        DropdownMenu(
          expanded = expanded,
          onExpandedChange = { expanded = it },
          anchor = {
            Button(onClick = { expanded = true }) {
              Text("Open menu")
            }
          },
        ) {
          DropdownMenuItem(text = { Text("First item") }, onClick = { expanded = false })
          DropdownMenuItem(text = { Text("Second item") }, onClick = { expanded = false })
        }
      }
    },
    bottom = {
      var expanded by remember { mutableStateOf(false) }
      MaterialSampleBlock("Dropdown menu") {
        Box {
          M3Button(onClick = { expanded = true }) {
            Text("Open menu")
          }
          M3DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            M3DropdownMenuItem(text = { Text("First item") }, onClick = { expanded = false })
            M3DropdownMenuItem(text = { Text("Second item") }, onClick = { expanded = false })
          }
        }
      }
    },
  )
}

@Composable
private fun AlertDialogDemo() {
  ComparisonLayout(
    top = {
      var showDialog by remember { mutableStateOf(false) }
      SampleBlock("Alert dialog") {
        Button(onClick = { showDialog = true }) { Text("Open dialog") }
      }
      AlertDialog(
        visible = showDialog,
        onDismissRequest = { showDialog = false },
        confirmButton = {
          TextButton(onClick = { showDialog = false }) { Text("Confirm") }
        },
        dismissButton = {
          TextButton(onClick = { showDialog = false }) { Text("Dismiss") }
        },
        title = { Text("Alert dialog") },
        text = { Text("This dialog is rendered through the Material3 facade.") },
      )
    },
    bottom = {
      var showDialog by remember { mutableStateOf(false) }
      MaterialSampleBlock("Alert dialog") {
        M3Button(onClick = { showDialog = true }) { Text("Open dialog") }
      }
      if (showDialog) {
        M3AlertDialog(
          onDismissRequest = { showDialog = false },
          confirmButton = {
            M3TextButton(onClick = { showDialog = false }) { Text("Confirm") }
          },
          dismissButton = {
            M3TextButton(onClick = { showDialog = false }) { Text("Dismiss") }
          },
          title = { Text("Alert dialog") },
          text = { Text("This dialog is rendered by Material 3.") },
        )
      }
    },
  )
}

@Composable
private fun ModalBottomSheetDemo() {
  ComparisonLayout(
    top = {
      val sheetState = rememberModalBottomSheetState(
        initialDetent = SheetDetent.Hidden,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
        dismissAnimationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
      )
      SampleBlock("Modal bottom sheet") {
        Button(
          onClick = { sheetState.targetDetent = SheetDetent.FullyExpanded },
        ) { Text("Open sheet") }
      }
      ModalBottomSheet(
        onDismissRequest = { sheetState.targetDetent = SheetDetent.Hidden },
        sheetState = sheetState,
      ) {
        Spacer(Modifier.height(320.dp))
      }
    },
    bottom = {
      var showSheet by remember { mutableStateOf(false) }
      val sheetState = rememberM3ModalBottomSheetState(skipPartiallyExpanded = true)
      MaterialSampleBlock("Modal bottom sheet") {
        M3Button(onClick = { showSheet = true }) { Text("Open sheet") }
      }
      if (showSheet) {
        M3ModalBottomSheet(
          onDismissRequest = { showSheet = false },
          sheetState = sheetState,
        ) {
          Spacer(Modifier.height(320.dp))
        }
      }
    },
  )
}

@Composable
private fun TooltipDemo() {
  ComparisonLayout(
    top = {
      SampleBlock("Tooltip") {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
          TooltipBox(
            tooltip = {
              PlainTooltip {
                Text("Add item")
              }
            },
          ) {
            IconButton(onClick = {}) {
              Icon(MaterialSymbols.Rounded.Add, contentDescription = "Add item")
            }
          }
        }
      }
    },
    bottom = {
      MaterialSampleBlock("Tooltip") {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
          M3TooltipBox(
            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
              TooltipAnchorPosition.Above,
            ),
            tooltip = {
              M3PlainTooltip {
                Text("Add item")
              }
            },
            state = rememberTooltipState(),
          ) {
            M3IconButton(onClick = {}) {
              M3Icon(MaterialSymbols.Rounded.Add, contentDescription = "Add item")
            }
          }
        }
      }
    },
  )
}

@Composable
private fun HorizontalDividerDemo() {
  ComparisonLayout(
    top = {
      SampleBlock("Horizontal divider") {
        Text("Horizontal")
        HorizontalDivider()
      }
    },
    bottom = {
      MaterialSampleBlock("Horizontal divider") {
        Text("Horizontal")
        M3HorizontalDivider()
      }
    },
  )
}

@Composable
private fun VerticalDividerDemo() {
  ComparisonLayout(
    top = {
      SampleBlock("Vertical divider") {
        Row(Modifier.height(48.dp), verticalAlignment = Alignment.CenterVertically) {
          Text("Start")
          VerticalDivider(Modifier.padding(horizontal = 16.dp))
          Text("End")
        }
      }
    },
    bottom = {
      MaterialSampleBlock("Vertical divider") {
        Row(Modifier.height(48.dp), verticalAlignment = Alignment.CenterVertically) {
          Text("Start")
          M3VerticalDivider(Modifier.padding(horizontal = 16.dp))
          Text("End")
        }
      }
    },
  )
}

@Composable
private fun MaterialSampleBlock(title: String, content: @Composable () -> Unit) {
  Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    Text(title, style = MaterialTheme.typography.titleMedium)
    M3Surface(
      modifier = Modifier.fillMaxWidth().widthIn(max = 720.dp),
      shape = MaterialTheme.shapes.medium,
      color = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
      Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        content()
      }
    }
  }
}

@Composable
private fun FlowRow(content: @Composable RowScope.() -> Unit) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.CenterVertically,
    content = content,
  )
}
