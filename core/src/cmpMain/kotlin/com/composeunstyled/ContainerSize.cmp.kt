package com.composeunstyled

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.DpSize

@Composable
actual fun currentWindowContainerSize(): DpSize {
    return with(LocalDensity.current) {
        val intSize = LocalWindowInfo.current.containerSize
        DpSize(intSize.width.toDp(), intSize.height.toDp())
    }
}