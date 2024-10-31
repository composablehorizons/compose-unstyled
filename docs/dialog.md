---
title: Dialog
description: A stackable, renderless, highly performant foundational component to build modal bottom sheets with, jam-packed with styling features without compromising on accessibility or keyboard interactions.
---

# Dialog

An unstyled Dialog component that can be used to implement Dialogs with the styling of your choice.

Fully accessible, supports animations, offers consistent behavior across platforms and an optional background scrim.

<div style="position: relative; max-width: 800px; height: 340px; border-radius: 20px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../dialog-demo/index.html" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
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

A dialog consists of the following components: `Dialog`, `DialogPanel` and the optional `Scrim`.

The `Dialog` controls the visibility of the dialog via the `DialogState` object.

The `DialogPanel` is a container component that renders the dialog's panel and its contents.

The optional `Scrim` component is used to add layer behind the dialog and dim the rest of the UI.

```kotlin
val dialogState = rememberDialogState()

Box {
    Box(Modifier.clickable { dialogState.visible = true }) {
        BasicText("Show Dialog")
    }
    Dialog(state = dialogState) {
        DialogPanel(
            modifier = Modifier
                .displayCutoutPadding()
                .systemBarsPadding()
                .widthIn(min = 280.dp, max = 560.dp)
                .padding(20.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFE4E4E4), RoundedCornerShape(12.dp))
                .background(Color.White),
        ) {
            Column {
                Column(Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp)) {
                    BasicText(
                        text = "Update Available",
                        style = TextStyle(fontWeight = FontWeight.Medium)
                    )
                    Spacer(Modifier.height(8.dp))
                    BasicText(
                        text = "A new version of the app is available. Please update to the latest version.",
                        style = TextStyle(color = Color(0xFF474747))
                    )
                }
                Spacer(Modifier.height(24.dp))
                Box(Modifier.padding(12.dp)
                    .align(Alignment.End)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(role = Role.Button) { /* TODO */ }
                    .padding(horizontal = 12.dp, vertical = 8.dp)) {
                    BasicText(
                        text = "Update",
                        style = TextStyle(color = Color(0xFF6699FF))
                    )
                }
            }
        }
    }
}
```

## Styling

Any sort of styling is done by the `Modifier` of the respective component.

Changing the looks of the dialog's panel is done by passing the respective styling `Modifier`s to your `DialogPanel`:

```kotlin
Dialog(state = rememberDialogState(visible = true)) {
    DialogPanel(
        modifier = Modifier.systemBarsPadding()
            .widthIn(min = 280.dp, max = 560.dp)
            .padding(20.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE4E4E4), RoundedCornerShape(12.dp))
            .background(Color.White)
    ) {
        Column {
            BasicText("Something important happened")
            Box(Modifier.clickable { /* TODO */ }) {
                BasicText("Got it")
            }
        }
    }
}
```

## Code Examples

### Showing/Hide the dialog

Pass your own `DialogState` to the `Dialog` and change the *visible* property according to your needs:

```kotlin
val state = rememberDialogState()

Box(Modifier.clickable { state.visible = true }) {
    BasicText("Show dialog")
}
Dialog(state = state) {
    DialogPanel {
        Column {
            BasicText("Something important happened")
            Box(Modifier.clickable { state.visible = false }) {
                BasicText("Got it")
            }
        }
    }
}
```

The Dialog will also be automatically dismissed by default if the user taps outside the DialogPanel or presses the '
Escape' or 'Back' button on their device.

In override to override this behavior pass the `DialogProperties` object to the Dialog with the desired properties:

```kotlin
Dialog(
    state = rememberDialogState(),
    properties = DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = false)
) {
    // TODO the rest of your dialog
}
```

### Adding a scrim

Use the `Scrim` component within your `Dialog`:

```kotlin
val state = rememberDialogState()

Box(Modifier.clickable { state.visible = true }) {
    BasicText("Show dialog")
}
Dialog(state = state) {
    Scrim()
    DialogPanel {
        Column {
            BasicText("Something important happened")
            Box(Modifier.clickable { state.visible = false }) {
                BasicText("Got it")
            }
        }
    }
}
```

The `Scrim` is customizable and you can pass any *scrimColor*, and *enter*/*exit* transitions that matches your design
specs.

### Scrollable dialogs

Add any scrollable component such as `LazyColumn` to the contents of your dialog:

```kotlin
val state = rememberDialogState()

Box(Modifier.clickable { state.visible = true }) {
    BasicText("Show dialog")
}
Dialog(state = state) {
    DialogPanel {
        Column {
            LazyColumn(Modifier.height(320.dp)) {
                item { BasicText("Something important happened") }
                repeat(100) { i ->
                    item { BasicText("Update number ${i}") }
                }
            }
            Box(Modifier.clickable { state.visible = false }) {
                BasicText("Got it")
            }
        }
    }
}
```

### Full-screen dialogs

Pass a `Modifier.fillMaxSize()` to the `DialogPanel`'s modifier parameter. Make sure to pass
the `Modifier.systemBarsPadding()` and `Modifier.displayCutoutPadding()` or any related inset Modifier so that the
dialog is not drawn behind any system
bars (such as status and navigation bar on Android):

```kotlin
Dialog(state = rememberDialogState(visible = true)) {
    DialogPanel(
        modifier = Modifier
            .displayCutoutPadding()
            .systemBarsPadding()
            .fillMaxSize()
    ) {
        Column {
            BasicText("This is a full screen dialog")
            Box(Modifier.clickable { state.visible = false }) {
                BasicText("Got it")
            }
        }
    }
}
```

### Adding transitions

Add any *enter*/*exit* transitions into the `DialogPanel` and `Scrim` to control how they appear on the screen when they
enter and exit the composition:

```kotlin
Dialog(state = rememberDialogState(visible = true)) {
    Scrim(enter = fadeIn(), exit = fadeOut())
    DialogPanel(
        enter = scaleIn(initialScale = 0.8f) + fadeIn(tween(durationMillis = 250)),
        exit = scaleOut(targetScale = 0.6f) + fadeOut(tween(durationMillis = 150)),
    ) {
        // TODO the dialog's contents
    }
}
```

### Styling the system bars

**Android only**

Dialogs will not change the color of your system UI when displayed. We provide a `LocalModalWindow` composition local,
which provides you with the Android `Window` that hosts the dialog, so that you can customize the System UI according to
your needs:

```kotlin
Dialog(rememberDialogState()) {
    DialogPanel {
        val window = LocalModalWindow.current
        LaunchedEffect(Unit) {
            // change system bars to transparent
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()

            // don't forget to update the icons too
            val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
            windowInsetsControllerCompat.isAppearanceLightStatusBars = true
            windowInsetsControllerCompat.isAppearanceLightNavigationBars = true
        }
        BasicText("Transparent bars. So cool 😎 ", modifier = Modifier.navigationBarsPadding())
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

| Key                                         | Description                                                                                  |
|---------------------------------------------|----------------------------------------------------------------------------------------------|
| <div class="keyboard-key">Esc</div>         | Closes any open dialogs, if the *dismissOnBackPress* of `DialogProperties()` is set to true. |
| <div class="keyboard-key">Back</div>        | Closes any open dialogs, if the *dismissOnBackPress* of `DialogProperties()` is set to true. |
| <div class="keyboard-key">Tab</div>         | Cycles through the dialog's contents.                                                        |
| <div class="keyboard-key">Shift + Tab</div> | Cycles through the dialog's contents in backwards order.                                     |

## Parameters

### Dialog

The main component.

| Parameter    | Description                                                                                                                   |
|--------------|-------------------------------------------------------------------------------------------------------------------------------|
| `state`      | A `DialogState` object which controls the visibility of the dialog.                                                           |
| `properties` | Properties that control when the dialog needs to be dismissed (such as clicking outside of the panel or pressing Esc or Back. |
| `onDismiss`  | Called when the dialog is being dismissed either by tapping outside or by pressing `Esc` or `Back`.                           |
| `content`    | A `@Composable` function that provides a `DialogScope`.                                                                       |

### DialogPanel

The visual representation of your dialog. Can only be used from a `DialogScope`.

| Parameter    | Description                                                                                                                   |
|--------------|-------------------------------------------------------------------------------------------------------------------------------|
| `state`      | A `DialogState` object which controls the visibility of the dialog.                                                           |
| `properties` | Properties that control when the dialog needs to be dismissed (such as clicking outside of the panel or pressing Esc or Back. |
| `content`    | A `@Composable` function that provides a `DialogScope`.                                                                       |

### Scrim

The dimming layer that is often placed behind a `DialogPanel`, to let the user focus on the dialog. Can only be used
from a `DialogScope`.

| Parameter    | Description                                                                       |
|--------------|-----------------------------------------------------------------------------------|
| `modifier`   | `Modifier` for the Scrim                                                          |
| `scrimColor` | Controls the color of the Scrim. The default color is Black with an alpha of 60%. |
| `enter`      | The `EnterTransition` when the Scrim enters the composition                       |
| `exit`       | The `ExitTransition` when the Scrim enters the composition                        |

## Compose Unstyled Dialog vs Compose Dialog

Compose Multiplatform's original Dialog component does not support custom animations. Even though it is possible to
animate it, it requires you to combine multiple components together and sync state to animations to composition which is
not
straightforward to do.

In addition, the Jetpack Compose (Android) Dialog comes with the original Android's dialog width and inset constraints,
which are historically a pain to deal with and customize.

Our Dialog is designed to be customizable inside out and work the same way on every platform without surprises. It
behaves like any other component. If for example you need a full
screen dialog, all you have to do is pass `Modifier.fillMaxSize()` to the `DialogPanel`, without having to worry about
platform flags.

## Styled Examples

<a href="https://composablesui.com?ref=core">

Looking for styled components for Jetpack Compose or Compose Multiplatform?

Explore a rich collection of production ready examples at <span style="color: #E91E63; font-weight: 500">
ComposablesUi.com</span>

<img src="../composablesui-banner.jpg" alt="Composables UI" style="width: 100%; max-width: 800px">
</a>