import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.composetheme.*
import com.composables.ui.Dialog
import com.composables.ui.DialogContent
import com.composables.ui.Scrim
import com.composables.ui.rememberDialogState

val AppTheme = buildComposeTheme { }

@Composable
fun DialogDemo() {
    AppTheme {
        val dialogState = rememberDialogState(false)
        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF4A90E2), Color(0xFF50C9C3)))).padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).clickable(role = Role.Button) { dialogState.visible = true }.background(Color.White).padding(horizontal = 14.dp, vertical = 10.dp)) {
                BasicText("Show dialog", style = TextStyle.Default.copy(fontWeight = FontWeight(500)))
            }
            Dialog(state = dialogState) {
                Scrim()
                DialogContent(
                    modifier = Modifier.systemBarsPadding().widthIn(min = 280.dp, max = 560.dp).padding(20.dp).clip(ComposeTheme.shapes.roundXL).border(1.dp, ComposeTheme.colors.gray100, ComposeTheme.shapes.roundXL).background(Color.White), enter = fadeIn(), exit = fadeOut()
                ) {
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
    }
}
