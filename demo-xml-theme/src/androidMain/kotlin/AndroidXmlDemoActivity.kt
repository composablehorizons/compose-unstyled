/*
 * Copyright (c) 2026 Composable Horizons
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.composeunstyled.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composeunstyled.ProvideContentColor
import com.composeunstyled.ProvideTextStyle
import com.composeunstyled.Text
import com.composeunstyled.UnstyledButton
import com.composeunstyled.theme.Theme
import com.composeunstyled.theme.buildTheme
import com.composeunstyled.theme.resolveThemeColor
import com.composeunstyled.theme.resolveThemeDp
import com.composeunstyled.theme.resolveThemeTextAppearance

class AndroidXmlDemoActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.light(
        Color.Transparent.toArgb(),
        Color.Transparent.toArgb(),
      ),
      navigationBarStyle = SystemBarStyle.light(
        Color.Transparent.toArgb(),
        Color.Transparent.toArgb(),
      ),
    )

    setContent {
      App()
    }
  }
}

data class AppColors(
  val background: Color,
  val onBackground: Color,
  val primary: Color,
  val onPrimary: Color,
)

data class AppSpacing(
  val small: Dp,
  val medium: Dp,
  val large: Dp,
)

data class AppTextStyles(
  val body: TextStyle,
)

val AppTheme = buildTheme {
  // get a reference to the calling (themed) context
  val context = LocalContext.current

  // add the material ripple effect
  defaultIndication = ripple()

  // map your XML colors to Compose
  provide(
    AppColors(
      background = resolveThemeColor(context, R.attr.color_background),
      onBackground = resolveThemeColor(context, R.attr.color_onBackground),
      primary = resolveThemeColor(context, R.attr.color_primary),
      onPrimary = resolveThemeColor(context, R.attr.color_onPrimary),
    ),
  )

  // map your XML dimens to Compose
  provide(
    AppSpacing(
      small = resolveThemeDp(context, R.attr.spacing_small),
      medium = resolveThemeDp(context, R.attr.spacing_medium),
      large = resolveThemeDp(context, R.attr.spacing_large),
    ),
  )

  // map your XML typography to Compose
  provide(
    AppTextStyles(
      body = resolveThemeTextAppearance(context, R.attr.textStyle_body),
    ),
  )
}

@Composable
fun App() {
  AppTheme {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(Theme.get<AppColors>().background),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      ProvideTextStyle(Theme.get<AppTextStyles>().body) {
        ProvideContentColor(Theme.get<AppColors>().onBackground) {
          Text("Hello Styled World!")

          Spacer(Modifier.height(Theme.get<AppSpacing>().large))

          UnstyledButton(
            onClick = {},
            modifier = Modifier
              .clip(RoundedCornerShape(100))
              .background(Theme.get<AppColors>().primary),
          ) {
            Text("Click Me", modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
          }
        }
      }
    }
  }
}
