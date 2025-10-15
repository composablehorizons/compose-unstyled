package com.composeunstyled.demo

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pause
import com.composables.icons.lucide.SkipBack
import com.composables.icons.lucide.SkipForward
import com.composeunstyled.Button
import com.composeunstyled.Icon
import com.composeunstyled.ProvideContentColor
import com.composeunstyled.Slider
import com.composeunstyled.Text
import com.composeunstyled.Thumb
import com.composeunstyled.outline
import com.composeunstyled.rememberSliderState
import com.composeunstyled.theme.Theme
import com.composeunstyled.theme.ThemeProperty
import com.composeunstyled.theme.ThemeToken
import com.composeunstyled.theme.buildTheme
import composeunstyled.demo.generated.resources.BebasNeue
import composeunstyled.demo.generated.resources.Inter
import composeunstyled.demo.generated.resources.Res
import composeunstyled.demo.generated.resources.Roboto
import composeunstyled.demo.generated.resources.just_hoist_it_cover
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

private val colors = ThemeProperty<Color>("colors")
private val typography = ThemeProperty<TextStyle>("typography")
private val shapes = ThemeProperty<Shape>("shapes")
private val elevation = ThemeProperty<Dp>("elevation")

private val background = ThemeToken<Color>("background")
private val card = ThemeToken<Color>("surface")
private val onCard = ThemeToken<Color>("onCard")
private val outline = ThemeToken<Color>("outline")
private val accent = ThemeToken<Color>("accent")
private val primary = ThemeToken<Color>("primary")
private val onPrimary = ThemeToken<Color>("onPrimary")
private val onSecondary = ThemeToken<Color>("onSecondary")
private val secondary = ThemeToken<Color>("secondary")

private val subtle = ThemeToken<Dp>("subtle")

private val titleMedium = ThemeToken<TextStyle>("titleMedium")
private val bodyMedium = ThemeToken<TextStyle>("bodyMedium")

private val cardShape = ThemeToken<Shape>("cardShape")
private val albumCoverShape = ThemeToken<Shape>("albumCoverShape")
private val buttonShape = ThemeToken<Shape>("buttonShape")

private val LightTheme = buildTheme {
    name = "LightTheme"

    properties[colors] = mapOf(
        accent to Color(0xFF3B82F6),
        card to Color.White,
        onCard to Color(0xFF1E293B),
        outline to Color(0xFFE2E8F0),
        primary to Color(0xFF2563EB),
        onPrimary to Color.White,
        secondary to Color(0xFFE2E8F0),
        onSecondary to Color(0xFF64748B),
        background to Color(0xFFF8F9FA),
    )

    properties[typography] = mapOf(
        titleMedium to TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        bodyMedium to TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
        )
    )

    properties[shapes] = mapOf(
        cardShape to RoundedCornerShape(16.dp),
        albumCoverShape to RoundedCornerShape(12.dp),
        buttonShape to CircleShape
    )

    properties[elevation] = mapOf(
        subtle to 8.dp
    )
}

private val DarkTheme = buildTheme {
    name = "DarkTheme"

    properties[colors] = mapOf(
        accent to Color(0xFF60A5FA),
        card to Color(0xFF1E293B),
        onCard to Color(0xFFF1F5F9),
        outline to Color(0xFF374151),
        primary to Color(0xFF3B82F6),
        onPrimary to Color.White,
        secondary to Color(0xFF374151),
        onSecondary to Color(0xFF9CA3AF),
        background to Color(0xFF0F172A),
    )

    properties[typography] = mapOf(
        titleMedium to TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        bodyMedium to TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
        )
    )

    properties[shapes] = mapOf(
        cardShape to RoundedCornerShape(16.dp),
        albumCoverShape to RoundedCornerShape(12.dp),
        buttonShape to CircleShape
    )

    properties[elevation] = mapOf(
        subtle to 12.dp
    )
}

private val BrutalistTheme = buildTheme {
    name = "BrutalistTheme"

    properties[colors] = mapOf(
        accent to Color(0xFF000000),
        card to Color(0xFF00FF00),
        onCard to Color(0xFF000000),
        outline to Color(0xFF000000),
        primary to Color(0xFF000000),
        onPrimary to Color(0xFF00FF00),
        secondary to Color(0xFF000000),
        onSecondary to Color(0xFF000000),
        background to Color(0xFFFFAA00),
    )

    properties[typography] = mapOf(
        titleMedium to TextStyle(
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily(Font(Res.font.BebasNeue)),
        ),
        bodyMedium to TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(Res.font.BebasNeue)),
        )
    )

    properties[shapes] = mapOf(
        cardShape to RectangleShape,
        albumCoverShape to RectangleShape,
        buttonShape to RectangleShape
    )

    properties[elevation] = mapOf(
        subtle to 0.dp
    )
}

private val Material3Theme = buildTheme {
    name = "Material3Theme"

    properties[colors] = mapOf(
        accent to Color(0xFF6750A4),
        card to Color(0xFFFFFBFE),
        onCard to Color(0xFF1D1B20),
        outline to Color.Unspecified,
        primary to Color(0xFF6750A4),
        onPrimary to Color.White,
        secondary to Color(0xFFE8DEF8),
        onSecondary to Color(0xFF49454F),
        background to Color(0xFFFEF7FF),
    )

    properties[typography] = mapOf(
        titleMedium to TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily(Font(Res.font.Roboto)),
        ),
        bodyMedium to TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily(Font(Res.font.Roboto)),
        )
    )

    properties[shapes] = mapOf(
        cardShape to RoundedCornerShape(12.dp),
        albumCoverShape to RoundedCornerShape(8.dp),
        buttonShape to RoundedCornerShape(20.dp)
    )

    properties[elevation] = mapOf(
        subtle to 6.dp
    )
}

private val NeoBrutalistTheme = buildTheme {
    name = "NeoBrutalistTheme"

    properties[colors] = mapOf(
        accent to Color(0xFFEF4444),
        card to Color(0xFFF3F4F6),
        onCard to Color(0xFF1F2937),
        outline to Color.Black,
        primary to Color(0xFFEF4444),
        onPrimary to Color.White,
        secondary to Color(0xFFE5E7EB),
        onSecondary to Color(0xFF6B7280),
        background to Color(0xFFFAFAFA),
    )

    properties[typography] = mapOf(
        titleMedium to TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(Res.font.Inter)),
        ),
        bodyMedium to TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily(Font(Res.font.Inter)),
        )
    )

    properties[shapes] = mapOf(
        cardShape to RectangleShape,
        albumCoverShape to CutCornerShape(12.dp),
        buttonShape to CutCornerShape(8.dp)
    )

    properties[elevation] = mapOf(
        subtle to 0.dp
    )
}


private val AppleMusicTheme = buildTheme {
    name = "AppleMusicTheme"

    properties[colors] = mapOf(
        accent to Color.Black,
        card to Color(0xFFFF2D55),
        onCard to Color(0xFFFFFFFF),
        outline to Color(0xFFFF1744),
        primary to Color(0xFFFFFFFF),
        onPrimary to Color(0xFFFF2D55),
        secondary to Color(0xFFFFFFFF).copy(alpha = 0.6f),
        onSecondary to Color(0xFFFFFFFF).copy(alpha = 0.7f),
        background to Color(0xFF000000),
    )

    properties[typography] = mapOf(
        titleMedium to TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(Res.font.Inter)),
        ),
        bodyMedium to TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily(Font(Res.font.Inter)),
        )
    )

    properties[shapes] = mapOf(
        cardShape to RoundedCornerShape(20.dp),
        albumCoverShape to RoundedCornerShape(12.dp),
        buttonShape to CircleShape
    )

    properties[elevation] = mapOf(
        subtle to 8.dp
    )
}

data class ThemeInfo(
    val theme: @Composable (@Composable () -> Unit) -> Unit,
    val color: Color,
)

private val OceanTheme = buildTheme {
    name = "OceanTheme"

    properties[colors] = mapOf(
        accent to Color(0xFF0077BE),
        card to Color(0xFFE6F3FF),
        onCard to Color(0xFF003D66),
        outline to Color(0xFF87CEEB),
        primary to Color(0xFF0077BE),
        onPrimary to Color.White,
        secondary to Color(0xFFB3E0FF),
        onSecondary to Color(0xFF005C99),
        background to Color(0xFFF0F8FF),
    )

    properties[typography] = mapOf(
        titleMedium to TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily(Font(Res.font.Inter)),
        ),
        bodyMedium to TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily(Font(Res.font.Inter)),
        )
    )

    properties[shapes] = mapOf(
        cardShape to RoundedCornerShape(20.dp),
        albumCoverShape to RoundedCornerShape(16.dp),
        buttonShape to CircleShape
    )

    properties[elevation] = mapOf(
        subtle to 6.dp
    )
}

private val ForestTheme = buildTheme {
    name = "ForestTheme"

    properties[colors] = mapOf(
        accent to Color(0xFF228B22),
        card to Color(0xFFF0FFF0),
        onCard to Color(0xFF2F4F2F),
        outline to Color(0xFF90EE90),
        primary to Color(0xFF228B22),
        onPrimary to Color.White,
        secondary to Color(0xFFE0FFE0),
        onSecondary to Color(0xFF556B2F),
        background to Color(0xFFF5FFFA),
    )

    properties[typography] = mapOf(
        titleMedium to TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily(Font(Res.font.Inter)),
        ),
        bodyMedium to TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily(Font(Res.font.Inter)),
        )
    )

    properties[shapes] = mapOf(
        cardShape to RoundedCornerShape(12.dp),
        albumCoverShape to RoundedCornerShape(8.dp),
        buttonShape to RoundedCornerShape(8.dp)
    )

    properties[elevation] = mapOf(
        subtle to 4.dp
    )
}

private val SunsetTheme = buildTheme {
    name = "SunsetTheme"

    properties[colors] = mapOf(
        accent to Color(0xFFFF6B35),
        card to Color(0xFFFFF8E1),
        onCard to Color(0xFF8B4513),
        outline to Color(0xFFFFB347),
        primary to Color(0xFFFF6B35),
        onPrimary to Color.White,
        secondary to Color(0xFFFFE0B3),
        onSecondary to Color(0xFFD2691E),
        background to Color(0xFFFFF4E6),
    )

    properties[typography] = mapOf(
        titleMedium to TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily(Font(Res.font.Inter)),
        ),
        bodyMedium to TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily(Font(Res.font.Inter)),
        )
    )

    properties[shapes] = mapOf(
        cardShape to RoundedCornerShape(16.dp),
        albumCoverShape to RoundedCornerShape(12.dp),
        buttonShape to RoundedCornerShape(20.dp)
    )

    properties[elevation] = mapOf(
        subtle to 8.dp
    )
}

private val MonochromeTheme = buildTheme {
    name = "MonochromeTheme"

    properties[colors] = mapOf(
        accent to Color(0xFF000000),
        card to Color(0xFFFAFAFA),
        onCard to Color(0xFF333333),
        outline to Color(0xFFE0E0E0),
        primary to Color(0xFF000000),
        onPrimary to Color.White,
        secondary to Color(0xFFF0F0F0),
        onSecondary to Color(0xFF666666),
        background to Color(0xFFFFFFFF),
    )

    properties[typography] = mapOf(
        titleMedium to TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(Res.font.Inter)),
        ),
        bodyMedium to TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily(Font(Res.font.Inter)),
        )
    )

    properties[shapes] = mapOf(
        cardShape to RoundedCornerShape(2.dp),
        albumCoverShape to RoundedCornerShape(2.dp),
        buttonShape to RoundedCornerShape(2.dp)
    )

    properties[elevation] = mapOf(
        subtle to 1.dp
    )
}

private val GalaxyTheme = buildTheme {
    name = "GalaxyTheme"

    properties[colors] = mapOf(
        accent to Color(0xFF06B6D4),
        card to Color(0xFF1E293B),
        onCard to Color(0xFFE2E8F0),
        outline to Color(0xFF0891B2),
        primary to Color(0xFF06B6D4),
        onPrimary to Color.White,
        secondary to Color(0xFF0F172A),
        onSecondary to Color(0xFF94A3B8),
        background to Color(0xFF020617),
    )

    properties[typography] = mapOf(
        titleMedium to TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily(Font(Res.font.Inter)),
        ),
        bodyMedium to TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily(Font(Res.font.Inter)),
        )
    )

    properties[shapes] = mapOf(
        cardShape to RoundedCornerShape(24.dp),
        albumCoverShape to RoundedCornerShape(16.dp),
        buttonShape to CircleShape
    )

    properties[elevation] = mapOf(
        subtle to 12.dp
    )
}

private val availableThemes = listOf(
    ThemeInfo(LightTheme, Color(0xFFF8F9FA)),
    ThemeInfo(DarkTheme, Color(0xFF1E293B)),
    ThemeInfo(Material3Theme, Color(0xFF6750A4)),
    ThemeInfo(BrutalistTheme, Color(0xFF00FF00)),
    ThemeInfo(NeoBrutalistTheme, Color(0xFFEF4444)),
    ThemeInfo(AppleMusicTheme, Color(0xFFFF2D55)),
    ThemeInfo(OceanTheme, Color(0xFF0077BE)),
    ThemeInfo(ForestTheme, Color(0xFF228B22)),
    ThemeInfo(SunsetTheme, Color(0xFFFF6B35)),
    ThemeInfo(MonochromeTheme, Color(0xFF333333)),
    ThemeInfo(GalaxyTheme, Color(0xFF06B6D4)),
)

@Composable
fun ThemeDemo() {
    var selectedTheme by remember { mutableStateOf(0) }
    val currentTheme = availableThemes[selectedTheme].theme

    currentTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme[colors][background]).padding(vertical = 16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    availableThemes.forEachIndexed { index, themeInfo ->
                        SimpleThemeCard(index, selectedTheme, themeInfo.color) {
                            selectedTheme = index
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Box(Modifier.padding(horizontal = 16.dp)) {
                    MusicPlayerCard(Modifier.width(400.dp))
                }
            }
        }
    }
}

@Composable
fun MusicPlayerCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .outline(1.dp, Theme[colors][outline], Theme[shapes][cardShape])
            .shadow(Theme[elevation][subtle], Theme[shapes][cardShape])
            .background(Theme[colors][card], Theme[shapes][cardShape])
            .padding(24.dp)
    ) {
        ProvideContentColor(Theme[colors][onCard]) {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.just_hoist_it_cover),
                        modifier = Modifier
                            .clip(Theme[shapes][albumCoverShape])
                            .background(Theme[colors][primary])
                            .size(80.dp),
                        contentDescription = "Album Cover",
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Just hoist it!",
                            style = Theme[typography][titleMedium]
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "The Deprecated",
                            style = Theme[typography][bodyMedium],
                            color = Theme[colors][onSecondary]
                        )
                    }
                }

                val sliderState = rememberSliderState(initialValue = 0.3f)

                Slider(
                    state = sliderState,
                    modifier = Modifier.fillMaxWidth(),
                    track = {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                        ) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(Theme[colors][secondary])
                            )
                            Box(
                                Modifier
                                    .fillMaxWidth(sliderState.value)
                                    .fillMaxSize()
                                    .background(Theme[colors][accent])
                            )
                        }
                    },
                    thumb = {
                        Thumb(
                            color = Theme[colors][accent],
                            modifier = Modifier.size(16.dp),
                            shape = Theme[shapes][buttonShape]
                        )
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { },
                        contentPadding = PaddingValues(12.dp),
                        shape = Theme[shapes][buttonShape]
                    ) {
                        Icon(
                            imageVector = Lucide.SkipBack,
                            contentDescription = "Previous",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Button(
                        onClick = { },
                        backgroundColor = Theme[colors][primary],
                        contentColor = Theme[colors][onPrimary],
                        contentPadding = PaddingValues(16.dp),
                        shape = Theme[shapes][buttonShape]
                    ) {
                        Icon(
                            imageVector = Lucide.Pause,
                            contentDescription = "Pause",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Button(
                        onClick = { },
                        contentPadding = PaddingValues(12.dp),
                        shape = Theme[shapes][buttonShape]
                    ) {
                        Icon(
                            imageVector = Lucide.SkipForward,
                            contentDescription = "Next",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SimpleThemeCard(
    themeIndex: Int,
    selectedTheme: Int,
    themeColor: Color,
    onClick: () -> Unit,
) {
    val isSelected = selectedTheme == themeIndex
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(isHovered, isFocused) {
        if (isHovered || isFocused) {
            onClick()
        }
    }

    val outlineColor by animateColorAsState(if (isSelected) Theme[colors][accent] else Color.Black.copy(0.1f))
    val outlineThickness by animateDpAsState(if (isSelected) 2.dp else 1.dp)
    val offset by animateDpAsState(if (isSelected) 2.dp else 0.dp)

    Button(
        onClick = onClick,
        backgroundColor = themeColor,
        contentColor = Color.Transparent,
        contentPadding = PaddingValues(0.dp),
        shape = CircleShape,
        modifier = Modifier
            .size(32.dp)
            .outline(outlineThickness, outlineColor, CircleShape, offset),
        interactionSource = interactionSource,
    ) { }
}
