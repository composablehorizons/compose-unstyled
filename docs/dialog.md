# Dialog

An unstyled Dialog component for Composable Multiplatform that can be used to implement Dialogs with the styling of your
choice.

Fully accessible, supports animations, offers consistent behavior across platforms and an optional background scrim.

<div style="position: relative; max-width: 800px; height: 340px; border-radius: 20px; overflow: hidden;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../dialog-demo/index.html" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Installation

```kotlin title="build.gradle.kts"
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.composables:core:1.4.0")
}
```

## Basic Example

A dialog consists of the following components: `Dialog`, `DialogContent` and the optional `Scrim`.

The `Dialog` controls the visibility of the dialog via the `DialogState` object.

The `DialogContent` renders the

The optional `Scrim` component is used to add layer behind the dialog

```kotlin
val dialogState = rememberDialogState()

Box {
    Box(Modifier.clickable { dialogState.visible = true }) {
        BasicText("Show Dialog")
    }
    Dialog(state = dialogState) {
        DialogContent(
            modifier = Modifier.systemBarsPadding()
                .widthIn(min = 280.dp, max = 560.dp)
                .padding(20.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFE4E4E4), RoundedCornerShape(12.dp))
                .background(Color.White),
            enter = fadeIn(),
            exit = fadeOut()
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

Changing the looks of the dialog's panel is done by passing the respective styling `Modifier`s to your `DialogContent`:

```kotlin
Dialog(state = rememberDialogState(visible = true)) {
    DialogContent(
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
    DialogContent {
        Column {
            BasicText("Something important happened")
            Box(Modifier.clickable { state.visible = false }) {
                BasicText("Got it")
            }
        }
    }
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
    DialogContent {
        Column {
            BasicText("Something important happened")
            Box(Modifier.clickable { state.visible = false }) {
                BasicText("Got it")
            }
        }
    }
}
```

The `Scrim` is customizable and you can pass any *scrimColor*, and *enter*/*exit* transitions that matches your design specs.

### Scrollable dialogs

Add any scrollable component such as `LazyColumn` to the contents of your dialog:

```kotlin
val state = rememberDialogState()

Box(Modifier.clickable { state.visible = true }) {
    BasicText("Show dialog")
}
Dialog(state = state) {
    DialogContent {
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

### Adding transitions

## Keyboard Interactions

Esc

## Component API

### Dialog

### DialogContent

### Scrim

## Core Dialog vs Compose Dialog

Compose Multiplatform's original Dialog component does not support custom animations. Even though it is possible to
animate it, it requires you to combine multiple components together and sync state to animations to composition which is not
straightforward to do.

In addition, the Jetpack Compose (Android) Dialog comes with the original Android's dialog width and inset constraints,
which are historically a pain to deal with and customize.

Our Dialog is designed to be customizable inside out and work the same way on every platform without surprises. It
behaves like any other component. If for example you need a full
screen dialog, all you have to do is pass `Modifier.fillMaxSize()` to the `DialogContent`, without having to worry about
platform flags.

## Styled Examples

