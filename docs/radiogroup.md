---
title: Radio Group
description: A foundational component for creating accessible radio groups in Compose Multiplatform.
---

# Radio Group

A foundational component used to create accessible radio groups in Compose Multiplatform.

Comes with full accessibility features and keyboard navigation, while letting you handle the styling to your liking.

<div style="position: relative; max-width: 800px; height: 340px; border-radius: 20px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../radiogroup-demo/index.html" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Installation

```kotlin title="build.gradle.kts"
dependencies {
    implementation("com.composables:core:1.30.0")
}
```

## Basic Example

To create a radio group use the `RadioGroup` component. Each radio option should be represented using the `Radio`
component.

The radio group handles its own state and sets important accessibility semantics.

You are free to represent a radio as anything you like, such as a checkbox icon or a typical radio like the following example:

```kotlin
val values = listOf("Light", "Dark", "System")
val groupState = rememberRadioGroupState(initialValue = values[0])

RadioGroup(
    state = groupState,
    contentDescription = "Theme selection"
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                Text(text)
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
RadioGroup(
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
            Radio(
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
                        .shadow(elevation = 4.dp, RoundedCornerShape(8.dp))
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
                Text(text)
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

## Component API

### RadioGroupState

| Parameter        | Description                                       |
|------------------|---------------------------------------------------|
| `selectedOption` | The currently selected option in the radio group. |

### rememberRadioGroupState

| Parameter      | Description                                            |
|----------------|--------------------------------------------------------|
| `initialValue` | The initial selected option for the radio group state. |

### RadioGroup

| Parameter            | Description                                                 |
|----------------------|-------------------------------------------------------------|
| `state`              | The state of the radio group, managing the selected option. |
| `contentDescription` | Accessibility description of the radio group.               |
| `modifier`           | Modifier to be applied to the radio group.                  |
| `content`            | Composable function to define the radio buttons.            |

### Radio

| Parameter               | Description                                                    |
|-------------------------|----------------------------------------------------------------|
| `value`                 | The value associated with the radio button.                    |
| `modifier`              | Modifier to be applied to the radio button.                    |
| `backgroundColor`       | Background color of the radio button.                          |
| `contentColor`          | Color of the content inside the radio button.                  |
| `selectedColor`         | Color when the radio button is selected.                       |
| `enabled`               | Whether the radio button is enabled.                           |
| `shape`                 | Shape of the radio button.                                     |
| `borderColor`           | Color of the border.                                           |
| `borderWidth`           | Width of the border.                                           |
| `contentPadding`        | Padding inside the radio button.                               |
| `interactionSource`     | Interaction source for the radio button.                       |
| `indication`            | Visual indication for interactions.                            |
| `horizontalArrangement` | Horizontal arrangement of the content.                         |
| `verticalAlignment`     | Vertical alignment of the content.                             |
| `content`               | Composable function to define the content of the radio button. |

<style>
.keyboard-key {
  background-color: #EEEEEE;
  color: black;
  text-align: center;
  border-radius: 4px;
}
</style>
