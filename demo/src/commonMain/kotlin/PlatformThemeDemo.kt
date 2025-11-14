package com.composeunstyled.demo

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composeunstyled.Button
import com.composeunstyled.Stack
import com.composeunstyled.StackOrientation
import com.composeunstyled.Text
import com.composeunstyled.currentWindowContainerSize
import com.composeunstyled.outline
import com.composeunstyled.platformtheme.EmojiVariant
import com.composeunstyled.platformtheme.SpokenLanguage
import com.composeunstyled.platformtheme.WebFontOptions
import com.composeunstyled.platformtheme.bright
import com.composeunstyled.platformtheme.buildPlatformTheme
import com.composeunstyled.platformtheme.dimmed
import com.composeunstyled.platformtheme.heading1
import com.composeunstyled.platformtheme.heading2
import com.composeunstyled.platformtheme.heading3
import com.composeunstyled.platformtheme.heading4
import com.composeunstyled.platformtheme.heading5
import com.composeunstyled.platformtheme.heading6
import com.composeunstyled.platformtheme.heading7
import com.composeunstyled.platformtheme.heading8
import com.composeunstyled.platformtheme.heading9
import com.composeunstyled.platformtheme.indications
import com.composeunstyled.platformtheme.interactiveSize
import com.composeunstyled.platformtheme.interactiveSizes
import com.composeunstyled.platformtheme.roundedFull
import com.composeunstyled.platformtheme.shapes
import com.composeunstyled.platformtheme.sizeDefault
import com.composeunstyled.platformtheme.sizeMinimum
import com.composeunstyled.platformtheme.text1
import com.composeunstyled.platformtheme.text2
import com.composeunstyled.platformtheme.text3
import com.composeunstyled.platformtheme.text4
import com.composeunstyled.platformtheme.text5
import com.composeunstyled.platformtheme.text6
import com.composeunstyled.platformtheme.text7
import com.composeunstyled.platformtheme.text8
import com.composeunstyled.platformtheme.text9
import com.composeunstyled.platformtheme.textStyles
import com.composeunstyled.theme.Theme


private val PlatformTheme = buildPlatformTheme(
    webFontOptions = WebFontOptions(
        supportedLanguages = listOf(
            SpokenLanguage.Korean,
            SpokenLanguage.Japanese,
            SpokenLanguage.ChineseSimplified,
        ),
        emojiVariant = EmojiVariant.Colored
    )
)

@Composable
fun PlatformThemeDemo() {
    PlatformTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            TypographyDemo()
            TextStylesDemo()
            Column(
                modifier = Modifier.fillMaxWidth().widthIn(max = 1200.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Indications", style = Theme[textStyles][text9])
                MaterialButtons()
                IosButtons()
                MacOsButtons()
                ShadcnButtons()
            }
        }
    }
}

@Composable
fun TypographyDemo() {
    Text("Typography", style = Theme[textStyles][text9])

    Text("The quick brown fox jumps over the lazy dog 😊🦊😘", style = Theme[textStyles][heading9])
    Text("The quick brown fox jumps over the lazy dog 😊🦊😘", style = Theme[textStyles][text9])

    Text("Multilanguage", style = Theme[textStyles][text9])

    Text("Greek: Η γρήγορη καφέ αλεπού πηδά πάνω από το τεμπέλικο σκυλί")
    Text("Korean: 빠른 갈색 여우가 게으른 개를 뛰어넘습니다")
    Text("Japanese: あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわをん アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン")
    Text("Chinese Simplified: 敏捷的棕色狐狸跳过懒狗")
    Text("Chinese Traditional: 敏捷的棕色狐狸跳過懶狗")
}

@Composable
private fun MaterialButtons() {
    val isWide = currentWindowContainerSize().width >= 600.dp
    val orientation = if (isWide) StackOrientation.Horizontal else StackOrientation.Vertical

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val MaterialContentPaddingValues = PaddingValues(horizontal = 24.dp)

        Text("Material 3", style = Theme[textStyles][text5])
        Stack(
            orientation = orientation,
            modifier = Modifier.fillMaxWidth(),
            spacing = 20.dp
        ) {
            // Filled Button (Material 3 Filled)
            val filledInteraction = remember { MutableInteractionSource() }
            Button(
                onClick = {},
                backgroundColor = Color(0xFF6750A4),
                contentColor = Color.White,
                contentPadding = MaterialContentPaddingValues,
                shape = RoundedCornerShape(100.dp),
                interactionSource = filledInteraction,
                modifier = Modifier
                    .interactiveSize(Theme[interactiveSizes][sizeDefault])
            ) {
                Text("Filled")
            }

            // Outlined Button (Material 3 Outlined)
            val outlinedInteraction = remember { MutableInteractionSource() }
            Button(
                onClick = {},
                backgroundColor = Color.Transparent,
                contentColor = Color(0xFF6750A4),
                contentPadding = MaterialContentPaddingValues,
                shape = RoundedCornerShape(100.dp),
                interactionSource = outlinedInteraction,
                indication = Theme[indications][dimmed],
                modifier = Modifier
                    .outline(
                        width = 1.dp,
                        color = Color(0xFF79747E),
                        shape = RoundedCornerShape(100.dp),
                        offset = 0.dp
                    )
                    .interactiveSize(Theme[interactiveSizes][sizeDefault])
            ) {
                Text("Outlined")
            }

            // Text Button (Material 3 Text)
            val textInteraction = remember { MutableInteractionSource() }
            Button(
                onClick = {},
                backgroundColor = Color.Transparent,
                contentColor = Color(0xFF6750A4),
                contentPadding = MaterialContentPaddingValues,
                shape = RoundedCornerShape(100.dp),
                interactionSource = textInteraction,
                indication = Theme[indications][dimmed],
                modifier = Modifier
                    .interactiveSize(Theme[interactiveSizes][sizeDefault])
            ) {
                Text("Text")
            }

            // Tonal Button (Material 3 Filled Tonal)
            val tonalInteraction = remember { MutableInteractionSource() }
            Button(
                onClick = {},
                backgroundColor = Color(0xFFE8DEF8),
                contentColor = Color(0xFF1D192B),
                contentPadding = MaterialContentPaddingValues,
                shape = RoundedCornerShape(100.dp),
                indication = Theme[indications][dimmed],
                interactionSource = tonalInteraction,
                modifier = Modifier
                    .interactiveSize(Theme[interactiveSizes][sizeDefault])
            ) {
                Text("Tonal")
            }
        }
    }
}

@Composable
private fun IosButtons() {
    val isWide = currentWindowContainerSize().width >= 600.dp
    val orientation = if (isWide) StackOrientation.Horizontal else StackOrientation.Vertical

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("iOS", style = Theme[textStyles][text5])
        Stack(
            orientation = orientation,
            modifier = Modifier.fillMaxWidth(),
            spacing = 20.dp
        ) {
            val iosPaddingValues = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            val iosAccent = Color(0xFF0088FF)

            // Bordered Prominent Button (SwiftUI .borderedProminent - iOS 15+)
            val prominentInteraction = remember { MutableInteractionSource() }
            val interactiveSize = Theme[interactiveSizes][sizeMinimum]

            Button(
                onClick = {},
                backgroundColor = iosAccent,
                contentColor = Color.White,
                contentPadding = iosPaddingValues,
                shape = Theme[shapes][roundedFull],
                interactionSource = prominentInteraction,
                modifier = Modifier
                    .interactiveSize(interactiveSize)
            ) {
                Text("Bordered Prominent")
            }


            // Bordered Button (SwiftUI .bordered - iOS 15+)
            val borderedInteraction = remember { MutableInteractionSource() }
            Button(
                onClick = {},
                backgroundColor = Color(0xFFE9E9EB), // iOS system gray 6
                contentColor = iosAccent,
                contentPadding = iosPaddingValues,
                shape = Theme[shapes][roundedFull],
                indication = Theme[indications][bright],
                interactionSource = borderedInteraction,
                modifier = Modifier
                    .interactiveSize(interactiveSize)
            ) {
                Text("Bordered")
            }


            // Borderless Button (SwiftUI .borderless)
            // Default style on iOS - applies tint color
            val borderlessInteraction = remember { MutableInteractionSource() }
            Button(
                onClick = {},
                backgroundColor = Color.Transparent,
                contentColor = iosAccent,
                contentPadding = iosPaddingValues,
                shape = RoundedCornerShape(0.dp),
                indication = Theme[indications][bright],
                interactionSource = borderlessInteraction,
                modifier = Modifier
                    .interactiveSize(interactiveSize)
            ) {
                Text("Borderless")
            }

            // Plain Button (SwiftUI .plain)
            val plainInteraction = remember { MutableInteractionSource() }
            Button(
                onClick = {},
                backgroundColor = Color.Transparent,
                contentColor = Color.Black,
                contentPadding = iosPaddingValues,
                shape = RoundedCornerShape(0.dp),
                interactionSource = plainInteraction,
                modifier = Modifier
                    .interactiveSize(interactiveSize)
            ) {
                Text("Plain")
            }
        }
    }
}

@Composable
private fun MacOsButtons() {
    val isWide = currentWindowContainerSize().width >= 600.dp
    val orientation = if (isWide) StackOrientation.Horizontal else StackOrientation.Vertical
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("macOS", style = Theme[textStyles][text5])
        Stack(
            orientation = orientation,
            modifier = Modifier.fillMaxWidth(),
            spacing = 20.dp
        ) {
            val macPaddingValues = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            val macSecondary = Color(0xFFECECEC)

            // Bordered Prominent Button (SwiftUI .borderedProminent)
            val prominentInteraction = remember { MutableInteractionSource() }
            Button(
                onClick = {},
                backgroundColor = Color(0xFF007AFF),
                contentColor = Color.White,
                contentPadding = macPaddingValues,
                shape = RoundedCornerShape(6.dp),
                interactionSource = prominentInteraction,
                modifier = Modifier
                    .interactiveSize(Theme[interactiveSizes][sizeMinimum])
            ) {
                Text("Bordered Prominent")
            }

            // Bordered Button (SwiftUI .bordered)
            val borderedInteraction = remember { MutableInteractionSource() }
            Button(
                onClick = {},
                backgroundColor = macSecondary,
                contentColor = Color.Black,
                contentPadding = macPaddingValues,
                shape = RoundedCornerShape(6.dp),
                indication = Theme[indications][dimmed],
                interactionSource = borderedInteraction,
                modifier = Modifier
                    .interactiveSize(Theme[interactiveSizes][sizeMinimum])
            ) {
                Text("Bordered")
            }

            // Borderless Button (SwiftUI .borderless)
            val borderlessInteraction = remember { MutableInteractionSource() }
            Button(
                onClick = {},
                backgroundColor = Color.Transparent,
                contentColor = Color(0xFF8E8E93), // iOS/macOS system gray
                contentPadding = macPaddingValues,
                shape = RoundedCornerShape(6.dp),
                indication = Theme[indications][dimmed],
                interactionSource = borderlessInteraction,
                modifier = Modifier
                    .interactiveSize(Theme[interactiveSizes][sizeMinimum])
            ) {
                Text("Borderless")
            }

            // Plain Button (SwiftUI .plain)
            val plainInteraction = remember { MutableInteractionSource() }
            Button(
                onClick = {},
                backgroundColor = Color.Transparent,
                contentColor = Color(0xFF272727),
                contentPadding = macPaddingValues,
                shape = RoundedCornerShape(0.dp),
                interactionSource = plainInteraction,
                modifier = Modifier
                    .interactiveSize(Theme[interactiveSizes][sizeMinimum]),
            ) {
                Text("Plain")
            }
        }
    }
}

@Composable
private fun ShadcnButtons() {
    val isWide = currentWindowContainerSize().width >= 600.dp
    val orientation = if (isWide) StackOrientation.Horizontal else StackOrientation.Vertical

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Shadcn/ui", style = Theme[textStyles][text5])
        Stack(
            orientation = orientation,
            modifier = Modifier.fillMaxWidth(),
            spacing = 20.dp
        ) {
            val shadcnPaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp)

            // Primary Button
            val primaryInteraction = remember { MutableInteractionSource() }
            Button(
                onClick = {},
                backgroundColor = Color(0xFF0F172A), // slate-900
                contentColor = Color(0xFFF8FAFC), // slate-50
                contentPadding = shadcnPaddingValues,
                shape = RoundedCornerShape(6.dp),
                interactionSource = primaryInteraction,
                modifier = Modifier
                    .interactiveSize(Theme[interactiveSizes][sizeDefault])
            ) {
                Text("Primary")
            }

            // Secondary Button
            val secondaryInteraction = remember { MutableInteractionSource() }
            Button(
                onClick = {},
                backgroundColor = Color(0xFFF1F5F9), // slate-100
                contentColor = Color(0xFF0F172A), // slate-900
                contentPadding = shadcnPaddingValues,
                shape = RoundedCornerShape(6.dp),
                indication = Theme[indications][dimmed],
                interactionSource = secondaryInteraction,
                modifier = Modifier
                    .interactiveSize(Theme[interactiveSizes][sizeDefault])
            ) {
                Text("Secondary")
            }

            // Outline Button
            val outlineInteraction = remember { MutableInteractionSource() }
            Button(
                onClick = {},
                backgroundColor = Color.Transparent,
                contentColor = Color(0xFF0F172A), // slate-900
                contentPadding = shadcnPaddingValues,
                shape = RoundedCornerShape(6.dp),
                indication = Theme[indications][dimmed],
                interactionSource = outlineInteraction,
                modifier = Modifier
                    .outline(
                        width = 1.dp,
                        color = Color(0xFFE2E8F0), // slate-200
                        shape = RoundedCornerShape(6.dp),
                        offset = 0.dp
                    )
                    .interactiveSize(Theme[interactiveSizes][sizeDefault])
            ) {
                Text("Outline")
            }

            // Ghost Button
            val ghostInteraction = remember { MutableInteractionSource() }
            Button(
                onClick = {},
                backgroundColor = Color.Transparent,
                contentColor = Color(0xFF0F172A), // slate-900
                contentPadding = shadcnPaddingValues,
                shape = RoundedCornerShape(6.dp),
                indication = Theme[indications][dimmed],
                interactionSource = ghostInteraction,
                modifier = Modifier
                    .interactiveSize(Theme[interactiveSizes][sizeDefault])
            ) {
                Text("Ghost")
            }

            // Destructive Button
            val destructiveInteraction = remember { MutableInteractionSource() }
            Button(
                onClick = {},
                backgroundColor = Color(0xFFEF4444), // red-500
                contentColor = Color.White,
                contentPadding = shadcnPaddingValues,
                shape = RoundedCornerShape(6.dp),
                interactionSource = destructiveInteraction,
                modifier = Modifier
                    .interactiveSize(Theme[interactiveSizes][sizeDefault])
            ) {
                Text("Destructive")
            }
        }
    }
}

@Composable
private fun TextStylesDemo() {
    Column(
        modifier = Modifier.fillMaxWidth().widthIn(max = 1200.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val isWide = currentWindowContainerSize().width >= 600.dp
        val orientation = if (isWide) StackOrientation.Horizontal else StackOrientation.Vertical
        Text("Text Styles", style = Theme[textStyles][text9])

        Stack(
            orientation = orientation,
            modifier = Modifier.fillMaxWidth(),
            spacing = 24.dp
        ) {
            val text: String? = null
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text ?: "Text 9", style = Theme[textStyles][text9])
                Text(text ?: "Text 8", style = Theme[textStyles][text8])
                Text(text ?: "Text 7", style = Theme[textStyles][text7])
                Text(text ?: "Text 6", style = Theme[textStyles][text6])
                Text(text ?: "Text 5", style = Theme[textStyles][text5])
                Text(text ?: "Text 4", style = Theme[textStyles][text4])
                Text(text ?: "Text 3", style = Theme[textStyles][text3])
                Text(text ?: "Text 2", style = Theme[textStyles][text2])
                Text(text ?: "Text 1", style = Theme[textStyles][text1])
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text ?: "Heading 9", style = Theme[textStyles][heading9])
                Text(text ?: "Heading 8", style = Theme[textStyles][heading8])
                Text(text ?: "Heading 7", style = Theme[textStyles][heading7])
                Text(text ?: "Heading 6", style = Theme[textStyles][heading6])
                Text(text ?: "Heading 5", style = Theme[textStyles][heading5])
                Text(text ?: "Heading 4", style = Theme[textStyles][heading4])
                Text(text ?: "Heading 3", style = Theme[textStyles][heading3])
                Text(text ?: "Heading 2", style = Theme[textStyles][heading2])
                Text(text ?: "Heading 1", style = Theme[textStyles][heading1])
            }
        }
    }
}
