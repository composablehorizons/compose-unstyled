# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## Added

- Introduce Platform Themes. Platform Themes provide a native look and feel depending on the platform you are running on, such as platform fonts, text sizes, emojis on Web, touch indications, and interactive sizing for controls.
- Add option to animate `ColoredIndication`'s color changes
- Add getter to MutableThemeProperties

## Changed
- Updated demos to use Platform Theme instead of hardcoded styles

## [1.48.3] - 2025-11-11

### Fixed

- Ensure modal is added before monitoring its state (
  fixes https://github.com/composablehorizons/compose-unstyled/issues/151)
