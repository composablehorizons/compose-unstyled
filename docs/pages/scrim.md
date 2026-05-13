---
title: Scrim
description: A renderless modal scrim primitive for dimming content behind modal surfaces.
---

```kotlin
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composeunstyled.Modal
import com.composeunstyled.UnstyledButton
import com.composeunstyled.Scrim
import com.composeunstyled.modalFragment
import com.composeunstyled.rememberModalState

@Composable
fun ScrimExample() {
    val modalState = rememberModalState(initiallyVisible = false)

    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        UnstyledButton(
            onClick = { modalState.transitionState.targetState = true },
            borderColor = Color(0xFF18181B),
            borderWidth = 1.dp,
            backgroundColor = Color.Transparent,
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 11.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            BasicText(
                text = "Show Scrim",
                style = TextStyle(
                    color = Color(0xFF18181B),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }

    Modal(state = modalState) {
        Scrim(
            scrimColor = Color.Black.copy(alpha = 0.24f),
            enter = fadeIn(tween(durationMillis = 220)),
            exit = fadeOut(tween(durationMillis = 180))
        )
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                Modifier
                    .modalFragment()
                    .widthIn(max = 280.dp)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BasicText(
                    text = "Background content is dimmed.",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                UnstyledButton(
                    onClick = { modalState.transitionState.targetState = false },
                    borderColor = Color.White,
                    borderWidth = 1.dp,
                    backgroundColor = Color.Transparent,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    BasicText(
                        text = "Dismiss",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}
```

## Installation

```kotlin
implementation("com.composables:composeunstyled-scrim")
```

## Basic Example

Use `Scrim` inside modal content when you want to dim everything behind the modal surface.

```kotlin
Modal {
    Scrim()

    Box(
        modifier = Modifier
            .padding(24.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
    ) {
        BasicText("Modal content")
    }
}
```

## Styling

The scrim renders a full-size layer using the current modal state. Pass `scrimColor`, `enter`, and `exit` to customize its color and transitions.
