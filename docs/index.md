# Compose Menu (Dropdown)

An unstyled Composable component for Compose Multiplatform that can be used to implement Dropdown Menus with the styling
of your choice.

Comes with built-in Keyboard management and animation support. Supports Compose Desktop, Web (WASM), Android and iOS.

<iframe style="border-radius: 20px;" width="800" height="340" src="preview/index.html" title="Demo" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>

## Installation

Ensure you have `mavenCentral()` to your `dependencyResolutionManagement{}` block, then include the dependency:

```kotlin title="build.gradle.kts"
dependencies {
    implementation("com.composables.ui:menu:0.0.2")
}
```
 
## Basic Example

There are four components that you will use to implement a dropdown: `Menu`, `MenuButton`, `MenuItems` and `MenuItem`.

The `Menu` wraps the `MenuButton` and the `MenuItems` components. When the `MenuButton` is clicked, the `MenuItems` will
be displayed on the screen at the position relative to the `Menu`.

The `MenuItems` component wraps multiple `MenuItem`s. When a `MenuItem` is clicked, the menu is dismissed.
Each `MenuItem` has a `onClick` parameter you can use for interaction purposes.

The menu's dropdown visibility is handled for you thanks to the `Menu`'s internal state.

```kotlin
val options = listOf("United States", "Greece", "Indonesia", "United Kingdom")
var selected by remember { mutableStateOf(0) }

Column(Modifier.fillMaxSize()) {
    Menu(Modifier.align(Alignment.End)) {
        MenuButton(Modifier.clip(RoundedCornerShape(6.dp)).border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(6.dp))) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text("Options", style = defaultTextStyle.copy(fontWeight = FontWeight(500)))
                Spacer(Modifier.width(4.dp))
                Image(ChevronDown, null)
            }
        }

        MenuItems(
            modifier = Modifier.width(320.dp).border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
                .background(Color.White).padding(4.dp),
            hideTransition = fadeOut()
        ) {
            Column {
                options.forEachIndexed { index, option ->
                    MenuItem(
                        modifier = Modifier.clip(RoundedCornerShape(4.dp)),
                        onClick = { selected = index }
                    ) {
                        Text(option, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp))
                    }
                }
            }
        }
    }
    Text("Selected = ${options[selected]}")
}

@Composable
fun Text(text: String, style: TextStyle = defaultTextStyle, modifier: Modifier = Modifier) {
    BasicText(text, style = style, modifier = modifier)
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

    MenuItems {
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

    MenuItems {
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

### Styling the MenuItems

The `MenuItems` component is a layout on which the menu's items will be displayed when the menu is expanded. In Material
Design this is often a card.

```kotlin hl_lines="6 7 8 9 10 11 12"
Menu {
    MenuButton(Modifier.clip(RoundedCornerShape(6.dp)).border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(6.dp))) {
        BasicText("Options", modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp))
    }

    MenuItems(
        modifier = Modifier.width(320.dp).border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
            .background(Color.White).padding(4.dp)
    ) {
        MenuItem(onClick = { selected = index }) {
            Text(option, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp))
        }
    }
}
```

## Animating the Menu

Modify the `enter` and `exit` parameters of the `MenuItems` component to modify the animation specs of the dropdown menu
when it is visible/hidden.

The `MenuItems` use the `AnimatedVisiblity` composable internally, which gives you a lot of flexibility towards what you
can achieve.

### Animation Recipes

#### Material Design Dropdown Animation

Material Design scales and fades the dropdown in and out.

```kotlin  hl_lines="3 4"
MenuItems(
    modifier = Modifier.width(320.dp).border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(4.dp)).background(Color.White)
        .padding(4.dp),
    showTransition =scaleIn(
        tween(durationMillis = 120, easing = LinearOutSlowInEasing),
        initialScale = 0.8f,
        transformOrigin = TransformOrigin(0f, 0f)
    ) + fadeIn(tween(durationMillis = 30)),
    hideTransition = scaleOut(tween(durationMillis = 1, delayMillis = 75), targetScale = 1f) + fadeOut(tween(durationMillis = 75))
) {
    MenuItem(onClick = { /* TODO */ }) {
        Basictext("Option 1")
    }
    MenuItem(onClick = { /* TODO */ }) {
        Basictext("Option 2")
    }
}

```

#### Mac OS Menu Animations

macOS shows the menu instantly on click, and quickly fades the menu out when dismissed:

```kotlin  hl_lines="1"
MenuItems(hideTransition = fadeOut(tween(durationMillis = 100, easing = LinearEasing))) {
    MenuItem(onClick = { /* TODO */ }) {
        Basictext("Option 1")
    }
}
```

### Styling touch presses and focus

`MenuItem`'s uses the default Compose mechanism for providing touch and focus feedback. Use the `LocalIndication`
CompositionLocal to override the default indication.

Here is an example of using Material Design's signature ripple feedback with your menu:

```kotlin
import androidx.compose.foundation.LocalIndication
import androidx.compose.material.ripple.rememberRipple

CompositionLocalProvider(LocalIndication provides rememberRipple()) {
    // MenuButton and MenuItems will use the ripple effect when focused and pressed

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

