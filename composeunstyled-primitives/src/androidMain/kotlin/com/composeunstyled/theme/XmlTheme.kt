package com.composeunstyled.theme

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Typeface
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.FontRes
import androidx.annotation.StyleableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.composeunstyled.primitives.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TYPEFACE_SANS = 1
private const val TYPEFACE_SERIF = 2
private const val TYPEFACE_MONOSPACE = 3

@Composable
fun resolveThemeColor(context: Context, @AttrRes resId: Int): Color {
    return remember(context, resId) {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(resId, typedValue, true)
        Color(typedValue.data)
    }
}

@Composable
fun resolveThemeDp(context: Context, @AttrRes resId: Int): Dp {
    val density = LocalDensity.current
    val resources = context.resources

    return remember(context, density, resId) {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(resId, typedValue, false)
        with(density) {
            typedValue.getDimension(resources.displayMetrics).toDp()
        }
    }
}

@Composable
fun resolveThemeSp(context: Context, @AttrRes resId: Int): TextUnit {
    val density = LocalDensity.current
    val resources = context.resources

    return remember(context, density, resId) {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(resId, typedValue, false)
        with(density) {
            typedValue.getDimension(resources.displayMetrics).toSp()
        }
    }
}

@Composable
fun resolveThemeInt(context: Context, @AttrRes resId: Int): Int {
    return remember(context, resId) {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(resId, typedValue, true)
        typedValue.data
    }
}

@Composable
fun resolveThemeFloat(context: Context, @AttrRes resId: Int): Float {
    return remember(context, resId) {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(resId, typedValue, true)
        typedValue.float
    }
}

@Composable
fun resolveThemePx(context: Context, @AttrRes resId: Int): Float {
    val resources = context.resources

    return remember(context, resId) {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(resId, typedValue, false)
        typedValue.getDimension(resources.displayMetrics)
    }
}

@Composable
fun resolveThemeString(context: Context, @AttrRes resId: Int): String {
    return remember(context, resId) {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(resId, typedValue, true)
        typedValue.string?.toString().orEmpty()
    }
}

@Composable
fun resolveThemeBoolean(context: Context, @AttrRes resId: Int): Boolean {
    return remember(context, resId) {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(resId, typedValue, true)
        typedValue.data != 0
    }
}

@Composable
@SuppressLint("ResourceType")
fun resolveThemeTextAppearance(context: Context, @AttrRes resId: Int): TextStyle {
    val density = LocalDensity.current

    var resolvedTextStyle by remember { mutableStateOf<TextStyle?>(null) }

    LaunchedEffect(context, density, resId) {
        val typedValue = TypedValue()
        if (context.theme.resolveAttribute(resId, typedValue, true).not()) {
            return@LaunchedEffect
        }
        val styleResId = typedValue.resourceId

        val a: TypedArray = context.theme.obtainStyledAttributes(styleResId, R.styleable.TextAppearance)
        val textSizePx = a.getDimension(R.styleable.TextAppearance_android_textSize, -1f)

        val textSize = if (textSizePx == -1f) {
            TextUnit.Unspecified
        } else {
            with(density) { textSizePx.toSp() }
        }
        val textColor = getColorStateList(context, a, R.styleable.TextAppearance_android_textColor)
        val textStyle = a.getInt(R.styleable.TextAppearance_android_textStyle, Typeface.NORMAL)
        val typeface = a.getInt(R.styleable.TextAppearance_android_typeface, TYPEFACE_SANS)

        val fontFamilyResourceId = a.getResourceId(R.styleable.TextAppearance_android_fontFamily, 0)
        val fontFamily = a.getString(R.styleable.TextAppearance_android_fontFamily)

        val shadowColor = getColorStateList(context, a, R.styleable.TextAppearance_android_shadowColor)
        val shadowDx = a.getFloat(R.styleable.TextAppearance_android_shadowDx, 0f)
        val shadowDy = a.getFloat(R.styleable.TextAppearance_android_shadowDy, 0f)
        val shadowRadius = a.getFloat(R.styleable.TextAppearance_android_shadowRadius, 0f)

        val fontWeight = resolveFontWeight(textStyle)
        val fontStyle = resolveFontStyle(textStyle)
        val composeFontFamily = resolveFontFamily(fontFamilyResourceId, context, fontFamily, textStyle, typeface)
        val shadow = resolveShadow(shadowRadius, shadowColor, shadowDx, shadowDy)

        val resolvedColor = if (textColor == null) {
            Color.Unspecified
        } else {
            Color(textColor.defaultColor)
        }

        a.recycle()

        resolvedTextStyle = TextStyle(
            fontSize = textSize,
            fontWeight = fontWeight,
            fontStyle = fontStyle,
            fontFamily = composeFontFamily,
            color = resolvedColor,
            shadow = shadow,
        )
    }

    return resolvedTextStyle ?: TextStyle.Default
}

private fun resolveShadow(
    shadowRadius: Float,
    shadowColor: ColorStateList?,
    shadowDx: Float,
    shadowDy: Float
): Shadow? {
    if (shadowRadius <= 0f || shadowColor == null) return null

    return Shadow(
        color = Color(shadowColor.defaultColor),
        offset = Offset(shadowDx, shadowDy),
        blurRadius = shadowRadius
    )
}

private suspend fun resolveFontFamily(
    @FontRes fontFamilyResourceId: Int,
    context: Context,
    fontFamily: String?,
    textStyle: Int,
    typeface: Int
): FontFamily = when {
    fontFamilyResourceId != 0 -> {
        val androidTypeface = withContext(Dispatchers.IO) {
            // Use Dispatchers.IO as ResourcesCompat.getFont can block the current thread
            ResourcesCompat.getFont(context, fontFamilyResourceId)
        }
        if (androidTypeface != null) {
            FontFamily(androidTypeface)
        } else {
            FontFamily.Default
        }
    }

    fontFamily != null -> {
        val androidTypeface = Typeface.create(fontFamily, textStyle)
        FontFamily(androidTypeface)
    }
    // Fall back to typeface enum
    else -> when (typeface) {
        TYPEFACE_SERIF -> FontFamily.Serif
        TYPEFACE_MONOSPACE -> FontFamily.Monospace
        else -> FontFamily.Default
    }
}

private fun resolveFontStyle(textStyle: Int): FontStyle = when {
    textStyle == Typeface.ITALIC -> FontStyle.Italic
    textStyle == Typeface.BOLD_ITALIC -> FontStyle.Italic
    textStyle and Typeface.ITALIC != 0 -> FontStyle.Italic
    else -> FontStyle.Normal
}

private fun resolveFontWeight(textStyle: Int): FontWeight = when {
    textStyle == Typeface.BOLD -> FontWeight.Bold
    textStyle == Typeface.BOLD_ITALIC -> FontWeight.Bold
    textStyle and Typeface.BOLD != 0 -> FontWeight.Bold
    else -> FontWeight.Normal
}

private fun getColorStateList(context: Context, attributes: TypedArray, @StyleableRes index: Int): ColorStateList? {
    if (attributes.hasValue(index)) {
        val resourceId = attributes.getResourceId(index, 0)
        if (resourceId != 0) {

            val value: ColorStateList? = ContextCompat.getColorStateList(context, resourceId)
            if (value != null) {
                return value
            }
        }
    }

    return attributes.getColorStateList(index)
}
