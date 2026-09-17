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

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SnapSpec
import androidx.compose.animation.core.snap
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositeKeyHashCode
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.currentCompositeKeyHashCode
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
  @Suppress("UNCHECKED_CAST")
  internal val colorValues: Map<ThemeToken<Color>, Color>? = if (
    values.values.all { it is Color }
  ) {
    values as Map<ThemeToken<Color>, Color>
  } else {
    null
  }

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
    val builder = ThemeBuilderV2()
    builder.themeAction()
    builder.render(colorScheme = null, content = content)
  }
}

internal class ResolvedTheme(
  internal val name: String,
  internal val properties: Map<ThemeProperty<*>, ThemeValues<*>>,
)

internal class CompositionCallTracker {
  private var activeContentKey: CompositeKeyHashCode? = null

  @Composable
  fun once(content: @Composable () -> Unit) {
    val currentKey = currentCompositeKeyHashCode

    DisposableEffect(currentKey) {
      check(activeContentKey == null || activeContentKey == currentKey) {
        "You may call the content lambda of extend {} exactly once."
      }

      activeContentKey = currentKey

      onDispose {
        if (activeContentKey == currentKey) {
          activeContentKey = null
        }
      }
    }

    content()
  }
}

internal val LocalTheme =
  compositionLocalOf<ResolvedTheme> {
    error(
      "No theme was set. In order to use the Theme object you need to wrap " +
        "your content with a theme @Composable returned by the buildTheme {} function.",
    )
  }

internal val UnspecifiedTextSelectionColors = TextSelectionColors(
  handleColor = Color.Unspecified,
  backgroundColor = Color.Unspecified,
)

@DslMarker
annotation class ThemeBuilderMarker

@Deprecated(
  message = "This will be removed in 3.0. It is now up to you to implement " +
    "this behavior if it is a requirement for your design system.",
)
data class ComponentInteractiveSize(
  val nonTouchInteractionSize: Dp = Dp.Unspecified,
  val touchInteractionSize: Dp = Dp.Unspecified,
)

@Deprecated(
  message = "This will be removed in 3.0. It is now up to you to implement " +
    "this behavior if it is a requirement for your design system.",
)
fun ComponentInteractiveSize(size: Dp): ComponentInteractiveSize {
  return ComponentInteractiveSize(size, size)
}

@ThemeBuilderMarker
open class ThemeBuilder internal constructor() {
  var name: String = "Theme"

  var defaultIndication: Indication? by mutableStateOf(null)
  var defaultTextStyle: TextStyle by mutableStateOf(TextStyle.Default)
  var defaultContentColor: Color by mutableStateOf(Color.Unspecified)
  var defaultTextSelectionColors: TextSelectionColors? by mutableStateOf(null)

  @Deprecated(
    message = "This will be removed in 3.0. It is now up to you to implement this " +
      "behavior if it is a requirement for your design system.",
  )
  var defaultComponentInteractiveSize: ComponentInteractiveSize by mutableStateOf(
    ComponentInteractiveSize(Dp.Unspecified, Dp.Unspecified),
  )

  val properties = MutableThemeProperties()

  internal var extendedTheme: ThemeExtension? = null
    private set

  private var extensionKey: CompositeKeyHashCode? = null

  @Composable
  fun extend(extension: @Composable (@Composable () -> Unit) -> Unit) {
    val currentKey = currentCompositeKeyHashCode

    check(extensionKey == null || extensionKey == currentKey) {
      "Themes can only be extended exactly once. " +
        "Make sure you use the `extend {}` block within your buildTheme {} only once."
    }

    extensionKey = currentKey
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

/**
 * A named set of theme-property overrides.
 *
 * Use [Light] and [Dark] for system color schemes, or create a custom scheme such as
 * `val Sepia = ColorScheme("sepia")`.
 */
data class ColorScheme(val name: String) {
  init {
    require(name.isNotBlank()) { "A color scheme name cannot be blank." }
  }

  companion object {
    val Light = ColorScheme("light")
    val Dark = ColorScheme("dark")
  }
}

/** A composable theme returned by [buildThemeV2]. */
interface ThemeComposableV2 {
  /** Applies this theme using the platform's light or dark preference. */
  @Composable
  operator fun invoke(content: @Composable () -> Unit)

  /** Applies this theme with [colorScheme]'s overrides. */
  @Composable
  operator fun invoke(
    colorScheme: ColorScheme,
    content: @Composable () -> Unit,
  )
}

private val LocalThemeColorScheme = compositionLocalOf<ColorScheme?> { null }

fun buildThemeV2(themeAction: @Composable ThemeBuilderV2.() -> Unit = {}): ThemeComposableV2 {
  return ThemeV2(themeAction)
}

private class ThemeV2(
  private val themeAction: @Composable ThemeBuilderV2.() -> Unit,
) : ThemeComposableV2 {
  @Composable
  override fun invoke(content: @Composable () -> Unit) {
    render(content)
  }

  @Composable
  override fun invoke(
    colorScheme: ColorScheme,
    content: @Composable () -> Unit,
  ) {
    CompositionLocalProvider(LocalThemeColorScheme provides colorScheme) {
      render(content)
    }
  }

  @Composable
  private fun render(content: @Composable () -> Unit) {
    val colorScheme = LocalThemeColorScheme.current ?: run {
      if (isSystemInDarkTheme()) ColorScheme.Dark else ColorScheme.Light
    }
    val builder = ThemeBuilderV2()
    builder.themeAction()
    builder.render(colorScheme = colorScheme, content = content)
  }
}

@Composable
internal fun ThemeBuilderV2.render(
  colorScheme: ColorScheme?,
  content: @Composable () -> Unit,
) {
  val colorSchemeOverrides = colorScheme?.let(this.colorSchemeOverrides::get)
  val allProperties = colorSchemeOverrides?.properties?.entries?.takeIf { it.isNotEmpty() }
    ?.let { overrides ->
      properties.entries.toMutableMap().apply {
        overrides.forEach { (property, values) ->
          this[property] = this[property]?.mergeWith(values) ?: values
        }
      }
    }
    ?: properties.entries
  val colorSchemeTransitionSpec = this.colorSchemeTransitionSpec
  val finalProperties = if (colorSchemeTransitionSpec.isImmediate()) {
    allProperties
  } else {
    allProperties.animateColors(colorSchemeTransitionSpec)
  }

  val defaultIndication =
    colorSchemeOverrides?.defaultIndication ?: this.defaultIndication ?: NoIndication
  val defaultTextStyle = colorSchemeOverrides?.defaultTextStyle ?: this.defaultTextStyle
  val targetContentColor = colorSchemeOverrides?.defaultContentColor ?: this.defaultContentColor
  val defaultContentColor = if (colorSchemeTransitionSpec.isImmediate()) {
    targetContentColor
  } else {
    animateColorAsState(targetContentColor, colorSchemeTransitionSpec).value
  }
  val textSelectionColors = colorSchemeOverrides?.defaultTextSelectionColors
    ?: this.defaultTextSelectionColors
    ?: UnspecifiedTextSelectionColors
  val minInteractiveSize = colorSchemeOverrides?.defaultComponentInteractiveSize
    ?: this.defaultComponentInteractiveSize
  val finalInteractiveSize = ComponentInteractiveSize(
    touchInteractionSize = minInteractiveSize.touchInteractionSize,
    nonTouchInteractionSize = minInteractiveSize.nonTouchInteractionSize,
  )
  val theme = ResolvedTheme(this.name, finalProperties)

  CompositionLocalProvider(
    LocalTheme provides theme,
    LocalIndication provides defaultIndication,
    LocalTextStyle provides defaultTextStyle,
    LocalContentColor provides defaultContentColor,
    LocalTextSelectionColors provides textSelectionColors,
    LocalMinimumComponentInteractiveSize provides finalInteractiveSize,
  ) {
    val currentExtendedTheme = this.extendedTheme

    if (currentExtendedTheme == null) {
      content()
    } else {
      val tracker = remember { CompositionCallTracker() }

      currentExtendedTheme {
        tracker.once {
          content()
        }
      }
    }
  }
}

@ThemeBuilderMarker
class ThemeBuilderV2 internal constructor() : ThemeBuilder() {
  internal val colorSchemeOverrides = mutableStateMapOf<ColorScheme, ColorSchemedThemeBuilder>()

  /**
   * The animation used when resolved color values change, including when the selected
   * [ColorScheme] changes.
   *
   * Defaults to [snap], so color values change immediately. The spec applies to [ThemeProperty]
   * values whose tokens are all [Color]s, as well as [defaultContentColor].
   */
  var colorSchemeTransitionSpec: AnimationSpec<Color> by mutableStateOf(snap())

  /**
   * Defines property and default-value overrides for [colorScheme].
   *
   * Values set outside this block remain the fallback when this scheme does not override them.
   */
  @Composable
  fun colorScheme(
    colorScheme: ColorScheme,
    themeAction: @Composable ColorSchemedThemeBuilder.() -> Unit,
  ) {
    val builder = ColorSchemedThemeBuilder()
    builder.themeAction()
    colorSchemeOverrides[colorScheme] = builder
  }
}

@ThemeBuilderMarker
class ColorSchemedThemeBuilder internal constructor() {
  var defaultIndication: Indication? by mutableStateOf(null)
  var defaultTextStyle: TextStyle? by mutableStateOf(null)
  var defaultContentColor: Color? by mutableStateOf(null)
  var defaultTextSelectionColors: TextSelectionColors? by mutableStateOf(null)

  @Deprecated(
    message = "This will be removed in 3.0. It is now up to you to implement this " +
      "behavior if it is a requirement for your design system.",
  )
  var defaultComponentInteractiveSize: ComponentInteractiveSize? by mutableStateOf(null)

  val properties = MutableThemeProperties()
}

@Suppress("UNCHECKED_CAST")
private fun ThemeValues<*>.mergeWith(overrides: ThemeValues<*>): ThemeValues<*> {
  val defaultValues = this as ThemeValues<Any?>
  val overrideValues = overrides as ThemeValues<Any?>
  return ThemeValues(defaultValues.propertyName, defaultValues.values + overrideValues.values)
}

@Composable
private fun Map<ThemeProperty<*>, ThemeValues<*>>.animateColors(
  animationSpec: AnimationSpec<Color>,
): Map<ThemeProperty<*>, ThemeValues<*>> {
  val animatedProperties = mutableMapOf<ThemeProperty<*>, ThemeValues<*>>()

  for ((property, values) in this) {
    animatedProperties[property] = key(property) {
      values.animateColors(animationSpec)
    }
  }

  return animatedProperties
}

@Composable
private fun ThemeValues<*>.animateColors(animationSpec: AnimationSpec<Color>): ThemeValues<*> {
  val colorValues = colorValues ?: return this

  val animatedValues = mutableMapOf<ThemeToken<Color>, Color>()

  for ((token, color) in colorValues) {
    animatedValues[token] = key(token) {
      animateColorAsState(color, animationSpec).value
    }
  }

  return ThemeValues(propertyName, animatedValues)
}

private fun AnimationSpec<Color>.isImmediate(): Boolean {
  return this is SnapSpec && delay == 0
}
