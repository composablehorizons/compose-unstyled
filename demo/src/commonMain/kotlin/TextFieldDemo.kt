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

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composeunstyled.Editable
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledTextField

@Composable
fun TextFieldDemo() {
  val state = rememberTextFieldState()
  var contentAlignment by remember { mutableStateOf(Alignment.CenterStart) }
  var sizeMode by remember { mutableStateOf(SizeMode.Wrap) }
  var paddingMode by remember { mutableStateOf(PaddingMode.Roomy) }
  var textAlign by remember { mutableStateOf(TextAlign.Unspecified) }
  var textSize by remember { mutableIntStateOf(18) }
  var lineMode by remember { mutableStateOf(LineMode.Single) }
  var placeholderMode by remember { mutableStateOf(PlaceholderMode.Email) }
  var borderEnabled by remember { mutableStateOf(true) }
  var backgroundEnabled by remember { mutableStateOf(true) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .imePadding(),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      ControlGroup("Content alignment") {
        ContentAlignmentControls(contentAlignment) { contentAlignment = it }
      }
      ControlGroup("Size") {
        ChoiceRow(
          values = SizeMode.entries,
          selected = sizeMode,
          label = { it.label },
          onSelect = { sizeMode = it },
        )
      }
      ControlGroup("Text align") {
        ChoiceRow(
          values = TextAlignMode.entries,
          selected = textAlign.toMode(),
          label = { it.label },
          onSelect = { textAlign = it.textAlign },
        )
      }
      ControlGroup("Text size") {
        ChoiceRow(
          values = listOf(12, 18, 28, 44),
          selected = textSize,
          label = { "${it}sp" },
          onSelect = { textSize = it },
        )
      }
      ControlGroup("Padding") {
        ChoiceRow(
          values = PaddingMode.entries,
          selected = paddingMode,
          label = { it.label },
          onSelect = { paddingMode = it },
        )
      }
      ControlGroup("Text") {
        ChoiceRow(
          values = TextPreset.entries,
          selected = TextPreset.Custom,
          label = { it.label },
          onSelect = { state.setTextAndPlaceCursorAtEnd(it.value) },
        )
      }
      ControlGroup("Placeholder") {
        ChoiceRow(
          values = PlaceholderMode.entries,
          selected = placeholderMode,
          label = { it.label },
          onSelect = { placeholderMode = it },
        )
      }
      ControlGroup("Lines and chrome") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          SmokeButton(
            label = "Single",
            selected = lineMode == LineMode.Single,
            onClick = { lineMode = LineMode.Single },
          )
          SmokeButton(
            label = "Multi",
            selected = lineMode == LineMode.Multi,
            onClick = { lineMode = LineMode.Multi },
          )
          SmokeButton(
            label = "Border",
            selected = borderEnabled,
            onClick = { borderEnabled = !borderEnabled },
          )
          SmokeButton(
            label = "Bg",
            selected = backgroundEnabled,
            onClick = { backgroundEnabled = !backgroundEnabled },
          )
        }
      }
      Text(
        text = "State: ${state.text.toString().replace("\n", "\\n")}",
        style = MaterialTheme.typography.labelSmall,
        color = Color(0xFF555555),
      )
    }

    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(16.dp),
      contentAlignment = Alignment.Center,
    ) {
      UnstyledTextField(
        state = state,
        modifier = textFieldModifier(
          sizeMode = sizeMode,
          borderEnabled = borderEnabled,
          backgroundEnabled = backgroundEnabled,
        ),
        contentPadding = paddingMode.padding,
        contentAlignment = contentAlignment,
        accessibilityLabel = "Smoke test text field",
        lineLimits = when (lineMode) {
          LineMode.Single -> TextFieldLineLimits.SingleLine
          LineMode.Multi -> TextFieldLineLimits.MultiLine()
        },
        cursorBrush = SolidColor(Color(0xFF8E44AD)),
        textStyle = MaterialTheme.typography.bodyMedium.merge(
          TextStyle(
            fontSize = textSize.sp,
            color = Color.Black,
          ),
        ),
        textAlign = textAlign,
      ) {
        Editable(
          placeholder = {
            Text(
              placeholderMode.text,
              maxLines = when (lineMode) {
                LineMode.Single -> 1
                LineMode.Multi -> Int.MAX_VALUE
              },
              softWrap = lineMode == LineMode.Multi,
              style = MaterialTheme.typography.bodyMedium.merge(
                TextStyle(
                  fontSize = textSize.sp,
                  color = Color.Black.copy(0.6f),
                ),
              ),
            )
          },
        )
      }
    }
  }
}

@Composable
private fun ControlGroup(
  label: String,
  content: @Composable () -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
    Text(label, style = MaterialTheme.typography.labelMedium)
    content()
  }
}

@Composable
private fun ContentAlignmentControls(
  selected: Alignment,
  onSelect: (Alignment) -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      SmokeButton("Top start", selected == Alignment.TopStart) { onSelect(Alignment.TopStart) }
      SmokeButton("Top center", selected == Alignment.TopCenter) { onSelect(Alignment.TopCenter) }
      SmokeButton("Top end", selected == Alignment.TopEnd) { onSelect(Alignment.TopEnd) }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      SmokeButton("Center start", selected == Alignment.CenterStart) {
        onSelect(Alignment.CenterStart)
      }
      SmokeButton("Center", selected == Alignment.Center) { onSelect(Alignment.Center) }
      SmokeButton("Center end", selected == Alignment.CenterEnd) { onSelect(Alignment.CenterEnd) }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      SmokeButton("Bottom start", selected == Alignment.BottomStart) {
        onSelect(Alignment.BottomStart)
      }
      SmokeButton("Bottom center", selected == Alignment.BottomCenter) {
        onSelect(Alignment.BottomCenter)
      }
      SmokeButton("Bottom end", selected == Alignment.BottomEnd) {
        onSelect(Alignment.BottomEnd)
      }
    }
  }
}

@Composable
private fun <T> ChoiceRow(
  values: List<T>,
  selected: T,
  label: (T) -> String,
  onSelect: (T) -> Unit,
) {
  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    values.forEach { value ->
      SmokeButton(
        label = label(value),
        selected = value == selected,
        onClick = { onSelect(value) },
      )
    }
  }
}

@Composable
private fun <T> ChoiceRow(
  values: Array<T>,
  selected: T,
  label: (T) -> String,
  onSelect: (T) -> Unit,
) {
  ChoiceRow(values = values.toList(), selected = selected, label = label, onSelect = onSelect)
}

@Composable
private fun SmokeButton(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
) {
  val shape = RoundedCornerShape(8.dp)
  UnstyledButton(
    onClick = onClick,
    modifier = Modifier
      .clip(shape)
      .background(if (selected) Color(0xFFE9DDF9) else Color.White, shape)
      .border(
        width = 1.dp,
        color = if (selected) Color(0xFF8E44AD) else Color(0xFFBDBDBD),
        shape = shape,
      ),
    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelLarge.merge(
        TextStyle(
          color = if (selected) Color(0xFF5E2B84) else Color(0xFF4A4A4A),
        ),
      ),
    )
  }
}

private fun textFieldModifier(
  sizeMode: SizeMode,
  borderEnabled: Boolean,
  backgroundEnabled: Boolean,
): Modifier {
  val shape = RoundedCornerShape(8.dp)
  var modifier: Modifier = Modifier

  modifier = when (sizeMode) {
    SizeMode.Wrap -> modifier
    SizeMode.Narrow -> modifier.width(72.dp)
    SizeMode.Wide -> modifier.width(340.dp)
    SizeMode.Tall -> modifier.height(140.dp)
    SizeMode.Box -> modifier.size(240.dp, 140.dp)
    SizeMode.Fill -> modifier.fillMaxWidth()
  }

  if (borderEnabled) {
    modifier = modifier.border(1.dp, Color(0xFFBDBDBD), shape)
  }
  if (backgroundEnabled) {
    modifier = modifier.background(Color.White, shape)
  }
  return modifier
}

private enum class SizeMode(val label: String) {
  Wrap("Wrap"),
  Narrow("72w"),
  Wide("340w"),
  Tall("140h"),
  Box("240x140"),
  Fill("Fill width"),
}

private enum class PaddingMode(
  val label: String,
  val padding: PaddingValues,
) {
  None("None", PaddingValues(0.dp)),
  Tight("Tight", PaddingValues(horizontal = 6.dp, vertical = 4.dp)),
  Roomy("Roomy", PaddingValues(horizontal = 16.dp, vertical = 12.dp)),
  Huge("Huge", PaddingValues(horizontal = 40.dp, vertical = 28.dp)),
}

private enum class LineMode {
  Single,
  Multi,
}

private enum class PlaceholderMode(
  val label: String,
  val text: String,
) {
  Email("Email", "email@example.com"),
  Short("Short", "email"),
  Long("Long", "a-placeholder-that-is-way-longer-than-the-editable"),
}

private enum class TextPreset(
  val label: String,
  val value: String,
) {
  Custom("Custom", ""),
  Empty("Empty", ""),
  A("A", "A"),
  Email("Email", "alex@example.com"),
  Long("Long", "some very long typed value that should expose sizing behavior"),
  Multi("Multi", "first line\nsecond line\nthird line"),
}

private enum class TextAlignMode(
  val label: String,
  val textAlign: TextAlign,
) {
  Unspecified("Unspecified", TextAlign.Unspecified),
  Start("Start", TextAlign.Start),
  Center("Center", TextAlign.Center),
  End("End", TextAlign.End),
}

private fun TextAlign.toMode(): TextAlignMode {
  return when (this) {
    TextAlign.Start -> TextAlignMode.Start
    TextAlign.Center -> TextAlignMode.Center
    TextAlign.End -> TextAlignMode.End
    else -> TextAlignMode.Unspecified
  }
}
