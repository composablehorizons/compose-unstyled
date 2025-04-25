package com.composeunstyled.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composeunstyled.Radio
import com.composeunstyled.RadioGroup
import com.composeunstyled.Text
import com.composeunstyled.rememberRadioGroupState

@Composable
fun RadioGroupDemo() {
    val values = listOf("Light", "Dark", "System")
    val groupState = rememberRadioGroupState(
        initialValue = values[0]
    )

    Box(
        modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFFEC6F66), Color(0xFFF3A183)))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.width(300.dp).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RadioGroup(
                state = groupState,
                modifier = Modifier.fillMaxWidth(),
                contentDescription = "Theme selection"
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    values.forEach { text ->
                        val selected = groupState.selectedOption == text
                        Radio(
                            value = text,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .shadow(elevation = 4.dp, RoundedCornerShape(8.dp))
                                    .clip(CircleShape)
                                    .background(
                                        if(selected) Color(0xFFB23A48) else Color.White
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .alpha(if (selected) 1f else 0f)
                                        .background(Color.White)
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Text(text)
                        }
                    }
                }
            }
        }
    }
}
