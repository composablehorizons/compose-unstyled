package com.composables.core

import androidx.compose.runtime.Composable

@Composable
internal expect fun Modal(protectNavBars: Boolean = false, content: @Composable () -> Unit)