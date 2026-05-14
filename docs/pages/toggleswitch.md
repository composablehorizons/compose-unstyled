---
title: Toggle Switch
description: A switch component with an animated thumb slot.
---

<UnstyledDemo id="toggleswitch" />

## Installation

```kotlin
implementation("com.composables:composeunstyled-toggle-switch")
```

## Anatomy

```kotlin
UnstyledSwitch(
  checked = checked,
  onCheckedChange = onCheckedChange,
) {
  SwitchThumb {
  }
}
```

## Concepts

- `UnstyledSwitch` represents the interactive switch.
- `SwitchThumb` places thumb content at the start or end of the switch layout.

## Accessibility

Use the `onCheckedChange` parameter to make the switch interactive. Set it to `null` only when an
accessible parent component owns the toggle interaction.

## Code Examples

### Toggling a switch

Use the `checked` and `onCheckedChange` parameters to control switch state:

```kotlin
var checked by remember { mutableStateOf(false) }

UnstyledSwitch(
  checked = checked,
  onCheckedChange = { checked = it },
) {
  SwitchThumb {
    BasicText(if (checked) "On" else "Off")
  }
}
```

### Animating the switch thumb

Use the `animationSpec` parameter on `SwitchThumb` to change the thumb animation:

```kotlin
UnstyledSwitch(
  checked = checked,
  onCheckedChange = { checked = it },
) {
  SwitchThumb(animationSpec = tween(durationMillis = 200)) {
    BasicText(if (checked) "On" else "Off")
  }
}
```

### Moving toggle behavior to a parent

Use the `onCheckedChange` parameter with `null` when a parent toggleable surface owns the
interaction. This is useful when the switch is only the visual control inside a larger row.

```kotlin
Row(
  modifier = Modifier.toggleable(
    value = checked,
    role = Role.Switch,
    onValueChange = { checked = it },
  ),
) {
  BasicText("Notifications")

  UnstyledSwitch(
    checked = checked,
    onCheckedChange = null,
  ) {
    SwitchThumb {
      BasicText(if (checked) "On" else "Off")
    }
  }
}
```

<ApiReference id="toggleswitch" />
