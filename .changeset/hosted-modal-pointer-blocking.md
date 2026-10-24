---
"compose-unstyled": patch
---

Fix dialogs inside `DialogHost` allowing background hover, clicks, and scrolling when `dismissOnClickOutside` is disabled. Hosted modals now block background pointer input even without a scrim.
