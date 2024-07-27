package com.composables.core

import androidx.compose.runtime.Composable

@Composable
expect internal fun Modal(protectNavBars: Boolean = false, content: @Composable () -> Unit)