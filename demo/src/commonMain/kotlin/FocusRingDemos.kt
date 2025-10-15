package com.composeunstyled.demo

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.composeunstyled.focusRing

@Composable
fun FocusRingBasicDemo() {
    val interactionSource = remember { MutableInteractionSource() }

    SimpleButton(
        modifier = Modifier.focusRing(
            interactionSource = interactionSource,
            width = 2.dp,
            color = Color(0xFF3B82F6),
            shape = RoundedCornerShape(8.dp),
            offset = 2.dp
        ),
        interactionSource = interactionSource
    )
}

@Composable
fun FocusRingWidthDemo() {
    val interactionSource1 = remember { MutableInteractionSource() }
    val interactionSource2 = remember { MutableInteractionSource() }
    val interactionSource3 = remember { MutableInteractionSource() }

    SimpleButton(
        modifier = Modifier.focusRing(
            interactionSource = interactionSource1,
            width = 1.dp,
            color = Color(0xFF3B82F6),
            shape = RoundedCornerShape(8.dp),
            offset = 2.dp
        ),
        interactionSource = interactionSource1
    )
    SimpleButton(
        modifier = Modifier.focusRing(
            interactionSource = interactionSource2,
            width = 2.dp,
            color = Color(0xFF3B82F6),
            shape = RoundedCornerShape(8.dp),
            offset = 2.dp
        ),
        interactionSource = interactionSource2
    )
    SimpleButton(
        modifier = Modifier.focusRing(
            interactionSource = interactionSource3,
            width = 4.dp,
            color = Color(0xFF3B82F6),
            shape = RoundedCornerShape(8.dp),
            offset = 2.dp
        ),
        interactionSource = interactionSource3
    )
}

@Composable
fun FocusRingShapeDemo() {
    val interactionSource1 = remember { MutableInteractionSource() }
    val interactionSource2 = remember { MutableInteractionSource() }
    val interactionSource3 = remember { MutableInteractionSource() }

    SimpleButton(
        shape = RectangleShape,
        modifier = Modifier.focusRing(
            interactionSource = interactionSource1,
            width = 2.dp,
            color = Color(0xFF3B82F6),
            shape = RectangleShape,
            offset = 2.dp
        ),
        interactionSource = interactionSource1
    )
    SimpleButton(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.focusRing(
            interactionSource = interactionSource2,
            width = 2.dp,
            color = Color(0xFF3B82F6),
            shape = RoundedCornerShape(8.dp),
            offset = 2.dp
        ),
        interactionSource = interactionSource2
    )
    SimpleButton(
        shape = CircleShape,
        modifier = Modifier.focusRing(
            interactionSource = interactionSource3,
            width = 2.dp,
            color = Color(0xFF3B82F6),
            shape = CircleShape,
            offset = 2.dp
        ),
        interactionSource = interactionSource3
    )
}

@Composable
fun FocusRingOffsetDemo() {
    val interactionSource1 = remember { MutableInteractionSource() }
    val interactionSource2 = remember { MutableInteractionSource() }
    val interactionSource3 = remember { MutableInteractionSource() }

    SimpleButton(
        modifier = Modifier.focusRing(
            interactionSource = interactionSource1,
            width = 2.dp,
            color = Color(0xFF3B82F6),
            shape = RoundedCornerShape(8.dp),
            offset = 0.dp
        ),
        interactionSource = interactionSource1
    )
    SimpleButton(
        modifier = Modifier.focusRing(
            interactionSource = interactionSource2,
            width = 2.dp,
            color = Color(0xFF3B82F6),
            shape = RoundedCornerShape(8.dp),
            offset = 4.dp
        ),
        interactionSource = interactionSource2
    )
    SimpleButton(
        modifier = Modifier.focusRing(
            interactionSource = interactionSource3,
            width = 2.dp,
            color = Color(0xFF3B82F6),
            shape = RoundedCornerShape(8.dp),
            offset = 8.dp
        ),
        interactionSource = interactionSource3
    )
}

@Composable
fun FocusRingColorDemo() {
    val interactionSource1 = remember { MutableInteractionSource() }
    val interactionSource2 = remember { MutableInteractionSource() }
    val interactionSource3 = remember { MutableInteractionSource() }

    SimpleButton(
        modifier = Modifier.focusRing(
            interactionSource = interactionSource1,
            width = 2.dp,
            color = Color(0xFFEF4444),
            shape = RoundedCornerShape(8.dp),
            offset = 2.dp
        ),
        interactionSource = interactionSource1
    )
    SimpleButton(
        modifier = Modifier.focusRing(
            interactionSource = interactionSource2,
            width = 2.dp,
            color = Color(0xFF10B981),
            shape = RoundedCornerShape(8.dp),
            offset = 2.dp
        ),
        interactionSource = interactionSource2
    )
    SimpleButton(
        modifier = Modifier.focusRing(
            interactionSource = interactionSource3,
            width = 2.dp,
            color = Color(0xFF8B5CF6),
            shape = RoundedCornerShape(8.dp),
            offset = 2.dp
        ),
        interactionSource = interactionSource3
    )
}