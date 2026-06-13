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
@file:Suppress("DEPRECATION", "ktlint:standard:max-line-length")

package com.composeunstyled.theme

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.composeunstyled.LocalContentColor
import com.composeunstyled.LocalMinimumComponentInteractiveSize
import com.composeunstyled.LocalTextStyle

object Theme {
  @Composable
  operator fun <T> get(property: ThemeProperty<T>): ThemeValues<T> {
    val theme = LocalTheme.current

    @Suppress("UNCHECKED_CAST")
    val resolvedValue = theme.properties[property] as? ThemeValues<T>

    return resolvedValue ?: error(
      "There is no ${property.name} property in the ${theme.name} theme. To fix this: " +
        "1. Create a design property: val ${property.name} = " +
        "ThemeProperty<Type>(\"${property.name}\"), " +
        "2. Pass it to your theme definition: buildTheme { properties[${property.name}] = " +
        "mapOf(${property.name} to TODO(\"Give ${property.name} a value\")) }",
    )
  }
}

@ConsistentCopyVisibility
data class ThemeValues<T> internal constructor(
  internal val propertyName: String,
  internal val values: Map<ThemeToken<T>, T>,
) {
  @Composable
  operator fun get(token: ThemeToken<T>): T {
    return values[token] ?: run {
      val themeName = LocalTheme.current.name
      error("Tried to access the value of the token called ${token.name}, but no tokens with that name are defined within the $propertyName property. You probably forgot to set a ${token.name} token in your theme definition. The resolved theme was $themeName")
    }
  }

  internal fun copyWithUpdatedValue(token: ThemeToken<T>, newValue: T): ThemeValues<T> {
    val updatedValues = values.toMutableMap()
    updatedValues[token] = newValue
    return ThemeValues(propertyName, updatedValues)
  }
}

internal typealias ComposableWithContent = @Composable (@Composable () -> Unit) -> Unit
typealias ThemeComposable = ComposableWithContent
internal typealias ThemeExtension = @Composable (@Composable () -> Unit) -> Unit

data class ThemeProperty<T>(val name: String)
data class ThemeToken<T>(val name: String)

fun buildTheme(themeAction: @Composable ThemeBuilder.() -> Unit = {}): ThemeComposable {
  return { content ->
    val builder = ThemeBuilder().apply {
      themeAction()
    }

    val allProperties = builder.properties.entries

    val defaultIndication = builder.defaultIndication ?: NoIndication
    val textSelectionColors = builder.defaultTextSelectionColors ?: UnspecifiedTextSelectionColors

    val minInteractiveSize = builder.defaultComponentInteractiveSize

    val finalInteractiveSize = ComponentInteractiveSize(
      touchInteractionSize = minInteractiveSize.touchInteractionSize,
      nonTouchInteractionSize = minInteractiveSize.nonTouchInteractionSize,
    )

    val theme = ResolvedTheme(builder.name, allProperties)

    CompositionLocalProvider(
      LocalTheme provides theme,
      LocalIndication provides defaultIndication,
      LocalTextStyle provides builder.defaultTextStyle,
      LocalContentColor provides builder.defaultContentColor,
      LocalTextSelectionColors provides textSelectionColors,
      LocalMinimumComponentInteractiveSize provides finalInteractiveSize,
    ) {
      val currentExtendedTheme = builder.extendedTheme

      if (currentExtendedTheme == null) {
        content()
      } else {
        var wasContentCalled = false

        currentExtendedTheme {
          check(wasContentCalled.not()) {
            "You may call the content lambda of extend {} exactly once."
          }

          wasContentCalled = true
          content()
        }
      }
    }
  }
}

internal class ResolvedTheme(
  internal val name: String,
  internal val properties: Map<ThemeProperty<*>, ThemeValues<*>>,
)

internal val LocalTheme =
  staticCompositionLocalOf<ResolvedTheme> {
    error(
      "No theme was set. In order to use the Theme object you need to wrap your content with a theme @Composable returned by the buildTheme {} function.",
    )
  }

private val UnspecifiedTextSelectionColors = TextSelectionColors(
  handleColor = Color.Unspecified,
  backgroundColor = Color.Unspecified,
)

@DslMarker
annotation class ThemeBuilderMarker

@Deprecated(
  message = "This will be removed in 3.0. It is now up to you to implement this behavior if it is a requirement for your design system.",
)
data class ComponentInteractiveSize(
  val nonTouchInteractionSize: Dp = Dp.Unspecified,
  val touchInteractionSize: Dp = Dp.Unspecified,
)

@Deprecated(
  message = "This will be removed in 3.0. It is now up to you to implement this behavior if it is a requirement for your design system.",
)
fun ComponentInteractiveSize(size: Dp): ComponentInteractiveSize {
  return ComponentInteractiveSize(size, size)
}

@ThemeBuilderMarker
class ThemeBuilder internal constructor() {
  var name: String = "Theme"

  var defaultIndication: Indication? by mutableStateOf(null)
  var defaultTextStyle: TextStyle by mutableStateOf(TextStyle.Default)
  var defaultContentColor: Color by mutableStateOf(Color.Unspecified)
  var defaultTextSelectionColors: TextSelectionColors? by mutableStateOf(null)

  @Deprecated(
    message = "This will be removed in 3.0. It is now up to you to implement this behavior if it is a requirement for your design system.",
  )
  var defaultComponentInteractiveSize: ComponentInteractiveSize by mutableStateOf(
    ComponentInteractiveSize(Dp.Unspecified, Dp.Unspecified),
  )

  val properties = MutableThemeProperties()

  internal var extendedTheme: ThemeExtension? = null
    private set

  fun extend(extension: @Composable (@Composable () -> Unit) -> Unit) {
    check(this.extendedTheme == null) {
      "Themes can only be extended exactly once. Make sure you use the `extend {}` block within your buildTheme {} only once."
    }

    this.extendedTheme = extension
  }
}

class MutableThemeProperties internal constructor() {
  internal val entries = mutableStateMapOf<ThemeProperty<*>, ThemeValues<*>>()

  operator fun <T> set(property: ThemeProperty<T>, values: Map<ThemeToken<T>, T>) {
    entries[property] = ThemeValues(property.name, values)
  }

  operator fun <T> get(property: ThemeProperty<T>): ThemeValues<T> {
    return entries[property] as? ThemeValues<T>
      ?: error("No theme was set. In order to use the theme @Composable function.")
  }
}

data class OverriddenValue<T>(val token: ThemeToken<T>, val value: T)

infix fun <T> ThemeToken<T>.provides(value: T): OverriddenValue<T> {
  return OverriddenValue(this, value)
}
