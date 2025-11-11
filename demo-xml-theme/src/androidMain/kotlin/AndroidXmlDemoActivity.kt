package com.composeunstyled.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composeunstyled.Button
import com.composeunstyled.ProvideContentColor
import com.composeunstyled.ProvideTextStyle
import com.composeunstyled.Text
import com.composeunstyled.theme.Theme
import com.composeunstyled.theme.ThemeProperty
import com.composeunstyled.theme.ThemeToken
import com.composeunstyled.theme.buildTheme
import com.composeunstyled.theme.resolveThemeColor
import com.composeunstyled.theme.resolveThemeDp
import com.composeunstyled.theme.resolveThemeTextAppearance

class AndroidXmlDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.Transparent.toArgb(), Color.Transparent.toArgb()),
            navigationBarStyle = SystemBarStyle.light(Color.Transparent.toArgb(), Color.Transparent.toArgb())
        )

        setContent {
            App()
        }
    }
}

val colors = ThemeProperty<Color>("colors")

val background = ThemeToken<Color>("background")
val onBackground = ThemeToken<Color>("onBackground")
val primary = ThemeToken<Color>("primary")
val onPrimary = ThemeToken<Color>("onPrimary")

val spacing = ThemeProperty<Dp>("spacing")
val small = ThemeToken<Dp>("small")
val medium = ThemeToken<Dp>("medium")
val large = ThemeToken<Dp>("large")

val textStyles = ThemeProperty<TextStyle>("textStyles")
val body = ThemeToken<TextStyle>("body")

val AppTheme = buildTheme {
    // get a reference to the calling (themed) context
    val context = LocalContext.current

    // add the material ripple effect
    defaultIndication = ripple()

    // map your XML colors to Compose
    properties[colors] = mapOf(
        background to resolveThemeColor(context, R.attr.color_background),
        onBackground to resolveThemeColor(context, R.attr.color_onBackground),
        primary to resolveThemeColor(context, R.attr.color_primary),
        onPrimary to resolveThemeColor(context, R.attr.color_onPrimary),
    )

    // map your XML dimens to Compose
    properties[spacing] = mapOf(
        small to resolveThemeDp(context, R.attr.spacing_small),
        medium to resolveThemeDp(context, R.attr.spacing_medium),
        large to resolveThemeDp(context, R.attr.spacing_large),
    )

    // map your XML typography to Compose
    properties[textStyles] = mapOf(
        body to resolveThemeTextAppearance(context, R.attr.textStyle_body),
    )
}

@Composable
fun App() {
    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme[colors][background]),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProvideTextStyle(Theme[textStyles][body]) {
                ProvideContentColor(Theme[colors][onBackground]) {
                    Text("Hello Styled World!")

                    Spacer(Modifier.height(Theme[spacing][large]))

                    Button(
                        onClick = {},
                        backgroundColor = Theme[colors][primary],
                        contentColor = Theme[colors][onPrimary],
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(100)
                    ) {
                        Text("Click Me")
                    }
                }
            }
        }
    }
}
