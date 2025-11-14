package com.composeunstyled.platformtheme

import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composeunstyled.theme.ThemeComposable
import com.composeunstyled.theme.ThemeProperty
import com.composeunstyled.theme.ThemeToken
import com.composeunstyled.theme.buildTheme
import com.composeunstyled.theme.rememberColoredIndication

val indications = ThemeProperty<Indication>("indications")
val bright = ThemeToken<Indication>("bright")
val dimmed = ThemeToken<Indication>("dimmed")

val textStyles = ThemeProperty<TextStyle>("textStyles")
val text1 = ThemeToken<TextStyle>("text1")
val text2 = ThemeToken<TextStyle>("text2")
val text3 = ThemeToken<TextStyle>("text3")
val text4 = ThemeToken<TextStyle>("text4")
val text5 = ThemeToken<TextStyle>("text5")
val text6 = ThemeToken<TextStyle>("text6")
val text7 = ThemeToken<TextStyle>("text7")
val text8 = ThemeToken<TextStyle>("text8")
val text9 = ThemeToken<TextStyle>("text9")

val heading1 = ThemeToken<TextStyle>("heading1")
val heading2 = ThemeToken<TextStyle>("heading2")
val heading3 = ThemeToken<TextStyle>("heading3")
val heading4 = ThemeToken<TextStyle>("heading4")
val heading5 = ThemeToken<TextStyle>("heading5")
val heading6 = ThemeToken<TextStyle>("heading6")
val heading7 = ThemeToken<TextStyle>("heading7")
val heading8 = ThemeToken<TextStyle>("heading8")
val heading9 = ThemeToken<TextStyle>("heading9")

val shapes = ThemeProperty<Shape>("shapes")
val roundedNone = ThemeToken<Shape>("rounded_none")
val roundedSmall = ThemeToken<Shape>("rounded_small")
val roundedMedium = ThemeToken<Shape>("rounded_medium")
val roundedLarge = ThemeToken<Shape>("rounded_large")
val roundedFull = ThemeToken<Shape>("rounded_full")

val interactiveSizes = ThemeProperty<Dp>("interactive_sizes")
val sizeDefault = ThemeToken<Dp>("size_default")
val sizeMinimum = ThemeToken<Dp>("size_minimum")

fun buildPlatformTheme(
    webFontOptions: WebFontOptions = WebFontOptions()
): ThemeComposable {
    return buildTheme {
        properties[indications] = mapOf(
            bright to platformValue(
                android = { rememberPlatformIndication(Color.White) },
                iOS = { rememberIosIndication(Color.White) },
                jvm = {
                    rememberColoredIndication(
                        color = Color.White,
                        pressedAlpha = 0.25f,
                        hoveredAlpha = 0f,
                        draggedAlpha = 0f,
                        focusedAlpha = 0f,
                    )
                },
                web = { rememberWebIndication(Color.White) }
            ),
            dimmed to platformValue(
                android = { rememberPlatformIndication(Color.Black) },
                iOS = { rememberIosIndication(Color.Black) },
                jvm = {
                    rememberColoredIndication(
                        color = Color.Black,
                        pressedAlpha = 0.1f,
                        hoveredAlpha = 0f,
                        draggedAlpha = 0f,
                        focusedAlpha = 0f,
                    )
                },
                web = { rememberWebIndication(Color.Black) }
            )
        )
        defaultIndication = properties[indications][bright]

        properties[interactiveSizes] = mapOf(
            sizeMinimum to platformValue(
                android = {
                    32.dp // the size of a chip in material 3
                },
                iOS = {
                    28.dp // HIG
                },
                jvm = {
                    20.dp // HIG – other platforms don't specify a value
                },
                web = {
                    20.dp // HIG – other platforms don't specify a value
                }
            ),
            sizeDefault to platformValue(
                android = {
                    48.dp // M3
                },
                iOS = {
                    44.dp // HIG
                },
                jvm = {
                    28.dp // HIG – other platforms don't specify a value
                },
                web = {
                    28.dp // HIG – other platforms don't specify a value
                }
            ),
        )

        val areFontsLoaded = loadPlatformFonts(webFontOptions)
        CompositionLocalProvider(LocalPlatformFontsState provides areFontsLoaded) {
            val SansSerif = PlatformFonts.SansSerif

            properties[textStyles] = mapOf(
                heading1 to TextStyle(fontSize = typeScale(1), fontWeight = FontWeight.Bold, fontFamily = SansSerif),
                heading2 to TextStyle(fontSize = typeScale(2), fontWeight = FontWeight.Bold, fontFamily = SansSerif),
                heading3 to TextStyle(fontSize = typeScale(3), fontWeight = FontWeight.Bold, fontFamily = SansSerif),
                heading4 to TextStyle(fontSize = typeScale(4), fontWeight = FontWeight.Bold, fontFamily = SansSerif),
                heading5 to TextStyle(fontSize = typeScale(5), fontWeight = FontWeight.Bold, fontFamily = SansSerif),
                heading6 to TextStyle(fontSize = typeScale(6), fontWeight = FontWeight.Bold, fontFamily = SansSerif),
                heading7 to TextStyle(fontSize = typeScale(7), fontWeight = FontWeight.Bold, fontFamily = SansSerif),
                heading8 to TextStyle(fontSize = typeScale(8), fontWeight = FontWeight.Bold, fontFamily = SansSerif),
                heading9 to TextStyle(fontSize = typeScale(9), fontWeight = FontWeight.Bold, fontFamily = SansSerif),
                text1 to TextStyle(fontSize = typeScale(1), fontFamily = SansSerif),
                text2 to TextStyle(fontSize = typeScale(2), fontFamily = SansSerif),
                text3 to TextStyle(fontSize = typeScale(3), fontFamily = SansSerif),
                text4 to TextStyle(fontSize = typeScale(4), fontFamily = SansSerif),
                text5 to TextStyle(fontSize = typeScale(5), fontFamily = SansSerif),
                text6 to TextStyle(fontSize = typeScale(6), fontFamily = SansSerif),
                text7 to TextStyle(fontSize = typeScale(7), fontFamily = SansSerif),
                text8 to TextStyle(fontSize = typeScale(8), fontFamily = SansSerif),
                text9 to TextStyle(fontSize = typeScale(9), fontFamily = SansSerif),
            )
        }
        defaultTextStyle = properties[textStyles][text4]

        properties[shapes] = mapOf(
            roundedNone to RectangleShape,
            roundedSmall to RoundedCornerShape(4.dp),
            roundedMedium to RoundedCornerShape(6.dp),
            roundedLarge to RoundedCornerShape(8.dp),
            roundedFull to RoundedCornerShape(100),
        )
    }
}

@Composable
private fun rememberIosIndication(tint: Color): IndicationNodeFactory {
    return rememberColoredIndication(
        color = tint,
        pressedAlpha = 0.25f,
        hoveredAlpha = 0f,
        draggedAlpha = 0f,
        focusedAlpha = 0f,
        showAnimationSpec = snap(),
        hideAnimationSpec = tween(300)
    )
}

@Composable
private fun rememberWebIndication(tint: Color): IndicationNodeFactory = rememberColoredIndication(
    color = tint,
    pressedAlpha = 0.08f,
    hoveredAlpha = 0.06f,
    draggedAlpha = 0f,
    focusedAlpha = 0f,
    showAnimationSpec = tween(100),
    hideAnimationSpec = tween(100)
)

private fun typeScale(scale: Int): TextUnit {
    return platformValue(
        android = { materialTypeScale(scale) },
        iOS = { iosTypeScale(scale) },
        web = { radixTypeScale(scale) },
        jvm = { os -> jvmTypeScale(os, scale) }
    )
}

private fun materialTypeScale(scale: Int): TextUnit {
    // values from Material 3 typography
    return when (scale) {
        9 -> 36.sp // Display Small

        8 -> 32.sp // Headline Large
        7 -> 28.sp // Headline Medium
        6 -> 24.sp // Headline Small

        5 -> 22.sp // Title Large

        4 -> 16.sp // Body Large (default)
        3 -> 14.sp // Body Medium
        2 -> 12.sp // Body Small

        1 -> 11.sp // Label Small
        else -> error("Invalid scale $scale")
    }
}

private fun iosTypeScale(scale: Int): TextUnit {
    // values from Apple HIG iOS text styles
    return when (scale) {
        // display
        9 -> 34.sp // Large Title

        // title
        8 -> 28.sp // Title 1
        7 -> 22.sp // Title 2
        6 -> 20.sp  // Title 3

        // headline
        5 -> 18.sp // N/A

        // body
        4 -> 17.sp // Body (default)
        3 -> 16.sp // Callout
        2 -> 13.sp // Footnote

        // caption
        1 -> 12.sp // Caption 2
        else -> error("Invalid scale $scale")
    }
}

private fun radixTypeScale(scale: Int): TextUnit {
    // Radix but a bit shifted
    return when (scale) {
        9 -> 35.sp

        8 -> 28.sp
        7 -> 24.sp
        6 -> 20.sp

        5 -> 18.sp

        4 -> 16.sp // base
        3 -> 14.sp
        2 -> 12.sp

        1 -> 10.sp
        else -> error("Invalid scale $scale")
    }
}

private fun jvmTypeScale(os: OperatingSystem, scale: Int): TextUnit {
    return when (os) {
        OperatingSystem.Mac -> macTypeScale(scale)
        OperatingSystem.Windows -> windowsTypeScale(scale)
        else -> radixTypeScale(scale)
    }
}

private fun macTypeScale(scale: Int): TextUnit {
    return when (scale) {
        9 -> 26.sp // Large Title

        8 -> 22.sp // Title 1
        7 -> 17.sp // Title 2
        6 -> 15.sp // Title 3

        5 -> 14.sp // -- filler

        4 -> 13.sp // base
        3 -> 12.sp // Callout
        2 -> 11.sp // Subheadline

        1 -> 10.sp // Caption 1
        else -> error("Invalid scale $scale")
    }
}

private fun windowsTypeScale(scale: Int): TextUnit {
    // approx values from Fluid 2
    return when (scale) {
        9 -> 68.sp // display

        8 -> 40.sp // Large title
        7 -> 28.sp // Title
        6 -> 20.sp  // Subtitle

        5 -> 18.sp // body large

        4 -> 14.sp // base (body)
        3 -> 13.sp // -- filer
        2 -> 12.sp // caption

        1 -> 10.sp // filler
        else -> error("Invalid scale $scale")
    }
}

@Composable
internal expect fun loadPlatformFonts(webFontOptions: WebFontOptions): PlatformFontsState

@Composable
internal expect fun rememberPlatformIndication(tint: Color): Indication

fun Modifier.interactiveSize(size: Dp): Modifier {
    return this then Modifier.sizeIn(minWidth = size, minHeight = size)
}
