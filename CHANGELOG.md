# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Modules! You can now pick and choose only the APIs that you want to use instead of being forced
  to add the full library to your codebase. `composeunstyled-primitives` continues to aggregate all
  primitives for users who prefer a single dependency.
- Added the following new modules: `composeunstyled-bottom-sheet`, `composeunstyled-build-modifier`,
  `composeunstyled-button`, `composeunstyled-checkbox`, `composeunstyled-colored-indication`,
  `composeunstyled-dialog`, `composeunstyled-disclosure`, `composeunstyled-dropdown-menu`,
  `composeunstyled-escape-handler`, `composeunstyled-focus-ring`, `composeunstyled-icon`,
  `composeunstyled-modal`, `composeunstyled-modal-bottom-sheet`, `composeunstyled-outline`,
  `composeunstyled-progress-indicator`, `composeunstyled-radio-group`, `composeunstyled-scrim`,
  `composeunstyled-scroll-area`, `composeunstyled-separators`, `composeunstyled-slider`,
  `composeunstyled-stack`, `composeunstyled-tab-group`, `composeunstyled-text-field`,
  `composeunstyled-toggle-switch`, `composeunstyled-tri-state-checkbox`, and
  `composeunstyled-window-container-size`.
- New Modal API: The Modal API has been revamped in order to make building custom models easier,
  without having to worry about different.
- Added `UnstyledScrim` as a standalone primitive.
- Added `Sheet` as the measured bottom sheet panel and `UnstyledBottomSheet` as the bottom sheet
  container.
- Compose Unstyled now uses Kotlin `2.3.20` and Compose Multiplatform `1.11.0-alpha01`.

### Removed

- The `composeunstyled` artifact is removed. Use `composeunstyled-primitives` for the aggregate
  dependency, or depend on the individual modules you need.
- All deprecated APIs are now removed. If you have breaking changes moving to this version,
  downgrade to the latest 1.x.x version and use `ReplaceWith()` to smoothly migrate to the latest
  APIs.
- Removed the `com.composables.core` package.
- Removed `ComposeUnstyledFlags`.

### Changed

- Decoupled theming from primitives. Primitives no longer read `LocalContentColor` or
  `LocalTextStyle`; users are responsible for styling primitives with their theming API of choice.
- Moved `Text`, `LocalContentColor`, `LocalTextStyle`, `ProvideContentColor`, and
  `ProvideTextStyle` to the theming module.
- The `BottomSheet` API has been reworked and split into two composables. `UnstyledBottomSheet`
  works as the area in which the bottom sheet can move on, while the `Sheet` does the actual
  rendering.
- Modal Bottom Sheet does not have its own independent `Sheet` composable anymore. Instead of reuses
  `BottomSheet`'s.
- Modal Bottom Sheet now has a separate `overlay` slot for passing any dimming overlay such as a
  scrim.

### Fixed
- Tooltips now hide on Escape only while visible and keep keyboard focus and hover visibility
  handling separate.
- Scrollbars now support dynamic sizing.
  (https://github.com/composablehorizons/compose-unstyled/issues/78)
- Scrollbars now stay visible while their thumb is dragged.
- Bottom Sheet content is now constrained to the visible sheet height. No need to hardcode a size if
  your content is scrollable. (https://github.com/composablehorizons/compose-unstyled/issues/134)
- Dialog, Modal Bottom Sheet, and Scrim now synchronize their exit lifecycle through shared modal
  state, preventing the modal window from being removed before animated fragments finish exiting.
- Dialog no longer freezes when it is initially visible without a scrim.

## [1.49.9] - 2025-04-24

### Fixed

- Fix dialog freeze when initially visible without a scrim. (Fixes #128)
- Fix stepped slider snapping behavior. (Fixes #81)
- Fix modals not inheriting `LocalLayoutDirection` from their parent. (Fixes #192)
- Prevent unnecessary theme recompositions when theme values do not change.

### Added

- Add new `ScrollArea` component in the `com.composeunstyled` package. Old packages will be removed
  in 2.0.

## [1.49.7] - 2025-04-21

### Added

- All component primitives are now prefixed with `Unstyled-`.
- Library minSDK is now 23.
- Bump compileSDK to 36.
- Add new `demo-system-ui-styling` module that showcases how to style System UI in modals.

### Fixed

- Fix state TextField visual transformation mapping (Fixes #207)
- Apply visualTransformation when TextField is non-editable (Fixes #206)
- Add textDecoration parameter to UnstyledTextField (Fixes #205)
- Add textDecoration parameter to UnstyledText (Fixes #205)
- Automatically focus dialog panel content on displayed (Fixes #204)
- Fix iOS DropdownMenu enter animations (Fixes #159)
- Fix Modals updating System UI appearance (Fixes #198)

## [1.49.6] - 2025-01-11

### Added

- ScrollAreaState now exposes a new `isScrollInProgress` property

### Fixed

- Scrollbars Thumb HideWhileIdle hides while scrolling #114

## [1.49.5] - 2025-01-03

### Added

- Add onTextLayout parameter to Text (#185)
- Begin of prefixing all components with `Unstyled-` prefix, so it's simpler to differenciate with
  other design systems components.

### Fixed

- Dialog's enter animations now work correctly on iOS.

## [1.49.4] - 2025-12-24

### Added

- Add `autoSize` parameter to Text (#183)

### Fixed

- Fix a bug where ScrollArea would trigger overscroll effect to the wrong axis
- Remove TextField placeholder on any character entered including space
- Change token names of PlatformTheme so that they do not override user's
- Fix inverted colors for RadioGroup
- Fix a Slider bug where Thumb would immediately jump while dragged

### Changed

- Deprecate ScrollArea with overscroll effect parameter

## [1.49.3] - 2025-11-27

### Added

Allow customization of Platform Themes (b2c2a26d)

### Fixed

- Tint TextField's contents according to LocalContentColor (1b3fb2bd)
- Fix a crash when updating BottomSheet's detents (7961afd1)
- Fix bug where Modifier.onFocusChanged wouldn't work with TextField (e3f0bd6c)

## [1.49.0] - 2025-11-14

### Added

- Introduce Platform Themes. Platform Themes provide a native look and feel depending on the
  platform you are running on, such as platform fonts, text sizes, emojis on Web, touch indications,
  and interactive sizing for controls.
- Add option to animate `ColoredIndication`'s color changes
- Add getter to MutableThemeProperties

### Changed

- Updated demos to use Platform Theme instead of hardcoded styles

## [1.48.3] - 2025-11-11

### Fixed

- Ensure modal is added before monitoring its state (
  fixes https://github.com/composablehorizons/compose-unstyled/issues/151)
