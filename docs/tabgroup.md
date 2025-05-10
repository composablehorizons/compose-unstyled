---
title: Tab Group
description: A component for creating tab groups for navigation.
---

# Tab Group

A foundational component used to create tab groups for navigation in Compose Multiplatform.

Comes with accessibility and keyboard navigation baked in.

<div style="position: relative; max-width: 800px; height: 340px; border-radius: 20px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../tabgroup-demo/index.html" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Installation

```kotlin title="build.gradle.kts"
dependencies {
    implementation("com.composables:core:1.30.0")
}
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

TabGroup(state = state) {
    TabList {
        categories.forEach { key ->
            Tab(key = key) {
                Text("Tab $key")
            }
        }
    }

    categories.forEach { key ->
        TabPanel(key = key) {
            Text("Content for $key")
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

TabGroup(state = state) {
    TabList {
        Tab(key = "Tab 1", enabled = false, modifier = Modifier.alpha(0.4f)) {
            Text("Tab 1")
        }
        Tab(key = "Tab 2") {
            Text("Tab 2")
        }
        Tab(key = "Tab 3") {
            Text("Tab 3")
        }
    }

    TabPanel(key = "Tab 1") {
        Text("Contents 1")
    }
    TabPanel(key = "Tab 2") {
        Text("Contents 2")
    }
    TabPanel(key = "Tab 3") {
        Text("Contents 3")
    }
}
```

### Displaying Tabs Vertically

To display tabs vertically, wrap them into a `Column`. Make sure to pass `Vertical` to the orientation parameter of the `TabList`. This will update the keyboard handling so that users can cycle through tabs by pressing the up and down keys:

```kotlin
val state = rememberTabGroupState(selectedTab = "Tab 2", orderedTabs = listOf("Tab 1", "Tab 2", "Tab 3"))

TabGroup(state = state) {
    TabList(orientation = Orientation.Vertical) {
        Column {
            Tab(key = "Tab 1") {
                Text("Tab 1")
            }
            Tab(key = "Tab 2") {
                Text("Tab 2")
            }
            Tab(key = "Tab 3") {
                Text("Tab 3")
            }
        }
    }

    TabPanel(key = "Tab 1") {
        Text("Contents 1")
    }
    TabPanel(key = "Tab 2") {
        Text("Contents 2")
    }
    TabPanel(key = "Tab 3") {
        Text("Contents 3")
    }
}
```

### Specifying the Default Tab

Specify the default tab by setting the `selectedTab` parameter in `rememberTabGroupState`.

```kotlin
val state = rememberTabGroupState(selectedTab = "Tab 2", orderedTabs = listOf("Tab 1", "Tab 2", "Tab 3"))

TabGroup(state = state) {
    TabList {
        Tab(key = "Tab 1", enabled = false) {
            Text("Tab 1")
        }
        Tab(key = "Tab 2") {
            Text("Tab 2")
        }
        Tab(key = "Tab 3") {
            Text("Tab 3")
        }
    }

    TabPanel(key = "Tab 1") {
        Text("Content for Tab 1")
    }
    TabPanel(key = "Tab 2") {
        Text("Content for Tab 2")
    }
    TabPanel(key = "Tab 3") {
        Text("Content for Tab 3")
    }
}
```

### Changing Tabs Manually

You can change tabs programmatically by setting the `selectedTab` property of the `TabGroupState`.

```kotlin
val state = rememberTabGroupState(selectedTab = "Tab 2", orderedTabs = listOf("Tab 1", "Tab 2", "Tab 3"))

Button(onClick = { state.selectedTab = "Tab 3" }) {
    Text("Go to Tab 3")
}

TabGroup(state = state) {
    TabList {
        Tab(key = "Tab 1", enabled = false) {
            Text("Tab 1")
        }
        Tab(key = "Tab 2") {
            Text("Tab 2")
        }
        Tab(key = "Tab 3") {
            Text("Tab 3")
        }
    }

    TabPanel(key = "Tab 1") {
        Text("Content for Tab 1")
    }
    TabPanel(key = "Tab 2") {
        Text("Content for Tab 2")
    }
    TabPanel(key = "Tab 3") {
        Text("Content for Tab 3")
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

## Component API

### TabGroupState

| Parameter        | Description                                       |
|------------------|---------------------------------------------------|
| `selectedTab`    | The currently selected tab in the tab group.      |
| `focusedTab`     | The currently focused tab in the tab group.       |

### rememberTabGroupState

| Parameter      | Description                                            |
|----------------|--------------------------------------------------------|
| `selectedTab`  | The initial selected tab for the tab group state.      |
| `orderedTabs`  | The list of tabs in the order they should appear.      |

### TabGroup

| Parameter            | Description                                                 |
|----------------------|-------------------------------------------------------------|
| `state`              | The state of the tab group, managing the current tab.       |
| `modifier`           | Modifier to be applied to the tab group.                    |
| `content`            | Composable function to define the tabs and panels.          |

### TabList

| Parameter            | Description                                                 |
|----------------------|-------------------------------------------------------------|
| `modifier`           | Modifier to be applied to the tab list.                     |
| `shape`              | The shape of the tab list.                                  |
| `backgroundColor`    | The background color of the tab list.                       |
| `orientation`        | The orientation of the tab list (horizontal or vertical).   |
| `activateOnFocus`    | Whether to activate a tab when it receives focus.           |
| `content`            | Composable function to define the tabs.                     |

### Tab

| Parameter            | Description                                                 |
|----------------------|-------------------------------------------------------------|
| `key`                | The unique key for the tab.                                 |
| `modifier`           | Modifier to be applied to the tab.                          |
| `enabled`            | Whether the tab is enabled.                                 |
| `indication`         | Visual indication for interactions.                         |
| `interactionSource`  | Interaction source for the tab.                             |
| `content`            | Composable function to define the content of the tab.       |

### TabPanel

| Parameter            | Description                                                 |
|----------------------|-------------------------------------------------------------|
| `key`                | The unique key for the tab panel.                           |
| `content`            | Composable function to define the content of the tab panel. |
