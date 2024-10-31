---
title: Modal Bottom Sheet
description: A stackable, renderless, highly performant foundational component to build modal bottom sheets with, jam-packed with styling features without compromising on accessibility or keyboard interactions.
---

# Bottom Sheet (Modal)

A stackable, renderless, highly performant foundational component to build modal bottom sheets with, jam-packed with
styling features
without compromising on accessibility or keyboard interactions.

<div style="position: relative; max-width: 800px; height: 340px; border-radius: 20px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../modalsheet-demo/index.html" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
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

A modal bottom sheet consists of the following components: `ModalBottomSheet`, `Sheet` and the optional `Scrim`
and `DragIndication`.

The `ModalBottomSheet` controls the visibility of the modal bottom sheet. It renders its contents above any other
layouts without having to worry about nesting.

The `Sheet` is a container component that renders the bottom sheet and its contents.

The `Scrim` component is used to add layer behind the dialog and dim the rest of the UI.

The `DragIndication` can be used within the `Sheet`'s contents. It provides a way
for the user to control the sheet without touch input.

A bottom sheet has a set of specified `Detents`. Each detent specify the height in which the sheet can rest while not
being dragged.

By default, 2 detents are specified: `Hidden` and `FullyExpanded`.

```kotlin
val sheetState = rememberModalBottomSheetState(
    initialDetent = Hidden,
)

Box(Modifier.clickable { sheetState.currentDetent = FullyExpanded }) {
    BasicText("Show Sheet")
}

ModalBottomSheet(state = sheetState) {
    Sheet(modifier = Modifier.fillMaxWidth().background(Color.White)) {
        Box(
            modifier = Modifier.fillMaxWidth().height(1200.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            DragIndication()
        }
    }
}
```

## Styling

The modal bottom sheet renders nothing on the screen by default. It manages a lot of states internally and leaves the
styling
to you.

Any sort of styling is done by the `Modifier` of the respective component.

Changing the looks of the bottom sheet is done by passing the respective styling `Modifier`s to your `Sheet`, `Scrim`
and `DragIndication`:

```kotlin
val sheetState = rememberModalBottomSheetState(
    initialDetent = FullyExpanded,
)

ModalBottomSheet(state = sheetState) {
    Sheet(
        modifier = Modifier
            .padding(top = 12.dp)
            .shadow(4.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(Color.White)
            .widthIn(max = 640.dp)
            .fillMaxWidth()
            .imePadding(),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(1200.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            DragIndication(
                modifier = Modifier
                    .padding(top = 22.dp)
                    .background(Color.Black.copy(0.4f), RoundedCornerShape(100))
                    .width(32.dp)
                    .height(4.dp)
            )
        }
    }
}
```

## Code Examples

### Showing/Hide the bottom sheet

The visibility of the sheet is controlled by its state's *currentDetent* property.

Pass `Hidden` to hide it, or `FullyExpanded` to fully expand it:

```kotlin
val sheetState = rememberModalBottomSheetState(
    initialDetent = Hidden,
)

Box(Modifier.clickable { sheetState.currentDetent = FullyExpanded }) {
    BasicText("Show Sheet")
}

ModalBottomSheet(state = sheetState) {
    Sheet(
        state = sheetState,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(1200.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(Modifier.clickable { sheetState.currentDetent = Hidden }) {
                BasicText("Hide Sheet")
            }
        }
    }
}
```

Using the `currentDetent` property will cause the sheet to animate to given detent.

### Customizing sheet heights

The heights in which the bottom sheet needs to stop for dragging purposes is controlled by the sheet's
state `SheetDetent`s.

To create a new detent, use the `SheetDetent` constructor to pass a unique identifier and a function which calculates
the height of the detent at a given moment. The calculated value will be capped between `0.dp` and the content's height.

**NOTE:** Make sure that the calculation returns _fast_, as this will affect your sheet's performance.

Make sure to pass your new detent when creating your bottom sheet state:

```kotlin
val Peek = SheetDetent(identifier = "peek") { containerHeight, sheetHeight ->
    containerHeight * 0.6f
}

val sheetState = rememberModalBottomSheetState(
    initialDetent = Peek,
    detents = listOf(Hidden, Peek, FullyExpanded)
)

ModalBottomSheet(state = sheetState) {
    Sheet(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .height(1200.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            DragIndication(
                modifier = Modifier
                    .padding(top = 22.dp)
                    .background(Color.Black.copy(0.4f), RoundedCornerShape(100))
                    .width(32.dp)
                    .height(4.dp)
            )
        }
    }
}
```

### Adding a scrim

Use the `Scrim` component within your `ModalBottomSheet`:

```kotlin
val sheetState = rememberModalBottomSheetState(
    initialDetent = Peek,
    detents = listOf(Hidden, Peek, FullyExpanded)
)

ModalBottomSheet(state = sheetState) {
    Scrim()
    Sheet(
        modifier = Modifier.fillMaxWidth()
            .background(Color.White)
            .height(1200.dp)
    ) {
    }
}
```

### Drawing behind the nav bar

The ModalBottomSheet component is a modal component. When displayed on the screen, it dims the navigation bars and
protect its icons.

Other than that, you have full control over how you want sheet and its contents to be rendered on the screen.

Nice looking bottom sheets tend to draw behind the platform's navigation bar while keeping their content above the
navigation bar.

The following code example showcases how to draw the bottom sheet behind the nav bar while ensuring its content is never
blocked by the nav bar's buttons.

At the same time, we make sure that the bottom sheet is never drawn behind the nav bars on landscape mode (for visual
purposes):

```kotlin
val sheetState = rememberModalBottomSheetState(
    initialDetent = FullyExpanded,
)

ModalBottomSheet(state = sheetState) {
    Sheet(
        modifier = Modifier.fillMaxWidth()
            .padding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal)
                    .asPaddingValues()
            )
            .navigationBarsPadding(),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DragIndication()
            BasicText("Here is some content")
        }
    }
}
```

### Working with the soft-keyboard

Add the `Modifier.imePadding()` in the contents of your sheet, to make sure that its contents are always draw above the
soft keyboard.

Here is a styled example of a 'Add note' sheet:

```kotlin
val sheetState = rememberModalBottomSheetState(
    initialDetent = FullyExpanded,
)
ModalBottomSheet(state = sheetState) {
    Sheet(
        modifier = Modifier.fillMaxWidth()
            .padding(
                // make sure the sheet is not behind nav bars on landscape
                WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal)
                    .asPaddingValues()
            )
            .background(Color.White),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp)
                // make sure the contents of the sheet is always above the nav bar
                .navigationBarsPadding()
                // draw the contents above the soft keyboard
                .imePadding()
        ) {
            DragIndication(Modifier.align(Alignment.CenterHorizontally))

            var text by remember { mutableStateOf("") }
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth()
            )
            Box(Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Blue)
                .clickable { /* TODO */ }
                .padding(4.dp)
                .align(Alignment.End)) {
                BasicText(
                    text = "Save note",
                    style = TextStyle.Default.copy(color = Color.White)
                )
            }
        }
    }
}
```

### Scrollable sheets

Add any scrollable component within the contents of your sheet. `BottomSheet` supports nesting scrolling out of the box:

```kotlin
val sheetState = rememberModalBottomSheetState(
    initialDetent = FullyExpanded,
)
ModalBottomSheet(state = sheetState) {
    Sheet(
        modifier = Modifier.fillMaxWidth()
            .background(Color.White),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DragIndication(Modifier.width(32.dp).height(4.dp))
            LazyColumn {
                repeat(50) {
                    item { BasicText("Item #${(it + 1)}", modifier = Modifier.padding(10.dp)) }
                }
            }
        }
    }
}
```

### Adding transitions

Pass your own `AnimationSpec` when creating your sheet's state:

```kotlin
val Peek = SheetDetent("peek") { containerHeight, sheetHeight ->
    containerHeight * 0.6f
}

val sheetState = rememberModalBottomSheetState(
    initialDetent = Peek,
    detents = listOf(Peek, FullyExpanded),
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
    )
)
ModalBottomSheet(state = sheetState) {
    Sheet(
        modifier = Modifier.fillMaxWidth()
            .background(Color.White)
            .height(1200.dp),
    ) {
    }
}
```

### Listening to state changes

Use the sheet's state `currentDetent` to observe for state changes. This value returns the current detent the sheet is
currently rested at.

The `targetDetent` value returns the next detent the sheet is approaching (ie while being dragged).

```kotlin
val Peek = SheetDetent("peek") { containerHeight, sheetHeight ->
    containerHeight * 0.6f
}

val sheetState = rememberModalBottomSheetState(
    initialDetent = Peek,
    detents = listOf(Peek, FullyExpanded),
)
ModalBottomSheet(state = sheetState) {
    Sheet(
        modifier = Modifier.fillMaxWidth()
            .background(Color.White)
            .height(1200.dp),
    ) {
        Column {
            BasicText("Current Detent = ${sheetState.currentDetent.identifier}")
            BasicText("Target Detent = ${sheetState.targetDetent.identifier}")
        }
    }
}
```

### Listening to dragging progress

Use the state's `offset` value to listen to how far the sheet has moved within its container.

If you are interested in listening to dragging events between detents, use the state's `progress` value instead:

```kotlin
val Peek = SheetDetent("peek") { containerHeight, sheetHeight ->
    containerHeight * 0.6f
}

val sheetState = rememberModalBottomSheetState(
    initialDetent = Peek,
    detents = listOf(Peek, FullyExpanded),
)

val alpha by animateFloatAsState(targetValue = sheetState.offset)

ModalBottomSheet(state = sheetState) {
    Sheet(
        modifier = Modifier.fillMaxWidth()
            .alpha(alpha)
            .background(Color.White)
            .height(1200.dp),
    ) {
    }
}
```

### Jumping to detent immediately

Use the `jumpTo()` function to move the sheet to the desired detent without any animation.

**NOTE:** It is not currently possible to make the sheet enter the screen without an animation.

```kotlin
val sheetState = rememberModalBottomSheetState(
    initialDetent = FullyExpanded,
)

ModalBottomSheet(state = sheetState) {
    Sheet(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.fillMaxWidth().height(1200.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(Modifier.clickable { sheetState.jumpTo(Hidden) }) {
                BasicText("Hide Sheet")
            }
        }
    }
}
```

### Styling the system bars

**Android only**

ModalBottomSheet will not change the color of your system UI when displayed. We provide a `LocalModalWindow` composition local,
which provides you with the Android `Window` that hosts the dialog, so that you can customize the System UI according to
your needs:

```kotlin
ModalBottomSheet(rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded)) {
    Sheet(Modifier.fillMaxWidth()) {
        val window = LocalModalWindow.current
        LaunchedEffect(Unit) {
            // change system bars to transparent
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Black.copy(0.3f).toArgb()

            // don't forget to update the icons too
            val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
            windowInsetsController.isAppearanceLightStatusBars = true
            windowInsetsController.isAppearanceLightNavigationBars = false
        }
        BasicText("Transparent status bar, darkened navbars. Easy-peazy 😎 ", modifier = Modifier.navigationBarsPadding())
    }
}
```

## Keyboard Interactions

The `BottomSheet` component does not have any keyboard interactions, as it is 100% controlled by touch input.

We strongly recommended to always include the `DragIndication` component within your sheet's content which enables the
following keyboard interactions:

<style>
.keyboard-key {
  background-color: #EEEEEE;
  color: black;
  text-align: center;
  border-radius: 4px;
}

.md-typeset__table {
   min-width: 100%;
}

.md-typeset table:not([class]) {
    display: table;
}

.parameter {
    white-space: nowrap
}
</style>

| Key                                           | Action                                          |
|-----------------------------------------------|-------------------------------------------------|
| <div class="keyboard-key">Tab</div>           | Moves focus to or away from the drag indication |
| <div class="keyboard-key">Space / Enter</div> | Toggles between the available sheet's detents   |

## Parameters

### rememberModalBottomSheetState()

| Parameter                                          | Description                                                                                                                                                                                                                                                                                                                                           |
|----------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| <div class="parameter">`initialDetent`</div>       | A `SheetDetent` which controls the height in which the sheet will be introduced within its container.                                                                                                                                                                                                                                                 |
| <div class="parameter">`detents`</div>             | A list of `SheetDetent` which the sheet can be rested for dragging purposes.                                                                                                                                                                                                                                                                          |
| <div class="parameter">`animationSpec`</div>       | An `AnimationSpec` used when animating the sheet across the different *sheetDetents*.                                                                                                                                                                                                                                                                 |
| <div class="parameter">`positionalThreshold`</div> | The positional threshold, in px, to be used when calculating the target state while a drag is in progress and when settling after the drag ends. This is the distance from the start of a transition. It will be, depending on the direction of the interaction, added or subtracted from/to the origin offset. It should always be a positive value. |
| <div class="parameter">`velocityThreshold`</div>   | The velocity threshold (in px per second) that the end velocity has to exceed in order to animate to the next state, even if the [positionalThreshold] has not been reached.                                                                                                                                                                          |

### ModalBottomSheetState

| Parameter                                               | Description                                                                                                                       |
|---------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| <div class='parameter'>`currentDetent` </div>           | The `SheetDetent` in which the sheet is currently rested on. Setting a new detent will cause the sheet to animate to that detent. |
| <div class='parameter'>`targetDetent` </div>            | The `SheetDetent` in which the sheet is about to rest on, if it is being dragged or animated.                                     |
| <div class='parameter'>`isIdle` </div>                  | Whether the sheet is currently resting at a specific detent.                                                                      |
| <div class='parameter'>`progress` </div>                | A 0 to 1 `Float` which represents how far between two detents the sheet has currently moved. 1.0f for arrived at the end.         |
| <div class='parameter'>`offset` </div>                  | A 0 to 1 `Float` which represents how far the sheet has moved within its dragging container. 1.0f for top of the container.       |
| <div class='parameter'>`fun jumpTo()` </div>            | Makes the sheet to immediately appear to the given detent without any animation.                                                  |
| <div class='parameter'>`suspend fun animateTo()` </div> | Animates the sheet to the given detent. This is a `suspend` function, which you can use to wait until the animation is complete.  |

### ModalBottomSheet()

The main component. Defines the area in which the sheet can be dragged in.

| Parameter                                 | Description                                                                                          |
|-------------------------------------------|------------------------------------------------------------------------------------------------------|
| <div class='parameter'>`state`</div>      | The `ModalBottomSheetState` for the component                                                        |
| <div class='parameter'>`properties`</div> | `ModalSheetProperties` that control whether the sheet needs to be dismissed on clicked outside, etc. |
| <div class='parameter'>`onDismiss`</div>  | Called when the sheet is being dismissed either by tapping outside or by pressing `Esc` or `Back`.   |
| <div class='parameter'>`content`</div>    | The contents of the Modal Bottom Sheet.                                                              |

### Sheet()

Renders the sheet and its contents.

| Parameter                               | Description                      |
|-----------------------------------------|----------------------------------|
| <div class='parameter'>`modifier`</div> | The `Modifier` for the component |
| <div class='parameter'>`enabled`</div>  | Enables or disables dragging.    |
| <div class='parameter'>`content`</div>  | The contents of the sheet.       |

### DragIndication()

A component that indicates that the sheet can be dragged.

| Parameter                               | Description                      |
|-----------------------------------------|----------------------------------|
| <div class='parameter'>`modifier`</div> | The `Modifier` for the component |

## Styled Examples

<a href="https://composablesui.com?ref=core">

Looking for styled components for Jetpack Compose or Compose Multiplatform?

Explore a rich collection of production ready examples at <span style="color: #E91E63; font-weight: 500">
ComposablesUi.com</span>

<img src="../composablesui-banner.jpg" alt="Composables UI" style="width: 100%; max-width: 800px">
</a>