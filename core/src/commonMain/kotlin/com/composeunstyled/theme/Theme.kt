package com.composeunstyled.theme

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.composeunstyled.LocalContentColor
import com.composeunstyled.LocalTextStyle


object Theme {
    @Composable
    operator fun <T> get(property: ThemeProperty<T>): ThemeValues<T> {
        val theme = LocalTheme.current

        @Suppress("UNCHECKED_CAST")
        val resolvedValue = theme.properties[property] as? ThemeValues<T>

        return resolvedValue ?: error(
            "There is no ${property.name} property in the ${theme.name} theme. To fix this: 1. Create a design property: val ${property.name} = ThemeProperty<Type>(${property.name}), 2. Pass it to your theme definition: buildTheme { properties -> properties[${property.name}] = TODO(\"Populate token values\") }"
        )
    }
}

class ThemeValues<T> internal constructor(
    internal val propertyName: String,
    internal val values: Map<ThemeToken<T>, T>
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

typealias ComposableWithContent = @Composable (@Composable () -> Unit) -> Unit
typealias ThemeComposable = ComposableWithContent

data class ThemeProperty<T>(val name: String)
data class ThemeToken<T>(val name: String)

fun buildTheme(themeAction: @Composable ThemeBuilder.() -> Unit = {}): ThemeComposable {
    return { content ->
        val builder = ThemeBuilder().apply {
            themeAction()
        }

        val allProperties = builder.properties.entries

        val indication = builder.defaultIndication ?: LocalIndication.current
        val textSelectionColors = builder.defaultTextSelectionColors ?: LocalTextSelectionColors.current

        val theme = ResolvedTheme(builder.name, allProperties)

        CompositionLocalProvider(
            LocalTheme provides theme,
            LocalIndication provides indication,
            LocalTextStyle provides builder.defaultTextStyle,
            LocalContentColor provides builder.defaultContentColor,
            LocalTextSelectionColors provides textSelectionColors
        ) {
            content()
        }
    }
}

internal class ResolvedTheme(
    internal val name: String = "ComposeTheme",
    internal val properties: Map<ThemeProperty<*>, ThemeValues<*>> = emptyMap(),
)

internal val LocalTheme =
    staticCompositionLocalOf<ResolvedTheme> { error("No theme was set. In order to use the Theme object you need to wrap your content with a theme @Composable returned by the buildTheme {} function.") }

@DslMarker
annotation class ThemeBuilderMarker

@ThemeBuilderMarker
class ThemeBuilder internal constructor() {
    var name: String = "Theme"

    var defaultIndication: Indication? by mutableStateOf(null)

    var defaultTextStyle: TextStyle by mutableStateOf(TextStyle.Default)

    var defaultContentColor: Color by mutableStateOf(Color.Unspecified)

    var defaultTextSelectionColors: TextSelectionColors? by mutableStateOf(null)

    val properties = MutableThemeProperties()
}

class MutableThemeProperties internal constructor() {
    internal val entries = mutableStateMapOf<ThemeProperty<*>, ThemeValues<*>>()

    operator fun <T> set(property: ThemeProperty<T>, values: Map<ThemeToken<T>, T>) {
        entries[property] = ThemeValues(property.name, values)
    }
}

data class OverriddenValue<T>(val token: ThemeToken<T>, val value: T)

infix fun <T> ThemeToken<T>.provides(value: T): OverriddenValue<T> {
    return OverriddenValue(this, value)
}

@Composable
fun ThemeOverride(
    vararg overriddenValues: OverriddenValue<*>,
    content: @Composable () -> Unit
) {
    val currentTheme = LocalTheme.current
    val updatedProperties = currentTheme.properties.toMutableMap()
    overriddenValues.forEach { overriddenValue ->
        val propertyEntry = updatedProperties.entries.find { (_, themeValues) ->
            themeValues.values.containsKey(overriddenValue.token)
        }
        
        if (propertyEntry != null) {
            val (property, themeValues) = propertyEntry
            @Suppress("UNCHECKED_CAST")
            val updatedThemeValues = (themeValues as ThemeValues<Any>).copyWithUpdatedValue(
                overriddenValue.token as ThemeToken<Any>,
                overriddenValue.value as Any
            )
            updatedProperties[property] = updatedThemeValues
        }
    }
    
    val updatedTheme = ResolvedTheme(currentTheme.name, updatedProperties)

    CompositionLocalProvider(LocalTheme provides updatedTheme) {
        content()
    }
}