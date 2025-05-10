---
title: Disclosure
description: A foundational component for creating Disclosures in Compose Multiplatform
---

# Disclosure

An unstyled Disclosure component that can be used to build disclosures with the styling of your choice in your Compose
Multiplatform apps.

Comes with accessibility baked in, animation support and it is fully customizable.

<div style="position: relative; max-width: 800px; height: 340px; border-radius: 20px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../disclosure-demo/index.html" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Installation

```kotlin title="build.gradle.kts"
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.composables:core:1.30.0")
}
```

## Basic Example

The Disclosure component consists of a `Disclosure`, a `DisclosureHeading`, and a `DisclosurePanel`.

The `Disclosure` component holds its state and the rest of the components. It also sets up important semantics for
accessibility.

The `DisclosureHeading` is a button which will reveal or hide the `DisclosurePanel` when clicked, according to the
disclosure's state:

```kotlin
Disclosure {
    DisclosureHeading {
        Text("What is the return policy?")
    }
    DisclosurePanel {
        Text("Our return policy allows returns within 30 days of purchase with a receipt.")
    }
}
```

## Styling

The Disclosure renders nothing on the screen by default. It handles the UX pattern of a disclosure for you and leaves
any styling for you to handle.

We provide some styling parameters to ensure that your content's interaction indicators look great according to the
styling you need.

Changing the styling of the disclosure is done by passing the respective `Modifier`s to your `DisclosureHeading` and
`DisclosurePanel`:

```kotlin
val state = rememberDisclosureState()

Disclosure(state = state) {
    DisclosureHeading(
        modifier = Modifier
            .background(Color.LightGray)
            .padding(16.dp)
            .shadow(4.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp)
    ) {
        val degrees by animateFloatAsState(if (state.expanded) -180f else 0f, tween())

        Text("What is Compose Unstyled?")

        // icon from composeicons.com/icons/lucide/chevron-down
        Icon(
            imageVector = ChevronDown,
            contentDescription = null,
            modifier = Modifier.rotate(degrees)
        )
    }
    DisclosurePanel(
        enter = expandVertically(
            spring(
                stiffness = Spring.StiffnessMediumLow,
                visibilityThreshold = IntSize.VisibilityThreshold
            )
        ),
        exit = shrinkVertically()
    ) {
        Text(
            "Compose Unstyled is a collection of unstyled, accessible UI components for Compose Multiplatform.",
            modifier = Modifier.padding(16.dp).alpha(0.66f)
        )
    }
}
```

## Code Examples

### Showing/Hide the disclosure

To manually show or hide the disclosure panel, use the `expanded` property of the `Disclosure` state.

To create your own controlled state use the `rememberDisclosureState()` function and pass it to the `Disclosure`
composable.

Then use the `DisclosureHeading` variant with the `onClick` parameter to toggle the state manually.

**Important:** the `DisclosureHeading` with the `onClick` parameter does not set the collapse/expanded actions to the
heading as you might want to customize the behavior. These are important for accessibility and you would need to set
them yourself, if you chose to use this variant.

```kotlin
val state = rememberDisclosureState()

Disclosure(state = state) {
    DisclosureHeading(onClick = { state.expanded = state.expanded.not() }) {
        Text("What is the return policy?")
    }
    DisclosurePanel {
        Text("Our return policy allows returns within 30 days of purchase with a receipt.")
    }
}
```

### Adding transitions

You can add transitions to the disclosure panel to animate its expansion and collapse:

```kotlin
Disclosure {
    DisclosureHeading {
        Text("What is Compose Unstyled?")
    }
    DisclosurePanel(
        enter = expandVertically(
            spring(
                stiffness = Spring.StiffnessMediumLow,
                visibilityThreshold = IntSize.VisibilityThreshold
            )
        ),
        exit = shrinkVertically()
    ) {
        Text(
            "Compose Unstyled is a collection of unstyled, accessible UI components for Compose Multiplatform.",
            modifier = Modifier.padding(16.dp).alpha(0.66f)
        )
    }
}
```

## Keyboard Interactions

| Key                                   | Description                                                   |
|---------------------------------------|---------------------------------------------------------------|
| <div class="keyboard-key">Enter</div> | Toggles the disclosure between expanded and collapsed states. |
| <div class="keyboard-key">Space</div> | Toggles the disclosure between expanded and collapsed states. |

## Component API

### rememberDisclosureState

| Parameter           | Description                                         |
|---------------------|-----------------------------------------------------|
| `initiallyExpanded` | Determines if the disclosure is expanded initially. |

### DisclosureState

| Parameter  | Description                                  |
|------------|----------------------------------------------|
| `expanded` | Controls whether the disclosure is expanded. |

### Disclosure

| Parameter  | Description                                                             |
|------------|-------------------------------------------------------------------------|
| `state`    | The DisclosureState object controlling the disclosure's expanded state. |
| `modifier` | Modifier to be applied to the disclosure.                               |
| `content`  | A composable function that defines the content of the disclosure.       |

### DisclosureHeading

| Parameter           | Description                                                     |
|---------------------|-----------------------------------------------------------------|
| `modifier`          | Modifier to be applied to the heading.                          |
| `enabled`           | Indicates if the heading is enabled.                            |
| `shape`             | The shape of the heading.                                       |
| `backgroundColor`   | The background color of the heading.                            |
| `contentColor`      | The color to apply to the contents of the heading.              |
| `contentPadding`    | Padding values for the content.                                 |
| `borderColor`       | The color of the border.                                        |
| `borderWidth`       | The width of the border.                                        |
| `indication`        | The indication to be shown when the heading is interacted with. |
| `interactionSource` | The interaction source for the heading.                         |
| `content`           | A composable function that defines the content of the heading.  |

### DisclosurePanel

| Parameter  | Description                                                  |
|------------|--------------------------------------------------------------|
| `modifier` | Modifier to be applied to the panel.                         |
| `enter`    | The enter transition for the panel.                          |
| `exit`     | The exit transition for the panel.                           |
| `content`  | A composable function that defines the content of the panel. |

## Chevron Icons and other icons

You can find the chevron icon used in our examples and many other icons at [composeicons.com](https://composeicons.com).

## Styled Examples

<a href="https://composablesui.com?ref=core">

Looking for styled components for Jetpack Compose or Compose Multiplatform?

Explore a rich collection of production ready examples at <span style="color: #E91E63; font-weight: 500">
ComposablesUi.com</span>

<img src="../composablesui-banner.jpg" alt="Composables UI" style="width: 100%; max-width: 800px">
</a>

<style>
.keyboard-key {
  background-color: #EEEEEE;
  color: black;
  text-align: center;
  border-radius: 4px;
}
</style>
