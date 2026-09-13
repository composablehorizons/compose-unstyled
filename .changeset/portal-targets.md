---
"composeunstyled-portal": minor
"composeunstyled-tooltip": minor
"composeunstyled-modal": minor
---

Add PortalTarget to route portals to the nearest host with a matching target. Nested hosts for other targets no longer intercept their content. Unmatched portals render nothing.

Bind TooltipHost and ModalHost to separate internal targets. A generic PortalHost no longer hosts tooltips; use TooltipHost. Generic PortalHost and Portal pairs continue to use the default target.
