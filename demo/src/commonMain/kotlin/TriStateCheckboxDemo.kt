package com.composeunstyled.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Minus
import com.composeunstyled.Checkbox
import com.composeunstyled.Icon
import com.composeunstyled.Text
import com.composeunstyled.TriStateCheckbox

@Composable
fun TriStateCheckboxDemo() {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0)))),
        contentAlignment = Alignment.Center
    ) {
        val checkboxOptions = listOf("Option 1", "Option 2", "Option 3", "Option 4")
        var selected by remember { mutableStateOf(List(checkboxOptions.size) { false }) }

        val triState = when {
            selected.all { it } -> ToggleableState.On
            selected.none { it } -> ToggleableState.Off
            else -> ToggleableState.Indeterminate
        }

        Column(
            modifier = Modifier.widthIn(max = 300.dp).fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TriStateCheckbox(
                    value = triState,
                    onClick = {
                        val newState = when (triState) {
                            ToggleableState.Off -> true
                            ToggleableState.Indeterminate -> true
                            ToggleableState.On -> false
                        }
                        selected = List(checkboxOptions.size) { newState }
                    },
                    shape = RoundedCornerShape(4.dp),
                    backgroundColor = Color.White,
                    borderWidth = 1.dp,
                    borderColor = Color.Black.copy(0.33f),
                    modifier = Modifier.size(24.dp),
                    contentDescription = "Select all options"
                ) {
                    when (triState) {
                        ToggleableState.On -> Icon(Lucide.Check, contentDescription = null)
                        ToggleableState.Indeterminate -> Icon(Lucide.Minus, contentDescription = null)
                        ToggleableState.Off -> Unit
                    }
                }

                Spacer(Modifier.width(12.dp))
                Text("Select All", color = Color.White)
            }

            checkboxOptions.forEachIndexed { index, option ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 36.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selected[index],
                        onCheckedChange = { checked ->
                            selected = selected.toMutableList().apply {
                                this[index] = checked
                            }
                        },
                        shape = RoundedCornerShape(4.dp),
                        backgroundColor = Color.White,
                        borderWidth = 1.dp,
                        borderColor = Color.Black.copy(0.33f),
                        modifier = Modifier.size(24.dp),
                        contentDescription = option
                    ) {
                        Icon(Lucide.Check, contentDescription = null)
                    }

                    Spacer(Modifier.width(12.dp))
                    Text(option, color = Color.White)
                }
            }
        }
    }
}
