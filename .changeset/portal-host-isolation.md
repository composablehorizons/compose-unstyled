---
"composeunstyled-tooltip": patch
"composeunstyled-modal": patch
---

Fix tooltips and modals rendering in the wrong portal host. Each now renders in its matching `TooltipHost` or `ModalHost`, even when another portal host is nested inside. These hosts no longer display unrelated portal content.

Tooltips that used a generic `PortalHost` now need a `TooltipHost`.
