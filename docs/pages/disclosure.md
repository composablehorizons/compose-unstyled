---
title: Disclosure
description: An unstyled Disclosure component that can be used to build disclosures with the styling of your choice in your Compose Multiplatform apps. Comes with accessibility baked in, animation support and it is fully customizable.
---

<UnstyledDemo id="disclosure" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-disclosure")
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
        BasicText("What is the return policy?")
    }
    DisclosurePanel {
        BasicText("Our return policy allows returns within 30 days of purchase with a receipt.")
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

UnstyledDisclosure(state = state) {
    UnstyledDisclosureHeading(
        modifier = Modifier
            .background(Color.LightGray)
            .padding(16.dp)
            .dropShadow(
                shape = RoundedCornerShape(8.dp),
                shadow = Shadow(
                    radius = 12.dp,
                    spread = 0.dp,
                    color = Color.Black.copy(alpha = 0.12f),
                    offset = DpOffset(x = 0.dp, y = 4.dp)
                )
            ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp)
    ) {
        val degrees by animateFloatAsState(if (state.expanded) -180f else 0f, tween())

        BasicText("What is Compose Unstyled?")

        // icon from composeicons.com/icons/lucide/chevron-down
        UnstyledIcon(
            imageVector = ChevronDown,
            contentDescription = null,
            modifier = Modifier.rotate(degrees)
        )
    }
    UnstyledDisclosurePanel(
        enter = expandVertically(
            spring(
                stiffness = Spring.StiffnessMediumLow,
                visibilityThreshold = IntSize.VisibilityThreshold
            )
        ),
        exit = shrinkVertically()
    ) {
        BasicText(
            "Compose Unstyled is a collection of unstyled, accessible UI components for Jetpack Compose and Compose Multiplatform.",
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

UnstyledDisclosure(state = state) {
    UnstyledDisclosureHeading(onClick = { state.expanded = state.expanded.not() }) {
        BasicText("What is the return policy?")
    }
    DisclosurePanel {
        BasicText("Our return policy allows returns within 30 days of purchase with a receipt.")
    }
}
```

### Adding transitions

You can add transitions to the disclosure panel to animate its expansion and collapse:

```kotlin
Disclosure {
    DisclosureHeading {
        BasicText("What is Compose Unstyled?")
    }
    UnstyledDisclosurePanel(
        enter = expandVertically(
            spring(
                stiffness = Spring.StiffnessMediumLow,
                visibilityThreshold = IntSize.VisibilityThreshold
            )
        ),
        exit = shrinkVertically()
    ) {
        BasicText(
            "Compose Unstyled is a collection of unstyled, accessible UI components for Jetpack Compose and Compose Multiplatform.",
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

<ApiReference id="disclosure" />
