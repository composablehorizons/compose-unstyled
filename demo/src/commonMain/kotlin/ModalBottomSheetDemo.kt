package com.composeunstyled.demo

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.core.DragIndication
import com.composables.core.ModalBottomSheet
import com.composables.core.Scrim
import com.composables.core.Sheet
import com.composables.core.SheetAnchor
import com.composables.core.SheetDetent
import com.composables.core.SheetDetent.Companion.FullyExpanded
import com.composables.core.SheetDetent.Companion.Hidden
import com.composables.core.rememberModalBottomSheetState
import com.composeunstyled.Button
import com.composeunstyled.Text
import com.composeunstyled.currentWindowContainerSize
import com.composeunstyled.focusRing
import com.composeunstyled.platformtheme.dimmed
import com.composeunstyled.platformtheme.indications
import com.composeunstyled.theme.Theme
import kotlinx.coroutines.delay

private val Peek = SheetDetent("peek") { containerHeight, sheetHeight ->
    containerHeight * 0.6f
}

@Composable
fun TopModalBottomSheetDemo() {
    ModalBottomSheetDemo(SheetAnchor.Top)
}

@Composable
fun ModalBottomSheetDemo(anchor: SheetAnchor = SheetAnchor.Bottom) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFF800080), Color(0xFFDA70D6))))
    ) {
        val modalSheetState = rememberModalBottomSheetState(
            initialDetent = Hidden,
            detents = listOf(Hidden, Peek, FullyExpanded),
            anchor = anchor
        )

        LaunchedEffect(Unit) {
            delay(500)
            modalSheetState.targetDetent = Peek
        }

        Button(
            onClick = { modalSheetState.targetDetent = Peek },
            modifier = Modifier
                .align(Alignment.Center)
                .padding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal).asPaddingValues()),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
            backgroundColor = Color.White,
            indication = Theme[indications][dimmed]
        ) {
            Text("Show Sheet")
        }

        val isCompact = currentWindowContainerSize().width < 600.dp

        ModalBottomSheet(state = modalSheetState) {
            Scrim(scrimColor = Color.Black.copy(0.3f), enter = fadeIn(), exit = fadeOut())

            val sheetShape = when (anchor) {
                SheetAnchor.Bottom -> RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                SheetAnchor.Top -> RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
            }
            val sheetAlignment = when (anchor) {
                SheetAnchor.Bottom -> Alignment.BottomCenter
                SheetAnchor.Top -> Alignment.TopCenter
            }
            val contentAlignment = when (anchor) {
                SheetAnchor.Bottom -> Alignment.TopCenter
                SheetAnchor.Top -> Alignment.BottomCenter
            }
            val paddingModifier = when (anchor) {
                SheetAnchor.Top -> Modifier
                    .displayCutoutPadding()
                    .statusBarsPadding()
                SheetAnchor.Bottom -> Modifier
            }

            Box(
                Modifier.fillMaxSize()
                    .let { if (isCompact) it else it.padding(horizontal = 56.dp) }
                    .then(paddingModifier)
                    .padding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal).asPaddingValues()),
                contentAlignment = sheetAlignment
            ) {
                Sheet(
                    modifier = Modifier
                        .shadow(4.dp, sheetShape)
                        .widthIn(max = 640.dp)
                        .fillMaxWidth(),
                    shape = sheetShape,
                    backgroundColor = Color.White,
                    contentColor = Color.Black
                ) {
                    Box(Modifier.fillMaxWidth().height(600.dp), contentAlignment = contentAlignment) {
                        val interactionSource = remember { MutableInteractionSource() }

                        DragIndication(
                            interactionSource = interactionSource,
                            modifier = Modifier
                                .padding(
                                    top = if (anchor == SheetAnchor.Bottom) 22.dp else 0.dp,
                                    bottom = if (anchor == SheetAnchor.Top) 22.dp else 0.dp
                                )
                                .focusRing(
                                    interactionSource,
                                    width = 2.dp,
                                    Color(0XFF2563EB),
                                    RoundedCornerShape(100),
                                    offset = 4.dp
                                )
                                .background(Color.Black.copy(0.4f), RoundedCornerShape(100))
                                .size(32.dp, 4.dp)
                        )
                    }
                }
            }
        }
    }
}
