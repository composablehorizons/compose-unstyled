---
title: Tab Group
description: A foundational component used to create tab groups for navigation in Jetpack Compose and Compose Multiplatform. Comes with accessibility and keyboard navigation baked in.
---

<UnstyledDemo id="tabgroup" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-tab-group")
```

## Basic Example

To create a tab group, use the `TabGroup` component.

The `TabList` component hosts the tabs. For each tab include its contents within a `TabPanel` with the same key as the tab.

It is important to pass an ordered list of the keys in the `rememberTabGroupState()`. This order is used for cycling through tabs using the keyboard.

```kotlin
val categories = listOf("Trending", "Latest", "Popular")

val state = rememberTabGroupState(
    selectedTab = categories.first(),
    orderedTabs = categories
)

UnstyledTabGroup(state = state) {
    TabList {
        categories.forEach { key ->
            UnstyledTab(key = key) {
                BasicText("Tab $key")
            }
        }
    }

    categories.forEach { key ->
        UnstyledTabPanel(key = key) {
            BasicText("Content for $key")
        }
    }
}
```

## Styling

Every component in Compose Unstyled is renderless. They handle all UX pattern logic, internal state, accessibility (according to ARIA standards), and keyboard interactions for you, but they do not render any UI to the screen.

This is by design so that you can style your components exactly to your needs.

Most of the time, styling is done using `Modifiers` of your choice. However, sometimes this is not enough due to the order of the `Modifier`s affecting the visual outcome.

For such cases we provide specific styling parameters.

## Code Example

### Disabling a Tab

You can disable a tab by setting the `enabled` parameter to `false` in the `Tab` component.

```kotlin
val state = rememberTabGroupState(selectedTab = "Tab 2", orderedTabs = listOf("Tab 1", "Tab 2", "Tab 3"))

UnstyledTabGroup(state = state) {
    TabList {
        UnstyledTab(key = "Tab 1", enabled = false, modifier = Modifier.alpha(0.4f)) {
            BasicText("Tab 1")
        }
        UnstyledTab(key = "Tab 2") {
            BasicText("Tab 2")
        }
        UnstyledTab(key = "Tab 3") {
            BasicText("Tab 3")
        }
    }

    UnstyledTabPanel(key = "Tab 1") {
        BasicText("Contents 1")
    }
    UnstyledTabPanel(key = "Tab 2") {
        BasicText("Contents 2")
    }
    UnstyledTabPanel(key = "Tab 3") {
        BasicText("Contents 3")
    }
}
```

### Displaying Tabs Vertically

To display tabs vertically, wrap them into a `Column`. Make sure to pass `Vertical` to the orientation parameter of the `TabList`. This will update the keyboard handling so that users can cycle through tabs by pressing the up and down keys:

```kotlin
val state = rememberTabGroupState(selectedTab = "Tab 2", orderedTabs = listOf("Tab 1", "Tab 2", "Tab 3"))

UnstyledTabGroup(state = state) {
    UnstyledTabList(orientation = Orientation.Vertical) {
        Column {
            UnstyledTab(key = "Tab 1") {
                BasicText("Tab 1")
            }
            UnstyledTab(key = "Tab 2") {
                BasicText("Tab 2")
            }
            UnstyledTab(key = "Tab 3") {
                BasicText("Tab 3")
            }
        }
    }

    UnstyledTabPanel(key = "Tab 1") {
        BasicText("Contents 1")
    }
    UnstyledTabPanel(key = "Tab 2") {
        BasicText("Contents 2")
    }
    UnstyledTabPanel(key = "Tab 3") {
        BasicText("Contents 3")
    }
}
```

### Specifying the Default Tab

Specify the default tab by setting the `selectedTab` parameter in `rememberTabGroupState`.

```kotlin
val state = rememberTabGroupState(selectedTab = "Tab 2", orderedTabs = listOf("Tab 1", "Tab 2", "Tab 3"))

UnstyledTabGroup(state = state) {
    TabList {
        UnstyledTab(key = "Tab 1", enabled = false) {
            BasicText("Tab 1")
        }
        UnstyledTab(key = "Tab 2") {
            BasicText("Tab 2")
        }
        UnstyledTab(key = "Tab 3") {
            BasicText("Tab 3")
        }
    }

    UnstyledTabPanel(key = "Tab 1") {
        BasicText("Content for Tab 1")
    }
    UnstyledTabPanel(key = "Tab 2") {
        BasicText("Content for Tab 2")
    }
    UnstyledTabPanel(key = "Tab 3") {
        BasicText("Content for Tab 3")
    }
}
```

### Changing Tabs Manually

You can change tabs programmatically by setting the `selectedTab` property of the `TabGroupState`.

```kotlin
val state = rememberTabGroupState(selectedTab = "Tab 2", orderedTabs = listOf("Tab 1", "Tab 2", "Tab 3"))

UnstyledButton(onClick = { state.selectedTab = "Tab 3" }) {
    BasicText("Go to Tab 3")
}

UnstyledTabGroup(state = state) {
    TabList {
        UnstyledTab(key = "Tab 1", enabled = false) {
            BasicText("Tab 1")
        }
        UnstyledTab(key = "Tab 2") {
            BasicText("Tab 2")
        }
        UnstyledTab(key = "Tab 3") {
            BasicText("Tab 3")
        }
    }

    UnstyledTabPanel(key = "Tab 1") {
        BasicText("Content for Tab 1")
    }
    UnstyledTabPanel(key = "Tab 2") {
        BasicText("Content for Tab 2")
    }
    UnstyledTabPanel(key = "Tab 3") {
        BasicText("Content for Tab 3")
    }
}
```

## Keyboard Interactions

For the tab list:

| Key                                   | Description                                                                                             |
|---------------------------------------|---------------------------------------------------------------------------------------------------------|
| <div class="keyboard-key">Tab</div>  | When focus moves into the tab list, places focus on the active tab element. When the tab list contains the focus, moves focus to the next element in the page tab sequence outside the tablist, which is the tabpanel unless the first element containing meaningful content inside the tabpanel is focusable. |
| <div class="keyboard-key">⬅</div>    | Moves focus to the previous tab. If focus is on the first tab, moves focus to the last tab. Optionally, activates the newly focused tab. |
| <div class="keyboard-key">➡</div>    | Moves focus to the next tab. If focus is on the last tab element, moves focus to the first tab. Optionally, activates the newly focused tab. |
| <div class="keyboard-key">Space</div> / <div class="keyboard-key">Enter</div> | Activates the tab if it was not activated automatically on focus. |
| <div class="keyboard-key">Home</div> | Moves focus to the first tab. Optionally, activates the newly focused tab. |
| <div class="keyboard-key">End</div>  | Moves focus to the last tab. Optionally, activates the newly focused tab. |

<style>
.keyboard-key {
  background-color: #EEEEEE;
  color: black;
  text-align: center;
  border-radius: 4px;
  padding: 2px 6px;
  margin: 0 2px;
  display: inline-block;
}
</style>

<ApiReference id="tabgroup" />
