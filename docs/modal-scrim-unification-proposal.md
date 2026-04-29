# Modal + Scrim API Unification Proposal

## Goal
Unify scrim behavior across `Dialog` and `ModalBottomSheet` by making scrim a modal primitive backed by shared modal state, while moving away from scope-based APIs toward CompositionLocal-driven state.

## Direction (No Backward-Compatibility Layer)
- This is a direct cut.
- Do not add compatibility aliases.
- Keep the name `Sheet` (do not introduce `ModalBottomSheetSheet`).
- Keep the name `UnstyledScrim` for the shared scrim primitive.

## Proposed Primitives (`composeunstyled-modal`)

```kotlin
@Stable
class ModalState(initiallyVisible: Boolean = false) {
  internal val visibilityState = MutableTransitionState(initiallyVisible)

  var visible by mutableStateOf(initiallyVisible)
    set(value) {
      field = value
      visibilityState.targetState = value
    }
}

@Composable
fun rememberModalState(initiallyVisible: Boolean = false): ModalState

internal val LocalModalState = staticCompositionLocalOf<ModalState> {
  error("CompositionLocal LocalModalState not present")
}

@Composable
expect fun Modal(
  state: ModalState,
  onDismissRequest: () -> Unit = {},
  dismissOnBackPress: Boolean = true,
  dismissOnClickOutside: Boolean = false,
  onKeyEvent: (KeyEvent) -> Boolean = { false },
  content: @Composable () -> Unit,
)

@Composable
fun UnstyledScrim(
  modifier: Modifier = Modifier,
  scrimColor: Color = Color.Black.copy(alpha = 0.6f),
  enter: EnterTransition = fadeIn(tween(0)),
  exit: ExitTransition = fadeOut(tween(0)),
)
```

### Behavior Contract
- `UnstyledScrim()` visibility is driven by `LocalModalState.current.visibilityState`.
- Scrim is composed while modal is visible or animating.
- Scrim is removed from composition when hidden and idle.

## Dialog Changes (`composeunstyled-dialog`)

### Current
- Dialog owns separate panel and scrim transition state.
- Dialog-local `UnstyledScrim()` reads `LocalDialogState`.

### Target
- Dialog maps `DialogState.visible` to shared `ModalState`.
- `UnstyledDialog` hosts shared `Modal(state = modalState, ...)`.
- Scrim uses shared `UnstyledScrim()` from modal module.
- Dialog-specific scrim state ownership is removed.

### Sketch
```kotlin
@Composable
fun UnstyledDialog(...) {
  val modalState = rememberModalState(initiallyVisible = state.visible)

  LaunchedEffect(state.visible) {
    modalState.visible = state.visible
  }

  Modal(
    state = modalState,
    onDismissRequest = {
      onDismiss()
      state.visible = false
    },
    dismissOnBackPress = properties.dismissOnBackPress,
    dismissOnClickOutside = properties.dismissOnClickOutside,
  ) {
    content() // can include UnstyledScrim() and UnstyledDialogPanel(...)
  }
}
```

## Modal Bottom Sheet Changes (`composeunstyled-modal-bottom-sheet`)

### Current
- `ModalBottomSheetState` owns `scrimState` and detent orchestration.
- `ModalBottomSheetScope.Scrim()` is scope-based and reads `scrimState`.
- `Sheet` is scope-based (`ModalBottomSheetScope.Sheet`).

### Target
- Remove `ModalBottomSheetScope`.
- Use CompositionLocals to expose required state to child composables.
- `Sheet` remains named `Sheet`, but becomes top-level/local-state driven (not scope extension).
- `Scrim` entry point is removed from modal-bottom-sheet; use shared `UnstyledScrim()`.
- `ModalBottomSheet` maps detent visibility/animation lifecycle to shared `ModalState`.

### Usage Target
```kotlin
ModalBottomSheet(state = state) {
  UnstyledScrim()
  Sheet {
    ...
  }
}
```

## CompositionLocal State Shape (Bottom Sheet)

```kotlin
internal data class ModalBottomSheetContentState(
  val modalBottomSheetState: ModalBottomSheetState,
  val bottomSheetState: BottomSheetState,
)

internal val LocalModalBottomSheetContentState =
  staticCompositionLocalOf<ModalBottomSheetContentState> {
    error("LocalModalBottomSheetContentState not present")
  }
```

`ModalBottomSheet` provides this local; `Sheet` reads from it.

## Testing Plan

### modal module
- `UnstyledScrim` appears when `ModalState.visible = true`.
- `UnstyledScrim` is removed from composition when `visible = false` and idle.
- dismiss paths (outside click/back press) correctly trigger `onDismissRequest`.

### dialog module
- shared `UnstyledScrim` follows dialog visibility through mapped `ModalState`.
- panel and scrim lifecycles remain consistent during show/hide.

### modal-bottom-sheet module
- shared `UnstyledScrim` follows hidden/non-hidden detent transitions.
- scrim removed from composition when hidden and idle.
- `Sheet` still behaves correctly after scope removal.

## Why This Is Better
- Single scrim API (`UnstyledScrim`) across modal consumers.
- Single modal visibility model.
- No duplicated scrim state machines.
- Aligns with CompositionLocal-based state propagation over scope-based API patterns.
