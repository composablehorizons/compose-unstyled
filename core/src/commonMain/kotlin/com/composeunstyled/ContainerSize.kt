package com.composeunstyled

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize

/**
 * Returns the size of the Window that hosts this [Composable].
 *
 * The function observes when the Window sizes changes and will trigger a recomposition when a size change is detected
 */
@Composable
expect fun currentWindowContainerSize(): DpSize
