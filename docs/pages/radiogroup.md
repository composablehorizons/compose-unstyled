---
title: Radio Group
description: A foundational component for creating accessible radio groups in Jetpack Compose and Compose Multiplatform. Comes with full accessibility features and keyboard navigation, while letting you handle the styling to your liking.
---

<UnstyledDemo id="radiogroup" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-radio-group")
```

## Basic Example

To create a radio group use the `RadioGroup` component. Each radio option should be represented using the `Radio`
component.

The radio group handles its own state and sets important accessibility semantics.

You are free to represent a radio as anything you like, such as a checkbox icon or a typical radio like the following example:

```kotlin
val values = listOf("Light", "Dark", "System")
val groupState = rememberRadioGroupState(initialValue = values[0])

UnstyledRadioGroup(
    state = groupState,
    contentDescription = "Theme selection"
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        values.forEach { text ->
            val selected = groupState.selectedOption == text
            UnstyledRadioButton(
                value = text,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
                shape = RoundedCornerShape(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .dropShadow(
                            shape = CircleShape,
                            shadow = Shadow(
                                radius = 8.dp,
                                spread = 0.dp,
                                color = Color.Black.copy(alpha = 0.14f),
                                offset = DpOffset(x = 0.dp, y = 2.dp)
                            )
                        )
                        .clip(CircleShape)
                        .background(
                            if (selected) Color(0xFFB23A48) else Color.White
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
                BasicText(text)
            }
        }
    }
}

```

## Styling

Every component in Compose Unstyled is renderless. They handle all UX pattern logic, internal state, accessibility (
according to ARIA standards), and keyboard interactions for you, but they do not render any UI to the screen.

This is by design so that you can style your components exactly to your needs.

Most of the time, styling is done using `Modifiers` of your choice. However, sometimes this is not enough due to the
order of the `Modifier`s affecting the visual outcome.

For such cases we provide specific styling parameters.

## Code Example

### Toggle radio manually

You can control selection yourself by using the `Radio` overload with the `selected`/`onSelectedChange` params.

```kotlin
UnstyledRadioGroup(
    state = groupState,
    contentDescription = "Theme selection"
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        values.forEach { text ->
            val selected = groupState.selectedOption == text
            UnstyledRadioButton(
                selected = groupState.selectedOption == text,
                onSelectedChange = { groupState.selectedOption = text },
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
                shape = RoundedCornerShape(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .dropShadow(
                            shape = CircleShape,
                            shadow = Shadow(
                                radius = 8.dp,
                                spread = 0.dp,
                                color = Color.Black.copy(alpha = 0.14f),
                                offset = DpOffset(x = 0.dp, y = 2.dp)
                            )
                        )
                        .clip(CircleShape)
                        .background(
                            if (selected) Color(0xFFB23A48) else Color.White
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
                BasicText(text)
            }
        }
    }
}

```

## Keyboard Interactions

| Key                                   | Description                                                                                             |
|---------------------------------------|---------------------------------------------------------------------------------------------------------|
| <div class="keyboard-key">Enter</div> | Selects the radio button, triggering its onSelectedChange callback                                      |
| <div class="keyboard-key">Space</div> | Selects the radio button, triggering its onSelectedChange callback                                      |
| <div class="keyboard-key">⬇</div>     | Moves focus to the next radio button. If focus is on the last button, it moves to the first button.     |
| <div class="keyboard-key">➡</div>     | Moves focus to the next radio button. If focus is on the last button, it moves to the first button.     |
| <div class="keyboard-key">⬆</div>     | Moves focus to the previous radio button. If focus is on the first button, it moves to the last button. |
| <div class="keyboard-key">⬅</div>     | Moves focus to the previous radio button. If focus is on the first button, it moves to the last button. |

<ApiReference id="radiogroup" />
