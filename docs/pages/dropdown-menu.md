---
title: Dropdown Menu
description: An unstyled component for Jetpack Compose and Compose Multiplatform that can be used to implement Dropdown Menus with the styling of your choice. Fully accessible, supports keyboard navigation and open/close animations.
---

<UnstyledDemo id="dropdown-menu" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-dropdown-menu")
```

## Basic Example

Use `UnstyledDropdownMenu` with a controlled `expanded` value. The `anchor` slot renders the trigger, and the `panel` slot renders the floating menu content.

```kotlin
val options = listOf("United States", "Greece", "Indonesia", "United Kingdom")
var selected by remember { mutableStateOf(0) }
var expanded by remember { mutableStateOf(false) }

UnstyledDropdownMenu(
    expanded = expanded,
    onExpandedChange = { expanded = it },
    anchor = {
        UnstyledButton(onClick = { expanded = true }) {
            BasicText("Options")
        }
    },
    panel = {
        DropdownMenuPanel(
            modifier = Modifier
                .width(320.dp)
                .background(Color.White, RoundedCornerShape(4.dp))
                .padding(4.dp),
            exit = fadeOut()
        ) {
            options.forEachIndexed { index, option ->
                MenuItem(
                    onClick = {
                        selected = index
                        expanded = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BasicText(option, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp))
                }
            }
        }
    }
)
```

## Code Examples

### Toggling the Menu

Change the controlled `expanded` value to show or hide the menu:

```kotlin
var expanded by remember { mutableStateOf(false) }

UnstyledDropdownMenu(
    expanded = expanded,
    onExpandedChange = { expanded = it },
    anchor = {
        UnstyledButton(onClick = { expanded = true }) {
            BasicText("Toggle the menu")
        }
    },
    panel = {
        DropdownMenuPanel {
            MenuItem(onClick = { expanded = false }) {
                BasicText("Close this menu")
            }
        }
    }
)
```

### Positioning the menu

Use `side`, `alignment`, `sideOffset`, and `alignmentOffset` to place the panel relative to the anchor.

```kotlin
UnstyledDropdownMenu(
    expanded = expanded,
    onExpandedChange = { expanded = it },
    side = AnchorSide.Bottom,
    alignment = AnchorAlignment.End,
    sideOffset = 8.dp,
    anchor = {
        UnstyledButton(onClick = { expanded = true }) {
            BasicText("Toggle the menu")
        }
    },
    panel = {
        DropdownMenuPanel {
            MenuItem(onClick = { /* TODO */ }) {
                BasicText("Option")
            }
        }
    }
)
```

## Styling

Compose Unstyled does not draw the menu trigger, panel, or items for you. Apply visual styling to the anchor content, `DropdownMenuPanel`, and each `MenuItem` modifier.

```kotlin
DropdownMenuPanel(
    modifier = Modifier
        .width(320.dp)
        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
        .background(Color.White, RoundedCornerShape(4.dp))
        .padding(4.dp)
) {
    MenuItem(
        onClick = { /* TODO */ },
        modifier = Modifier.fillMaxWidth()
    ) {
        BasicText("Option", modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp))
    }
}
```

## Animating the Menu

Modify the `enter` and `exit` parameters of `DropdownMenuPanel` to customize how the dropdown appears and disappears.

```kotlin
DropdownMenuPanel(
    modifier = Modifier
        .width(320.dp)
        .background(Color.White, RoundedCornerShape(4.dp))
        .padding(4.dp),
    enter = scaleIn(tween(durationMillis = 120, easing = LinearOutSlowInEasing), initialScale = 0.8f, transformOrigin = TransformOrigin(0f, 0f)) + fadeIn(tween(durationMillis = 30)),
    exit = scaleOut(tween(durationMillis = 1, delayMillis = 75), targetScale = 1f) + fadeOut(tween(durationMillis = 75))
) {
    MenuItem(onClick = { /* TODO */ }) {
        BasicText("Option 1")
    }
    MenuItem(onClick = { /* TODO */ }) {
        BasicText("Option 2")
    }
}
```

## Styling touch presses and focus

`MenuItem` uses the default Compose mechanism for touch and focus feedback. Pass `indication` or provide a custom `LocalIndication` when you need a different interaction effect.

```kotlin
CompositionLocalProvider(LocalIndication provides rememberRipple()) {
    DropdownMenuPanel {
        MenuItem(onClick = { /* TODO */ }) {
            BasicText("Close this menu")
        }
    }
}
```

## Keyboard Interactions

<style>
.keyboard-key {
  background-color: #EEEEEE;
  color: black;
  text-align: center;
  border-radius: 4px;
}
</style>

| Key                                   | Description                                                                                                                 |
|---------------------------------------|-----------------------------------------------------------------------------------------------------------------------------|
| <div class="keyboard-key">Enter</div> | Opens the menu from the anchor. Performs a click when a `MenuItem` is focused. |
| <div class="keyboard-key">⬇</div>     | Opens the menu from the anchor. Moves focus to the next `MenuItem` when the panel is expanded. |
| <div class="keyboard-key">⬆</div>     | Moves focus to the previous `MenuItem` when the panel is expanded. |
| <div class="keyboard-key">Esc</div>   | Closes the menu when the panel is expanded. |
