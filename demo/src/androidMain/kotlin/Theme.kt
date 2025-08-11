package com.composeunstyled.demo

import androidx.compose.foundation.LocalIndication
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ripple
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.composeunstyled.theme.*

// Define theme properties
val colors = ThemeProperty<Color>("colors")
val dimensions = ThemeProperty<Dp>("dimensions")
val textSizes = ThemeProperty<TextUnit>("textSizes")
val textStyles = ThemeProperty<TextStyle>("textStyles")
val values = ThemeProperty<String>("values")
val flags = ThemeProperty<Boolean>("flags")
val numbers = ThemeProperty<Int>("numbers")
val floats = ThemeProperty<Float>("floats")

// Define theme tokens
val background = ThemeToken<Color>("background")
val onBackgroundColor = ThemeToken<Color>("onBackgroundColor")
val primary = ThemeToken<Color>("primary")
val onPrimary = ThemeToken<Color>("onPrimary")

val demoDimension = ThemeToken<Dp>("demoDimension")
val demoTextSize = ThemeToken<TextUnit>("demoTextSize")

val header = ThemeToken<TextStyle>("heder")
val body = ThemeToken<TextStyle>("body")

val demoString = ThemeToken<String>("demoString")

val isGoat = ThemeToken<Boolean>("isGoat")
val demoBoolean = ThemeToken<Boolean>("demoBoolean")
val demoInt = ThemeToken<Int>("demoInt")
val demoFloat = ThemeToken<Float>("demoFloat")

val AndroidTheme = buildTheme {
    val context = LocalContext.current

    defaultIndication = ripple()

    properties[colors] = mapOf(
        background to resolveThemeColor(context, R.attr.backgroundColor),
        onBackgroundColor to resolveThemeColor(context, R.attr.onBackgroundColor),
        primary to resolveThemeColor(context, R.attr.primary),
        onPrimary to resolveThemeColor(context, R.attr.onPrimary)
    )

    properties[dimensions] = mapOf(
        demoDimension to resolveThemeDp(context, R.attr.demoDimension)
    )

    properties[textSizes] = mapOf(
        demoTextSize to resolveThemeSp(context, R.attr.demoTextSize)
    )

    val bodyStyle = resolveThemeTextAppearance(context, R.attr.body)
    defaultTextStyle = bodyStyle

    properties[textStyles] = mapOf(
        header to resolveThemeTextAppearance(context, R.attr.header),
        body to bodyStyle,
    )

    properties[values] = mapOf(
        demoString to resolveThemeString(context, R.attr.demoString)
    )

    properties[flags] = mapOf(
        isGoat to resolveThemeBoolean(context, R.attr.isGoat),
        demoBoolean to resolveThemeBoolean(context, R.attr.demoBoolean)
    )

    properties[numbers] = mapOf(
        demoInt to resolveThemeInt(context, R.attr.demoInt)
    )

    properties[floats] = mapOf(
        demoFloat to resolveThemeFloat(context, R.attr.demoFloat)
    )
}
