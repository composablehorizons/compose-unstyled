package com.composeunstyled.demo

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.composables.core.Separator
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.Lucide
import com.composeunstyled.Disclosure
import com.composeunstyled.DisclosureHeading
import com.composeunstyled.DisclosurePanel
import com.composeunstyled.Icon
import com.composeunstyled.Text
import com.composeunstyled.platformtheme.dimmed
import com.composeunstyled.platformtheme.indications
import com.composeunstyled.rememberDisclosureState
import com.composeunstyled.theme.Theme

@Composable
fun DisclosureDemo() {
    class FAQ(val question: String, val answer: String)

    val faqs = listOf(
        FAQ(
            question = "What is Compose Unstyled",
            answer = "Compose Unstyled is a collection of unstyled, accessible UI components for Compose Multiplatform. " +
                    "It provides the building blocks for creating beautiful, consistent user interfaces."
        ),
        FAQ(
            question = "When should I use Compose Unstyled?",
            answer = "Use Compose Unstyled as a foundation to build components for your own design system."
        )
    )
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFF00C6FF), Color(0xFF0072FF))))
            .padding(top = 60.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .width(500.dp)
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
        ) {
            faqs.forEachIndexed { i, faq ->
                if (i != 0) {
                    Separator(color = Color.Black.copy(0.2f))
                }

                val state = rememberDisclosureState(initiallyExpanded = i == 0)
                Disclosure(state = state) {
                    DisclosureHeading(
                        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
                        indication = Theme[indications][dimmed]
                    ) {
                        Text(faq.question, modifier = Modifier.weight(1f))

                        val degrees by animateFloatAsState(if (state.expanded) -180f else 0f, tween())
                        Icon(
                            imageVector = Lucide.ChevronDown,
                            contentDescription = null,
                            modifier = Modifier.rotate(degrees)
                        )
                    }
                    DisclosurePanel(
                        enter = expandVertically(
                            spring(
                                stiffness = Spring.StiffnessMediumLow,
                                visibilityThreshold = IntSize.VisibilityThreshold
                            )
                        ),
                        exit = shrinkVertically()
                    ) {
                        Text(faq.answer, modifier = Modifier.padding(16.dp).alpha(0.66f))
                    }
                }
            }
        }
    }
}
