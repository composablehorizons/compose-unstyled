---
title: Tab Group
description: A tab group component with generic tab keys and keyboard navigation.
---

<UnstyledDemo id="tabgroup" />

## Features

- Generic tab keys
- Horizontal and vertical tab lists
- Automatic or manual activation
- Focus handoff to panels

## Installation

```kotlin
implementation("com.composables:composeunstyled-tab-group")
```

## Anatomy

```kotlin
val tabs = listOf("account", "billing")

UnstyledTabGroup(
  selectedTab = selectedTab,
  onSelectedTabChange = onSelectedTabChange,
  tabs = tabs,
) {
  TabList {
    tabs.forEach { tab ->
      Tab(tab) {
      }
    }
  }

  tabs.forEach { tab ->
    TabPanel(tab) {
    }
  }
}
```

## Concepts

- `UnstyledTabGroup` represents a set of tabs and panels grouped by key.
- `TabList` renders the group of tabs.
- `Tab` renders one tab inside `TabList`.
- `TabPanel` renders the panel for the selected tab key.

## Accessibility

Keep `tabs` in the same order as the visual tabs. `TabList` uses that order for arrow-key navigation, Home, and End.

## Code Examples

### Selecting tabs manually

Use the `selectedTab` parameter to control the active tab:

```kotlin
val tabs = listOf("account", "billing")
var selectedTab by remember { mutableStateOf("account") }

UnstyledTabGroup(
  selectedTab = selectedTab,
  onSelectedTabChange = { selectedTab = it },
  tabs = tabs,
) {
  TabList {
    tabs.forEach { tab ->
      Tab(tab) {
        BasicText(tab)
      }
    }
  }

  TabPanel("account") {
    BasicText("Account")
  }

  TabPanel("billing") {
    BasicText("Billing")
  }
}
```

### Creating vertical tabs

Use the `orientation` parameter on `TabList` to change arrow-key navigation for vertical tabs:

```kotlin
TabList(orientation = Orientation.Vertical) {
  tabs.forEach { tab ->
    Tab(tab) {
      BasicText(tab)
    }
  }
}
```

### Requiring click activation

Use the `activateOnFocus` parameter when arrow-key focus should not select tabs:

```kotlin
Tab("billing", activateOnFocus = false) {
  BasicText("Billing")
}
```

### Disabling a tab

Use the `enabled` parameter to keep a tab visible but unavailable:

```kotlin
Tab(
  key = "billing",
  enabled = false,
) {
  BasicText("Billing")
}
```

<ApiReference id="tabgroup" />
