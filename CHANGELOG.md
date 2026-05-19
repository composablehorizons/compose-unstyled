# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [2.3.0] - 2026-05-19

### Added

- Added a `selectionColors` parameter to `UnstyledTextField` for styling text selection handles and
  background. (#374)

### Changed

- `buildTheme {}` now creates a theme without styling by default. It removes Compose Foundation's
  default styling, such as indication and text selection colors.
- Deprecated `minimumInteractiveComponentSize`, `ComponentInteractiveSize`, and
  `defaultComponentInteractiveSize`. This is now your responsibility to implement if your design
  system requires such functionality.

### Fixed

- Fixed short bottom sheets animating into percentage detents too slowly.
- Fixed content-height bottom sheets changing height while transitioning to or from the hidden
  detent.
- Fixed offset outlines preserving the wrong rounded corner geometry when drawn outside a component.

## [2.2.0] - 2026-05-18

### Added

- Added focus-visible support to `focusRing`, including `FocusVisibilityProvider`,
  `collectIsFocusVisibleAsState`, and `FocusRingVisibility.Focused` for opting into the previous
  focus-only behavior. (#349)
- Added a dedicated `visual-regressions` module for desktop screenshot regression coverage.

### Changed

- Theme's `minimumInteractiveComponentSize()` now applies touch-sized minimum bounds on devices that
  support touch input instead of forcing it depending on the platform target.
- Improved Bottom Sheet layout performance by reducing repeated measurement calculations.
- `UnstyledDialog` and `UnstyledModalBottomSheet` overlay lambdas now use dedicated overlay scopes,
  with `Scrim` available directly from those scopes.

### Removed

- Removed the separate `composeunstyled-scrim` artifact. Use `Scrim` from `composeunstyled-modal`,
  `composeunstyled-dialog`, or `composeunstyled-modal-bottom-sheet` instead.

### Fixed

- Fixed fully expanded bottom sheets with short lazy content anchoring incorrectly instead of staying
  aligned to the bottom of the container. (#356)
- Fixed fully expanded bottom sheets clipping content when the sheet modifier adds height, such as
  top padding. (#356)
- Fixed a visual glitch where bottom sheets could briefly jump past their expanded position when
  flung by touch.

## [2.1.0] - 2026-05-16

### Added

- Added recomposition test coverage for Bottom Sheet, Modal Bottom Sheet, and Toggle Switch.
- Added screenshot test coverage for Bottom Sheet and Modal Bottom Sheet demos.
- Tooltip now has a dedicated `TooltipHost` for rendering tooltip panels instead of leaking
  `Portal`.

### Changed

- Dropdown menus now render in our own `Modal()` instead of a platform popup.
- Dropdown Menu and Tooltip now expose shared anchor placement APIs from
  `composeunstyled-anchored-api` without exposing the internal anchored layout implementation
  transitively.
- `UnstyledIcon` now defaults `contentDescription` to `null` for decorative icons.

### Fixed

- Fixed `UnstyledSwitch` always animating to enabled even if its original state was enabled.
- Fixed bottom sheet content-dependent detent measurement when sheet modifiers add padding or fixed
  height content is taller than the visible sheet. (#316)
- Fixed dropdown menus being placed incorrectly on the first frame when initially expanded. (#308)
- Fixed modal bottom sheets animating before their modal window is attached. (#287)

## [2.0.0] - 2026-05-11

### Added

- Modules! You can now pick and choose only the APIs that you want to use instead of being forced
  to add the full library to your codebase. `composeunstyled-primitives` continues to aggregate all
  primitives for users who prefer a single dependency.
- Added the following new modules: `composeunstyled-anchored`, `composeunstyled-bottom-sheet`,
  `composeunstyled-build-modifier`, `composeunstyled-button`, `composeunstyled-checkbox`,
  `composeunstyled-colored-indication`, `composeunstyled-dialog`, `composeunstyled-disclosure`,
  `composeunstyled-dropdown-menu`, `composeunstyled-escape-handler`,
  `composeunstyled-focus-ring`, `composeunstyled-icon`, `composeunstyled-modal`,
  `composeunstyled-modal-bottom-sheet`, `composeunstyled-outline`, `composeunstyled-portal`,
  `composeunstyled-progress`, `composeunstyled-radio-group`, `composeunstyled-scrim`,
  `composeunstyled-scrollbars`, `composeunstyled-separators`, `composeunstyled-slider`,
  `composeunstyled-stack`, `composeunstyled-tab-group`, `composeunstyled-text-field`,
  `composeunstyled-toggle-switch`, `composeunstyled-tooltip`, `composeunstyled-tri-state-checkbox`,
  and `composeunstyled-window-container-size`.
- New Modal API: The Modal API has been revamped in order to make building custom modals easier,
  without having to manage platform-specific modal lifecycles directly.
- Added `PortalHost` and `Portal` for rendering content into a same-window portal without changing
  parent layout.
- Added anchored positioning APIs for placing floating content relative to an anchor.
- Added `Scrim` as a scoped modal primitive.
- Added `Sheet` as the measured bottom sheet panel and `UnstyledBottomSheet` as the bottom sheet
  container.
- Added public `BottomSheetState` and `ModalBottomSheetState` constructors so design systems can wrap
  primitive state in their own state objects.
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
- Bottom sheet animations can now be customized per state change with animation specs, and modal
  bottom sheets can use a separate dismiss animation spec.
- Modal Bottom Sheet does not have its own independent `Sheet` composable anymore. It reuses
  `BottomSheet`'s `Sheet` API.
- Modal Bottom Sheet now has a separate `overlay` slot for passing any dimming overlay such as a
  scrim.
- Modal Bottom Sheet is now IME-aware by default through `ModalBottomSheetProperties.offsetForIme`.
- `UnstyledDialog` visibility is now controlled by a `visible` parameter, and `DialogPanel` supports
  an optional `paneTitle` for accessibility.
- `UnstyledDisclosure` is now controlled by `expanded` and `onExpandedChange`.
- `UnstyledDropdownMenu` now uses scoped `DropdownMenuPanel` and `MenuItem` APIs, supports anchored
  side/alignment offsets, and supports Home/End keyboard navigation inside menus.
- `UnstyledTooltip` now uses a scoped `TooltipPanel` API with `TooltipPlacement` instead of
  arrow-specific parameters.
- `UnstyledTabGroup` now uses generic tab keys and scoped `TabList`, `Tab`, and `TabPanel` APIs.
- `UnstyledTextField` now uses `TextFieldState`, exposes a scoped `TextInput` slot, and passes
  through the newer `BasicTextField` transformation, line limit, keyboard, layout, and scroll
  parameters.
- `UnstyledSlider` now exposes `track` and `thumb` slots with `SliderState`, supports horizontal and
  vertical orientations, reverse direction, stepped values, keyboard control, and custom thumb sizing.
- Checkbox, tri-state checkbox, and radio group indicators are now scoped child APIs
  (`CheckedIndicator`, `StateIndicator`, and `SelectedIndicator`) that receive the primitive
  interaction source.
- Radio groups now support generic value types and scope `RadioButton` to `RadioGroupScope`.
- Toggle switch behavior and thumb placement are now split between `UnstyledSwitch` and the scoped
  `SwitchThumb` API.
- Scroll area state APIs moved into the scrollbars module and were renamed to `ScrollbarState`.
  The `ScrollArea` container was removed so scrollbars do not impose layout or styling choices.
- Scrollbars are now standalone vertical and horizontal primitives with caller-provided thumbs and
  `ThumbVisibility` control.
- Component primitives no longer choose internal layout, alignment, or sizing for their content.
  Design systems are now responsible for arranging primitive slots and indicators.

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
