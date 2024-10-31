---
title: Dropdown Menu
description: A stackable, renderless, highly performant foundational component to build modal bottom sheets with, jam-packed with styling features without compromising on accessibility or keyboard interactions.
---
# Dropdown Menu

An unstyled component for Compose Multiplatform that can be used to implement Dropdown Menus with the styling
of your choice. 

Fully accessible, supports keyboard navigation and open/close animations.

<div style="position: relative; max-width: 800px; height: 340px; border-radius: 20px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../menu-demo/index.html" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Installation

```kotlin title="build.gradle.kts"
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.composables:core:1.19.1")
}
```

## Basic Example

There are four components that you will use to implement a dropdown: `Menu`, `MenuButton`, `MenuContent` and `MenuItem`.

The `Menu` wraps the `MenuButton` and the `MenuContent` components. When the `MenuButton` is clicked, the `MenuContent` will
be displayed on the screen at the position relative to the `Menu`.

The `MenuContent` component wraps multiple `MenuItem`s. When a `MenuItem` is clicked, the menu is dismissed.
Each `MenuItem` has a `onClick` parameter you can use for interaction purposes.

The menu's dropdown visibility is handled for you thanks to the `Menu`'s internal state.

```kotlin
val options = listOf("United States", "Greece", "Indonesia", "United Kingdom")
var selected by remember { mutableStateOf(0) }
val state = rememberMenuState(expanded = true)

Column(Modifier.fillMaxSize()) {
    Menu(state, modifier = Modifier.align(Alignment.End)) {
        MenuButton(
            Modifier.clip(RoundedCornerShape(6.dp))
                .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(6.dp))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicText("Options", style = defaultTextStyle.copy(fontWeight = FontWeight(500)))
                Spacer(Modifier.width(4.dp))
                Image(ChevronDown, null)
            }
        }

        MenuContent(
            modifier = Modifier.width(320.dp)
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
                .background(Color.White)
                .padding(4.dp),
            exit = fadeOut()
        ) {
            options.forEachIndexed { index, option ->
                MenuItem(
                    modifier = Modifier.clip(RoundedCornerShape(4.dp)),
                    onClick = { selected = index }
                ) {
                    BasicText(option, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp))
                }
            }
        }
    }
    BasicText("Selected = ${options[selected]}")
}
```

## Code Examples

### Toggling the Menu

Pass your own `MenuState` to the `Menu` and change the *expanded* property according to your needs:

```kotlin
val state = rememberMenuState(expanded = true)

Menu(state = state) {
    MenuButton {
        BasicText("Toggle the menu")
    }

    MenuContent {
        MenuItem(onClick = { state.expanded = false }) {
            BasicText("Close this menu")
        }
    }
}
```

### Positioning the menu

This option is useful if you want to left align, center align or right align the `MenuButton` and the `MenuContent` when expanded.

```kotlin
Menu {
    MenuButton {
        BasicText("Toggle the menu")
    }

    MenuContent(alignment = Alignment.End) {
        MenuItem(onClick = { /* TODO */ }) {
            BasicText("Option")
        }
    }
}
```

## Styling

By default, the Menu component comes with no styling. This is by design as it is intended to be used as a building block
for your own design system's menus.

The `Menu` composable is used as an anchor point. Do not pass any styling to its `modifier`. Instead, use its `modifier`
parameter for anchoring and positioning needs (such as `Modifier.align()`).

The `MenuButton` is the composable responsible to handle clicking into showing and hiding the dropdown menu.

The following sample shows the minimum setup you need to display something on the screen:

```kotlin
Menu {
    MenuButton {
        BasicText("Show Options")
    }

    MenuContent {
        MenuItem(onClick = { /* TODO handle click */ }) {
            BasicText("Option 1")
        }
        MenuItem(onClick = { /* TODO handle click */ }) {
            BasicText("Option 2")
        }
        MenuItem(onClick = { /* TODO handle click */ }) {
            BasicText("Option 3")
        }
    }
}
```

However, the result will not look pretty. The following section goes over how to style each component to achieve the
visual results you want.

### Styling the Menu Button

Pass the desired styling to the `MenuButton`'s `modifier`. Do not pass any padding to it, as the `MenuButton` handles
click events internally and this will affect the interaction bounds.

Instead, provide any content padding to the contents of the button instead:

```kotlin hl_lines="2 3 4"
Menu {
    MenuButton(Modifier.clip(RoundedCornerShape(6.dp)).border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(6.dp))) {
        BasicText("Options", modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp))
    }

    MenuContent {
        MenuItem(onClick = { /* TODO handle click */ }) {
            BasicText("Option 1")
        }
        MenuItem(onClick = { /* TODO handle click */ }) {
            BasicText("Option 2")
        }
        MenuItem(onClick = { /* TODO handle click */ }) {
            BasicText("Option 3")
        }
    }
}
```

### Styling the MenuContent

The `MenuContent` component is a layout on which the menu's items will be displayed when the menu is expanded. In Material
Design this is often a card.

```kotlin hl_lines="6 7 8 9 10"
Menu {
    MenuButton(Modifier.clip(RoundedCornerShape(6.dp)).border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(6.dp))) {
        BasicText("Options", modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp))
    }

    MenuContent(Modifier.width(320.dp).border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(4.dp)).background(Color.White).padding(4.dp)) {
        MenuItem(onClick = { selected = index }) {
            Text(option, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp))
        }
    }
}
```

## Animating the Menu

Modify the `showTransition` and `hideTransition` parameters of the `MenuContent` component to modify the animation specs of the dropdown menu
when it is visible/hidden.

The `MenuContent` use the `AnimatedVisiblity` composable internally, which gives you a lot of flexibility towards what you
can achieve.

### Animation Recipes

#### Material Design Dropdown

Material Design scales and fades the dropdown in and out.

```kotlin  hl_lines="3 4"
MenuContent(
    modifier = Modifier.width(320.dp).border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(4.dp)).background(Color.White).padding(4.dp),
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

#### Mac OS Menu

macOS shows the menu instantly on click, and quickly fades the menu out when dismissed:

```kotlin  hl_lines="1"
MenuContent(exit = fadeOut(tween(durationMillis = 100, easing = LinearEasing))) {
    MenuItem(onClick = { /* TODO */ }) {
        BasicText("Option 1")
    }
}
```

## Styling touch presses and focus

`MenuItem`'s uses the default Compose mechanism for providing touch and focus feedback. Use the `LocalIndication`
CompositionLocal to override the default indication.

Here is an example of using Material Design's signature ripple feedback with your menu:

```kotlin
import androidx.compose.foundation.LocalIndication
import androidx.compose.material.ripple.rememberRipple

CompositionLocalProvider(LocalIndication provides rememberRipple()) {
    // MenuButton and MenuContent will use the ripple effect when focused and pressed

    Menu {
        // TODO implement the rest of the menu
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
| <div class="keyboard-key">Enter</div> | Opens the Menu, if the `MenuButton` is focused. Performs a click, when a `MenuItem` is focused.                             |
| <div class="keyboard-key">⬇</div>     | Opens the Menu, if the `MenuButton` is focused. Moves focus to the next `MenuItem` if the `Menu` is expanded.               |
| <div class="keyboard-key">⬆</div>     | Moves focus to the previous `MenuItem` if the `Menu` is expanded.                                                           |
| <div class="keyboard-key">Esc</div>   | Closes the Menu, if the Menu is expanded and moves focus to the `MenuButton`. Removes focus if the `MenuButton` is focused. |

## Styled Examples

<a href="https://composablesui.com?ref=core">

Looking for styled components for Jetpack Compose or Compose Multiplatform?

Explore a rich collection of production ready examples at <span style="color: #E91E63; font-weight: 500">ComposablesUi.com</span>

<img src="../composablesui-banner.jpg" alt="Composables UI" style="width: 100%; max-width: 800px">
</a>