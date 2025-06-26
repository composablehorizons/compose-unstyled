---
title: Introduction
description: Compose Unstyled is a set of foundational components for building high-quality, accessible design systems in Compose Multiplatform.
---

Compose Unstyled is a set of foundational components for building high-quality, accessible design systems in Compose
Multiplatform.

Instead of building every common UX pattern from scratch, we built them for you. This includes bottom sheets,
scrollbars, and sliders. You can style these according to your own styling needs.

## The problem with Compose

Compose Multiplatform is an excellent UI toolkit for building high-quality, modern, complex apps. However, the default
design system is Google's Material Design.

The 'Material look' feels out of place outside of Android. If you are building for desktop or web, using Material will
make your app feel awkward. Material is focused on touch devices with big touch targets.

Even when building for Android, there is a big chance that you will need components that look slightly (or completely)
different from Material's. Material Compose, Google's implementation of Material Design, has little to no room for
customizations.

You could rebuild every component from scratch if Material does not cover your needs. But who has time for that when a
project is running? Building great, accessible components that feel great for both touch and keyboard can be a time
sink.

So instead of spending days on this work, we are providing you with the building blocks you need. This allows you to put
together a high-quality, accessible design system. All you have to do is bring the styling.

## Key Features

### Fully unstyled

Components come with zero styling. They render nothing on the screen by design and make zero design choices for you.

If there is something you cannot style, that is considered a bug (kindly file an issue).

### Fully themable

Create themes from your own design system's tokens, that you can extend and customize at any point of development. 

### Fully accessible

Components are fully accessible and support keyboard navigation out of the box. Semantics implementation is based on
the [ARIA spec](https://www.w3.org/WAI/ARIA/apg/patterns/).

**Note:** Some Compose Multiplatform targets are more mature than others. All components' accessibility semantics have
been tested using Android's TalkBack.

### Developer Experience

Components have a simple API. They behave exactly the same on every platform and do not come with platform-specific
limitations, such as Android's dialogs fixed sizing.

For every component, we provide detailed documentation along with detailed code samples for common use cases.

There is no lock-in. If you need to modify a component, copy-paste the code into your project and do any modifications
you need. Each component is self-contained in its own single Kotlin file.

Components are also truly multiplatform. There is no mention of specific platforms on the public API. Platform-specific
features, such as styling system bars on Android, are only available to specific targets, which is also documented.

## Frequently Asked Questions

**Is this a component library?**

No. Components in Unstyled are meant to be used as building blocks to build your own components
with your own styling, without having to worry about the complex stuff such as UX details, accessibility and keyboard
navigation. In other words **it's how you build your own component library**.

**Is this based off Material Compose?**

No, all components are written from scratch. A few components do reuse source code from Material compose for behavior
purposes and they do not bring any styling.

**Can I use this together with Material Compose or other design systems?**

Yes. Many people use Unstyled because they prefer the simpler API and customization options of
the [ModalBottomSheet](modal-bottom-sheet.md) component than the Material one.

## Get involved

Have an idea for a component to be added or a component is not just right? [**Open an issue**](https://github.com/composablehorizons/compose-unstyled/issues)

Want to say thanks? Tell your friends and [**star the repo**](https://github.com/composablehorizons/compose-unstyled)

Come hang in the [**community Discord**](https://discord.gg/AZ4X7vEr5p)

Want to reach out to me? [**Ping me on X**](https://x.com/alexstyl)