---
title: Disclosure
description: An expandable content component with a dedicated trigger and content slot.
---

<UnstyledDemo id="disclosure" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-disclosure")
```

## Anatomy

```kotlin
UnstyledDisclosure(
  expanded = expanded,
  onExpandedChange = onExpandedChange,
) {
  DisclosureButton {
  }

  DisclosedContent {
  }
}
```

## Concepts

- `UnstyledDisclosure` represents an expandable region.
- `DisclosureButton` renders the trigger that toggles the disclosure.
- `DisclosedContent` renders content only while the disclosure is expanded.

## Accessibility

Use `DisclosureButton` for the disclosure trigger so assistive technology receives expand and collapse actions.

## Code Examples

### Showing hidden content

Use the `expanded` parameter to control whether `DisclosedContent` is visible:

```kotlin
var expanded by remember { mutableStateOf(false) }

UnstyledDisclosure(
  expanded = expanded,
  onExpandedChange = { expanded = it },
) {
  DisclosureButton {
    BasicText(if (expanded) "Hide details" else "Show details")
  }

  DisclosedContent {
    BasicText("Details")
  }
}
```

### Animating disclosed content

Use the `enter` and `exit` parameters on `DisclosedContent` to animate the disclosed content:

```kotlin
UnstyledDisclosure(
  expanded = expanded,
  onExpandedChange = { expanded = it },
) {
  DisclosureButton {
    BasicText("Details")
  }

  DisclosedContent(
    enter = expandVertically(),
    exit = shrinkVertically(),
  ) {
    BasicText("Hidden content")
  }
}
```

<ApiReference id="disclosure" />
