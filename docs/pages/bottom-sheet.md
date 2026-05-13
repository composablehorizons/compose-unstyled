---
title: Bottom Sheet
description: A renderless, highly performant foundational component to build Compose Multiplatform bottom sheets with, jam-packed with styling features without compromising on
  accessibility or keyboard interactions.
---

<UnstyledDemo id="bottom-sheet" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-bottom-sheet")
```

## Basic Example

A bottom sheet consists of the following components: `BottomSheet` and the optional `DragIndication`.

The `BottomSheet` component controls the area in which your sheet can be dragged and renders it within that area.

The `DragIndication` is an optional component that can be used within the `BottomSheet`'s contents. It provides a way
for the user to control the sheet without touch input.

A bottom sheet has a set of specified `Detents`. Each detent specify the height in which the sheet can rest while not
being dragged.

By default, 2 detents are specified: `Hidden` and `FullyExpanded`.

```kotlin
val sheetState = rememberBottomSheetState(
    initialDetent = Hidden,
)

UnstyledButton(onClick = { sheetState.targetDetent = FullyExpanded }) {
    BasicText("Show Sheet")
}

BottomSheet(
    state = sheetState,
    modifier = Modifier.fillMaxWidth(),
) {
    Box(
        modifier = Modifier.fillMaxWidth().height(1200.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        UnstyledDragIndication(Modifier.width(32.dp).height(4.dp))
    }
}
```

## Styling

The bottom sheet renders nothing on the screen by default. It manages a lot of states internally and leaves the styling
to you.

Any sort of styling is done by the `Modifier` of the respective component.

Changing the looks of the bottom sheet is done by passing the respective styling `Modifier`s to your `BottomSheet`
and `DragIndication`:

```kotlin
val sheetState = rememberBottomSheetState(
    initialDetent = Hidden,
)

BottomSheet(
    state = sheetState,
    modifier = Modifier
        .dropShadow(
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            shadow = Shadow(
                radius = 16.dp,
                spread = 0.dp,
                color = Color.Black.copy(alpha = 0.14f),
                offset = DpOffset(x = 0.dp, y = (-3).dp)
            )
        )
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
        UnstyledDragIndication(
            modifier = Modifier
                .padding(top = 22.dp)
                .background(Color.Black.copy(0.4f), RoundedCornerShape(100))
                .width(32.dp)
                .height(4.dp)
        )
    }
}

```

## Code Examples

### Showing/Hide the bottom sheet

The visibility of the sheet is controlled by its state's *targetDetent* property.

Pass `Hidden` to hide it, or `FullyExpanded` to fully expand it:

```kotlin
val sheetState = rememberBottomSheetState(
    initialDetent = Hidden,
)

UnstyledButton(onClick = { sheetState.targetDetent = FullyExpanded }) {
    BasicText("Show Sheet")
}

BottomSheet(
    state = sheetState,
    modifier = Modifier.fillMaxWidth(),
) {
    Box(
        modifier = Modifier.fillMaxWidth().height(1200.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        UnstyledButton(onClick = { sheetState.targetDetent = Hidden }) {
            BasicText("Hide Sheet")
        }
    }
}
```

Using the `targetDetent` property will cause the sheet to animate to given detent.

### Customizing sheet detents

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

val sheetState = rememberBottomSheetState(
    initialDetent = Peek,
    detents = listOf(Hidden, Peek, FullyExpanded)
)

BottomSheet(
    state = sheetState,
    modifier = Modifier.fillMaxWidth(),
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .height(1200.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        UnstyledDragIndication(
            modifier = Modifier
                .padding(top = 22.dp)
                .background(Color.Black.copy(0.4f), RoundedCornerShape(100))
                .width(32.dp)
                .height(4.dp)
        )
    }
}
```

### Drawing behind the nav bar

The BottomSheet component works as a normal component and does not do anything special when it comes to System UI. This
way you have full control
over how you want your layout to be rendered.

Nice looking bottom sheets tend to draw behind the platform's navigation bar while keeping their content above the
navigation bar.

The following code example showcases how to draw the bottom sheet behind the nav bar while ensuring its content is never
blocked by the nav bar's buttons.

At the same time, we make sure that the bottom sheet is never drawn behind the nav bars on landscape mode (for visual
purposes):

```kotlin
val sheetState = rememberBottomSheetState(
    initialDetent = FullyExpanded,
)

BottomSheet(
    state = sheetState,
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
        UnstyledDragIndication()
        BasicText("Here is some content")
    }
}
```

### Working with the soft-keyboard

Add the `Modifier.imePadding()` in the contents of your sheet to make sure its contents are always drawn above the soft
keyboard.

Here is a styled example of a 'Add note' sheet:

```kotlin
val sheetState = rememberBottomSheetState(
    initialDetent = FullyExpanded,
)
BottomSheet(
    state = sheetState,
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
        UnstyledDragIndication(Modifier.align(Alignment.CenterHorizontally))

        var text by remember { mutableStateOf("") }
        UnstyledTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth()
        )
        UnstyledButton(
            onClick = { /* TODO */ },
            shape = RoundedCornerShape(4.dp),
            contentPadding = PaddingValues(4.dp),
            modifier = Modifier.align(Alignment.End)
        ) {
            BasicText(
                text = "Save note",
                style = TextStyle.Default.copy(color = Color.White)
            )
        }
    }
}

```

### Scrollable sheets

Add any scrollable component within the contents of your sheet. `BottomSheet` supports nesting scrolling out of the box:

```kotlin
val sheetState = rememberBottomSheetState(
    initialDetent = FullyExpanded,
)

BottomSheet(
    state = sheetState,
    modifier = Modifier.fillMaxWidth()
        .background(Color.White),
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        UnstyledDragIndication(Modifier.width(32.dp).height(4.dp))
        LazyColumn {
            repeat(50) {
                item { BasicText("Item #${(it + 1)}", modifier = Modifier.padding(10.dp)) }
            }
        }
    }
}
```

<p class="border-l-4 pl-4 border-orange-500 flex flex-col gap-2 unstyled-warning">
<strong class="text-orange-800 unstyled-warning-title">⚠️ Scrollable content must have a fixed height</strong>
<span>The current implementation does not automatically handle dynamic content height of scrollable content. As a workaround, you must use a fixed height for your content.</span>
<span>If this affects you, <a href="https://github.com/composablehorizons/compose-unstyled/issues/134">react to the respected feature request.</a></span>
</p>

### Adding transitions

Pass your own `AnimationSpec` when creating your sheet's state:

```kotlin
val Peek = SheetDetent("peek") { containerHeight, sheetHeight ->
    containerHeight * 0.6f
}

val sheetState = rememberBottomSheetState(
    initialDetent = Peek,
    detents = listOf(Peek, FullyExpanded),
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
    )
)

BottomSheet(
    state = sheetState,
    modifier = Modifier.fillMaxWidth()
        .background(Color.White)
        .height(1200.dp),
) {
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

val sheetState = rememberBottomSheetState(
    initialDetent = Peek,
    detents = listOf(Peek, FullyExpanded),
)

BottomSheet(
    state = sheetState,
    modifier = Modifier.fillMaxWidth()
        .background(Color.White)
        .height(1200.dp),
) {
    Column {
        BasicText("Current Detent = ${sheetState.currentDetent.identifier}")
        BasicText("Target Detent = ${sheetState.targetDetent.identifier}")
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

val sheetState = rememberBottomSheetState(
    initialDetent = Peek,
    detents = listOf(Peek, FullyExpanded),
)

val alpha by animateFloatAsState(targetValue = sheetState.offset)

BottomSheet(
    state = sheetState,
    modifier = Modifier.fillMaxWidth()
        .alpha(alpha)
        .background(Color.White)
        .height(1200.dp),
) {
}
```

### Jumping to detent immediately

Use the `jumpTo()` function to move the sheet to the desired detent without any animation:

```kotlin
val sheetState = rememberBottomSheetState(
    initialDetent = Hidden,
)

UnstyledButton(onClick = { sheetState.jumpTo(FullyExpanded) }) {
    BasicText("Show Sheet")
}

BottomSheet(
    state = sheetState,
    modifier = Modifier.fillMaxWidth(),
) {
    Box(
        modifier = Modifier.fillMaxWidth().height(1200.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        UnstyledButton(onClick = { sheetState.jumpTo(Hidden) }) {
            BasicText("Hide Sheet")
        }
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

<ApiReference id="bottom-sheet" />

## Android Interop: WebView is not scrollable

Using an Android View `WebView` inside of a `BottomSheet` will block the `WebView`'s scrolling.

This is a bug with the Android <> Compose interop. To fix the issue, make sure to use a `Modifier.verticalScroll()` on your `AndroidView` that holds the `WebView`:

```kotlin
@Composable
fun WebView(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
        factory = {
            android.webkit.WebView(it)
        }
    )
}
```
