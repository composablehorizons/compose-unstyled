---
"composeunstyled-portal": minor
---

Add `PortalTarget` to `PortalHost` and `Portal` to choose where portal content renders. A portal uses the nearest host with a matching target. `PortalTarget.Unspecified` matches the nearest untargeted host. Portals with no matching host render nothing.
