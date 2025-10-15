package com.composeunstyled.demo

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composeunstyled.Button
import com.composeunstyled.Text

@Composable
internal fun SimpleButton(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(8.dp),
    interactionSource: androidx.compose.foundation.interaction.MutableInteractionSource? = null,
) {
    Button(
        onClick = {},
        shape = shape,
        modifier = modifier,
        borderWidth = 1.dp,
        borderColor = Color.Black.copy(alpha = 0.2f),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        interactionSource = interactionSource
    ) {
        Text("Button")
    }
}
