# Dialog 

An unstyled Dialog component for Composable Multiplatform that can be used to implement Dialogs with the styling of your choice. 

Fully accessible, supports animations, offers consistent behavior across platforms and an optional background scrim.

<div style="position: relative; max-width: 800px; height: 340px; border-radius: 20px; overflow: hidden;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../dialog-demo/index.html" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Installation

```kotlin title="build.gradle.kts"
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.composables:core:1.4.0")
}
```

## Basic Example

A dialog consists of the following components: `Dialog`, `DialogContent` and the optional `Scrim`.

The `Dialog` controls the visibility of the dialog via the `DialogState` object.

The `DialogContent` renders the 

The optional `Scrim` component is used to add layer behind the dialog

```kotlin
val dialogState = rememberDialogState(false)

Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF4A90E2), Color(0xFF50C9C3)))).padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).clickable(role = Role.Button) { dialogState.visible = true }.background(Color.White).padding(horizontal = 14.dp, vertical = 10.dp)) {
        BasicText("Show dialog", style = TextStyle.Default.copy(fontWeight = FontWeight(500)))
    }
    Dialog(state = dialogState) {
        Scrim()
        DialogContent(modifier = Modifier.systemBarsPadding().widthIn(min = 280.dp, max = 560.dp).padding(20.dp).clip(ComposeTheme.shapes.roundXL).border(1.dp, ComposeTheme.colors.gray100, ComposeTheme.shapes.roundXL).background(Color.White), enter = fadeIn(), exit = fadeOut()) {
            Column {
                Column(Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp)) {
                    BasicText("Update Available", style = ComposeTheme.textStyles.base.copy(fontWeight = FontWeight.Medium))
                    Spacer(Modifier.height(8.dp))
                    BasicText("A new version of the app is available. Please update to the latest version.", style = ComposeTheme.textStyles.base.copy(color = ComposeTheme.colors.gray900))
                }
                Spacer(Modifier.height(24.dp))
                Box(Modifier.padding(12.dp).align(Alignment.End).clip(ComposeTheme.shapes.round).clickable(role = Role.Button) { /* TODO */ }.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    BasicText("Update", style = ComposeTheme.textStyles.base.copy(color = ComposeTheme.colors.blue500))
                }
            }
        }
    }
}
```

## Styling

### Showing/Hide the dialog
### Adding a scrim
### Scrollable dialogs
### Full-screen dialogs
### Adding transitions

## Keyboard Interactions
Esc

## Component API
### Dialog
### DialogContent
### Scrim

## Core Dialog vs Compose Dialog

## Styled Examples

