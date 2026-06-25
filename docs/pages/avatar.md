---
title: Avatar
description: An avatar primitive with image, fallback content, and caller-defined shape.
---

<UnstyledDemo id="avatar" />

## Features

- Use any kind of painter you want
- Fallback content under the image

## Installation

```kotlin
implementation("com.composables:composeunstyled-avatar")
```

## Anatomy

```kotlin
val painter = rememberUriPainter(
  uri = "https://images.unsplash.com/photo-1533738363-b7f9aef128ce?q=80&w=1080",
  crossfade = 50.milliseconds,
)
UnstyledAvatar(
  painter = painter,
  contentDescription = "@coolcat",
  underlay = {
    BasicText("CC")
  },
)
```

## Concepts

- `UnstyledAvatar` represents the entire render avatar.
- The `underlay` slot is placed behind the image, so it can provide initials or placeholder content when the `painter` fails to load the image or is `null`.

## Accessibility

Pass a `contentDescription` when the avatar identifies a person, account, or brand. Use `null` when the avatar is decorative.

## Code Examples

### Showing initials until an image is available

Use the `underlay` parameter to provide fallback content behind the image. This is useful when the image may be missing or still loading.

```kotlin
UnstyledAvatar(
  painter = null,
  contentDescription = "@coolcat",
  underlay = {
    BasicText("CC")
  },
  modifier = Modifier
    .size(40.dp)
    .clip(CircleShape),
)
```

### Cropping profile photos to the avatar bounds

Use the `contentScale` parameter to crop the image to the avatar container.

```kotlin
val painter = rememberUriPainter(
  uri = "https://images.unsplash.com/photo-1533738363-b7f9aef128ce?q=80&w=1080",
  crossfade = 50.milliseconds,
)
UnstyledAvatar(
  painter = painter,
  contentDescription = "@coolcat",
  contentScale = ContentScale.Crop,
  modifier = Modifier
    .size(40.dp)
    .clip(CircleShape),
)
```

<ApiReference id="avatar" />
