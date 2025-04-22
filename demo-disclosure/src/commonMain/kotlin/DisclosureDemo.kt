package com.composeunstyled.demo

import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.composables.core.*
import com.composeunstyled.Disclosure
import com.composeunstyled.DisclosureHeading
import com.composeunstyled.DisclosurePanel
import com.composeunstyled.Text
import com.composeunstyled.rememberDisclosureState

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
            .padding(top = 60.dp),
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
                        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp)
                    ) {
                        Text(faq.question, modifier = Modifier.weight(1f))

                        val degrees by animateFloatAsState(if (state.expanded) -180f else 0f, tween())
                        Icon(
                            imageVector = ChevronDown,
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

val ChevronDown: ImageVector
    get() {
        if (_ChevronDown != null) {
            return _ChevronDown!!
        }
        _ChevronDown = ImageVector.Builder(
            name = "ChevronDown",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color.Black.copy(0.66f)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(6f, 9f)
                lineToRelative(6f, 6f)
                lineToRelative(6f, -6f)
            }
        }.build()
        return _ChevronDown!!
    }

private var _ChevronDown: ImageVector? = null
