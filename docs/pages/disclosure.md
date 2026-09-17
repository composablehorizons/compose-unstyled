---
title: Disclosure
description: An expandable content component with a dedicated trigger and content slot.
---

<UnstyledDemo id="disclosure" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-disclosure:2.9.2")
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

```kotlin expandable
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

```kotlin expandable
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

## API Reference

<ApiReference declaration="com.composeunstyled.UnstyledDisclosure" />
<ApiReference declaration="com.composeunstyled.UnstyledDisclosureButton" />
<ApiReference declaration="com.composeunstyled.UnstyledDisclosedContent" />
<ApiReference declaration="com.composeunstyled.DisclosureScope.DisclosureButton" />
<ApiReference declaration="com.composeunstyled.DisclosureScope.DisclosedContent" />
