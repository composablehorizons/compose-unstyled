---
title: Accordion
description: A component for creating expandable accordion sections.
---

# Accordion

An unstyled Accordion component that can be used to build accordions with the styling of your choice in your Compose Multiplatform apps.

Comes with accessibility baked in, animation support and it is fully customizable.

<div style="position: relative; max-width: 800px; height: 340px; border-radius: 20px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../accordion-demo/index.html" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
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

The Accordion component consists of an `Accordion`, an `AccordionHeading`, and an `AccordionPanel`.

The `Accordion` component holds its state and the rest of the components. It also sets up important semantics for accessibility.

The `AccordionHeading` is a button which will reveal or hide the `AccordionPanel` when clicked, according to the accordion's state:

```kotlin
Accordion {
    AccordionHeading {
        Text("What is the return policy?")
    }
    AccordionPanel {
        Text("Our return policy allows returns within 30 days of purchase with a receipt.")
    }
}
```

## Styling

The Accordion renders nothing on the screen by default. It handles the UX pattern of an accordion for you and leaves any styling for you to handle.

We provide some styling parameters to ensure that your content's interaction indicators look great according to the styling you need.

Changing the styling of the accordion is done by passing the respective `Modifier`s to your `AccordionHeading` and `AccordionPanel`:

```kotlin
Accordion {
    AccordionHeading(
        modifier = Modifier
            .background(Color.LightGray)
            .padding(16.dp)
            .shadow(4.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
        chevronDown = { Icon(ChevronDown, contentDescription = null) }
    ) {
        Text("What is Compose Unstyled?")
    }
    AccordionPanel(
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

### Showing/Hide the accordion

The visibility of the accordion is controlled by its state. You can manage the state using `rememberAccordionState` to toggle the visibility programmatically:

```kotlin
val accordionState = rememberAccordionState(initiallyExpanded = false)

Accordion(state = accordionState) {
    AccordionHeading {
        Text("What is the return policy?")
    }
    AccordionPanel {
        Text("Our return policy allows returns within 30 days of purchase with a receipt.")
    }
}

// To toggle the state externally
accordionState.expanded = !accordionState.expanded
```

### Adding transitions

You can add transitions to the accordion panel to animate its expansion and collapse:

```kotlin
Accordion {
    AccordionHeading(
        chevronDown = { Icon(ChevronDown, contentDescription = null) }
    ) {
        Text("What is Compose Unstyled?")
    }
    AccordionPanel(
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

| Key                                         | Description                                                                                  |
|---------------------------------------------|----------------------------------------------------------------------------------------------|
| <div class="keyboard-key">Enter</div>      | Toggles the accordion between expanded and collapsed states.                                 |
| <div class="keyboard-key">Space</div>      | Toggles the accordion between expanded and collapsed states.                                 |

## Component API

### rememberAccordionState

| Parameter | Description |
|-----------|-------------|
| `initiallyExpanded` | Determines if the accordion is expanded initially. |

### AccordionState

| Parameter | Description |
|-----------|-------------|
| `expanded` | Controls whether the accordion is expanded. |

### Accordion

| Parameter | Description |
|-----------|-------------|
| `state` | The AccordionState object controlling the accordion's expanded state. |
| `modifier` | Modifier to be applied to the accordion. |
| `content` | A composable function that defines the content of the accordion. |

### AccordionHeading

| Parameter | Description |
|-----------|-------------|
| `modifier` | Modifier to be applied to the heading. |
| `enabled` | Indicates if the heading is enabled. |
| `shape` | The shape of the heading. |
| `backgroundColor` | The background color of the heading. |
| `contentColor` | The color to apply to the contents of the heading. |
| `contentPadding` | Padding values for the content. |
| `borderColor` | The color of the border. |
| `borderWidth` | The width of the border. |
| `indication` | The indication to be shown when the heading is interacted with. |
| `interactionSource` | The interaction source for the heading. |
| `content` | A composable function that defines the content of the heading. |

### AccordionPanel

| Parameter | Description |
|-----------|-------------|
| `modifier` | Modifier to be applied to the panel. |
| `enter` | The enter transition for the panel. |
| `exit` | The exit transition for the panel. |
| `content` | A composable function that defines the content of the panel. |

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
