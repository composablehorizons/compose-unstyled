# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.49.5] - 2025-01-03

### Added

- Add onTextLayout parameter to Text (#185)
- Begin of prefixing all components with `Unstyled-` prefix, so it's simpler to differenciate with other design systems components.

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

- Introduce Platform Themes. Platform Themes provide a native look and feel depending on the platform you are running on, such as platform fonts, text sizes, emojis on Web, touch indications, and interactive sizing for controls.
- Add option to animate `ColoredIndication`'s color changes
- Add getter to MutableThemeProperties

### Changed
- Updated demos to use Platform Theme instead of hardcoded styles

## [1.48.3] - 2025-11-11

### Fixed

- Ensure modal is added before monitoring its state (
  fixes https://github.com/composablehorizons/compose-unstyled/issues/151)
