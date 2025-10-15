package com.composeunstyled.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Contact
import com.composables.icons.lucide.Heart
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Settings
import com.composeunstyled.Icon


@Composable
fun IconDemo() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color(0xFFF0F0F0)))),
        contentAlignment = Alignment.Center
    ) {
        val isCompact = maxWidth < 600.dp
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Lucide.Heart,
                contentDescription = null,
                tint = Color(0xFF9E9E9E),
                modifier = Modifier.requiredSize(84.dp)
            )
            if (isCompact.not()) {
                Icon(
                    Lucide.Search,
                    contentDescription = null,
                    tint = Color(0xFF757575),
                    modifier = Modifier.requiredSize(84.dp)
                )
            }
            Icon(
                Lucide.Settings,
                contentDescription = null,
                tint = Color(0xFF616161),
                modifier = Modifier.requiredSize(84.dp)
            )
            if (isCompact.not()) {
                Icon(
                    Lucide.Contact,
                    contentDescription = null,
                    tint = Color(0xFF424242),
                    modifier = Modifier.requiredSize(84.dp)
                )
            }
            Icon(
                imageVector = Lucide.House,
                contentDescription = null,
                tint = Color(0xFF212121),
                modifier = Modifier.requiredSize(84.dp)
            )
        }
    }
}
