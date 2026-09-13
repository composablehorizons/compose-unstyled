---
"compose-unstyled": minor
---

Add a `key` parameter to `PortalHost` and a `target` parameter to `Portal`. Both accept a `PortalTarget` token. A portal uses the nearest host whose key matches its target. `PortalTarget.Unspecified` matches the nearest untargeted host. Portals with no matching host render nothing.
