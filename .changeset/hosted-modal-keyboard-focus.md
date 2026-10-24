---
"compose-unstyled": patch
---

Fix hosted modals allowing keyboard focus and keystrokes to reach background controls. Tab and Shift+Tab now stay inside the topmost modal, and closing it restores the previous focus. This also fixes dialogs, modal bottom sheets, and modal drawers rendered through a modal host.
