# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## Added

- ColoredIndication can now animate it's state changes. See `showAnimationSpec` and `hideAnimationSpec` parameters
  during construction.

## [1.48.3] - 2025-11-11

### Fixed

- Ensure modal is added before monitoring its state (
  fixes https://github.com/composablehorizons/compose-unstyled/issues/151)
