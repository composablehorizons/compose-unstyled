package com.composeunstyled

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.window.layout.WindowMetricsCalculator

@Composable
actual fun currentWindowContainerSize(): DpSize {
    // Observe view configuration changes and recalculate the size class on each change. We can't
    // use Activity#onConfigurationChanged as this will sometimes fail to be called on different
    // API levels, hence why this function needs to be @Composable so we can observe the
    // ComposeView's configuration changes.
    LocalConfiguration.current
    val density = LocalDensity.current
    val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(LocalContext.current)
    return with(density) { metrics.bounds.toComposeRect().size.toDpSize() }
}
