package com.composables.core

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
public fun Icon(painter: Painter, contentDescription: String?, tint: Color, modifier: Modifier = Modifier) {
    val colorFilter = remember(tint) {
        if (tint == Color.Unspecified) null else ColorFilter.tint(tint)
    }
    Image(painter, contentDescription, modifier, colorFilter = colorFilter)
}

@Composable
public fun Icon(imageBitmap: ImageBitmap, contentDescription: String?, tint: Color, modifier: Modifier = Modifier) {
    val colorFilter = remember(tint) {
        if (tint == Color.Unspecified) null else ColorFilter.tint(tint)
    }
    Image(imageBitmap, contentDescription, modifier, colorFilter = colorFilter)
}

@Composable
public fun Icon(imageVector: ImageVector, contentDescription: String?, tint: Color, modifier: Modifier = Modifier) {
    val colorFilter = remember(tint) {
        if (tint == Color.Unspecified) null else ColorFilter.tint(tint)
    }
    Image(imageVector, contentDescription, modifier, colorFilter = colorFilter)
}
