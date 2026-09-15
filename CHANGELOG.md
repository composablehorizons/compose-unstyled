# compose-unstyled

## 2.10.0

### Minor Changes

- 448c4f3: Add the Drawer primitive.
- 9d5c16c: Add a `key` parameter to `PortalHost` and a `target` parameter to `Portal`. Both accept a `PortalTarget` token. A portal uses the nearest host whose key matches its target. `PortalTarget.Unspecified` matches the nearest untargeted host. Portals with no matching host render nothing.

### Patch Changes

- 25fa1b8: Expose the Android modal window through Compose's `DialogWindowProvider` so window-aware utilities can discover it from modal content.
- e5c9914: Expose `ModalState.hasMountedFragments` and `ModalState.isAttachedToWindow`.
- 3625da8: Preserve the caller's layout direction in non-Android modals.
- 6088abf: Avoid rewriting portal entries when callers recompose.
- 59d3926: Fix tooltips and modals rendering in the wrong portal host. Each now renders in its matching `TooltipHost` or `ModalHost`, even when another portal host is nested inside. These hosts no longer display unrelated portal content.

  Tooltips that used a generic `PortalHost` now need a `TooltipHost`.

## 2.9.2

### Patch Changes

- c6acc54: Fix the tab group demo screenshot baseline used by visual regression tests.

## 2.9.1

### Patch Changes

- cf1859f: Fix bottom sheet content being squished when collapsed.
- ffc1ba5: Upgrade Compose Multiplatform to 1.12.0 to prevent desktop accessibility crashes when opening modal content.
- d05a4cf: Fix modal content not rendering in Compose previews and preview screenshot tests.
- e442bed: Fix content-sized bottom sheets so short sheets follow upward drags immediately and stop at their measured content height.
