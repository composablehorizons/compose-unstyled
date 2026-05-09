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
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@file:Suppress("unused", "UNUSED_PARAMETER")

package com.composeunstyled.demo.material3api

import androidx.annotation.IntRange
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.BadgeDefaults
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuBoxScope
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButtonColors
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.RangeSliderState
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.SelectableChipElevation
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TabIndicatorScope
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldLabelScope
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.composeunstyled.AnchorAlignment
import com.composeunstyled.AnchorSide
import com.composeunstyled.CheckedIndicator
import com.composeunstyled.DialogPanel
import com.composeunstyled.DropdownMenuPanel
import com.composeunstyled.ModalBottomSheetState
import com.composeunstyled.ModalSheetProperties
import com.composeunstyled.RadioButton
import com.composeunstyled.Scrim
import com.composeunstyled.Sheet
import com.composeunstyled.SheetDetent
import com.composeunstyled.StateIndicator
import com.composeunstyled.TabKey
import com.composeunstyled.TabList
import com.composeunstyled.TabListScope
import com.composeunstyled.TextInput
import com.composeunstyled.TooltipPanel
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledCheckbox
import com.composeunstyled.UnstyledDialog
import com.composeunstyled.UnstyledDropdownMenu
import com.composeunstyled.UnstyledHorizontalSeparator
import com.composeunstyled.UnstyledIcon
import com.composeunstyled.UnstyledModalBottomSheet
import com.composeunstyled.UnstyledProgress
import com.composeunstyled.UnstyledRadioGroup
import com.composeunstyled.UnstyledSlider
import com.composeunstyled.UnstyledSwitch
import com.composeunstyled.UnstyledTabGroup
import com.composeunstyled.UnstyledTextField
import com.composeunstyled.UnstyledTooltip
import com.composeunstyled.UnstyledTriStateCheckbox
import com.composeunstyled.UnstyledVerticalSeparator
import com.composeunstyled.buildModifier
import com.composeunstyled.currentWindowContainerSize
import com.composeunstyled.rememberModalBottomSheetState
import kotlinx.coroutines.launch
import kotlin.math.max
import androidx.compose.material3.Surface as M3Surface
import com.composeunstyled.Tab as UnstyledTab

private val IconButtonSize = 40.dp
private val CheckboxSize = 18.dp
private val CheckboxCornerRadius = 2.dp
private val CheckboxStrokeWidth = 2.dp
private val MaterialDefaultSpatialAnimationSpec = spring<Float>(
  dampingRatio = 0.9f,
  stiffness = 700f,
)
private const val CheckboxSnapAnimationDelay = 100
private val RadioButtonPadding = 2.dp
private val RadioButtonSize = 20.dp
private val RadioButtonDotSize = 12.dp
private val RadioButtonStrokeWidth = 2.dp
private val SwitchWidth = 52.dp
private val SwitchHeight = 32.dp
private val SwitchThumbSize = 24.dp
private val SwitchUncheckedThumbSize = 16.dp
private val SwitchPressedThumbSize = 28.dp
private val SwitchStateLayerSize = 40.dp
private val SwitchTrackOutlineWidth = 2.dp
private val SliderHeight = 44.dp
private val SliderTrackHeight = 16.dp
private val SliderTrackCornerSize = 8.dp
private val SliderTrackInsideCornerSize = 2.dp
private val SliderThumbWidth = 4.dp
private val SliderThumbHeight = 44.dp
private val SliderThumbTrackGap = 6.dp
private val SliderStopIndicatorSize = 4.dp
private val LinearProgressHeight = 4.dp
private val LinearProgressWidth = 240.dp
private val CircularProgressSize = 40.dp
private val PrimaryTabHeight = 48.dp
private val PrimaryTabIndicatorHeight = 3.dp
private val SecondaryTabIndicatorHeight = 3.dp
private val TabDividerHeight = 1.dp
private val TabHorizontalPadding = 16.dp
private val TabIconSize = 24.dp
private val TabIconTextPadding = 8.dp
private val TextFieldContainerHeight = 56.dp
private val TextFieldHorizontalPadding = 16.dp
private val TextFieldIconHorizontalPadding = 12.dp
private val TextFieldIconSize = 24.dp
private val TextFieldLabelCollapsedTopPadding = 8.dp
private val TextFieldLabelExpandedTopPadding = 18.dp
private val TextFieldLabelScale = 0.75f
private val TextFieldFocusedIndicatorWidth = 2.dp
private val TextFieldUnfocusedIndicatorWidth = 1.dp
private val OutlinedTextFieldLabelCutoutPadding = 4.dp
private val TextFieldLabelAnimationSpec = tween<Float>(
  durationMillis = 150,
  easing = CubicBezierEasing(0.2f, 0f, 0f, 1f),
)
private val BadgeMinSize = 16.dp
private val ButtonMinWidth = 58.dp
private val ButtonMinHeight = 40.dp
private val FloatingActionButtonSize = 56.dp
private val SmallFloatingActionButtonSize = 40.dp
private val LargeFloatingActionButtonSize = 96.dp
private val FloatingActionButtonShadowElevation = 6.dp
private val NavigationItemIndicatorHeight = 32.dp
private const val DropdownMenuEnterDurationMillis = 120
private const val DropdownMenuExitDurationMillis = 75
private const val DropdownMenuFadeInDurationMillis = 30
private const val DropdownMenuInitialScale = 0.8f
private const val DropdownMenuTargetScale = 0.95f
private val DropdownMenuTransformOrigin = TransformOrigin(0.5f, 0f)
private val DialogMinWidth = 280.dp
private val DialogMaxWidth = 560.dp
private val DialogPadding = PaddingValues(24.dp)
private val DialogIconPadding = PaddingValues(bottom = 16.dp)
private val DialogTitlePadding = PaddingValues(bottom = 16.dp)
private val DialogTextPadding = PaddingValues(bottom = 24.dp)
private const val DialogEnterDurationMillis = 150
private const val DialogExitDurationMillis = 75
private const val DialogFadeInDurationMillis = 50
private val ModalBottomSheetTopMargin = 72.dp
private val ModalBottomSheetWideTopMargin = 56.dp
private val ModalBottomSheetWideSideMargin = 56.dp
private const val ModalBottomSheetAnimationDurationMillis = 300

private enum class TextFieldVariant {
  Filled,
  Outlined,
}

private fun Modifier.outlinedTextFieldCutout(
  labelSize: Size,
  labelStartPadding: Dp,
): Modifier = drawWithContent {
  if (labelSize.width > 0f) {
    val cutoutPadding = OutlinedTextFieldLabelCutoutPadding.toPx()
    val leftPadding = when (layoutDirection) {
      LayoutDirection.Ltr -> labelStartPadding.toPx()
      LayoutDirection.Rtl -> size.width - labelStartPadding.toPx() - labelSize.width
    }
    val left = (leftPadding - cutoutPadding).coerceAtLeast(0f)
    val right = (leftPadding + labelSize.width + cutoutPadding).coerceAtMost(size.width)
    val cutoutHeight = labelSize.height

    clipRect(left, -cutoutHeight / 2f, right, cutoutHeight / 2f, ClipOp.Difference) {
      this@drawWithContent.drawContent()
    }
  } else {
    drawContent()
  }
}

private val DropdownMenuEnterTransition = fadeIn(
  animationSpec = tween(DropdownMenuFadeInDurationMillis),
) + scaleIn(
  initialScale = DropdownMenuInitialScale,
  transformOrigin = DropdownMenuTransformOrigin,
  animationSpec = tween(DropdownMenuEnterDurationMillis),
)

private val DropdownMenuExitTransition = fadeOut(
  animationSpec = tween(DropdownMenuExitDurationMillis),
) + scaleOut(
  targetScale = DropdownMenuTargetScale,
  transformOrigin = DropdownMenuTransformOrigin,
  animationSpec = tween(DropdownMenuExitDurationMillis),
)

private fun ButtonColors.containerColorFor(enabled: Boolean): Color =
  if (enabled) containerColor else disabledContainerColor

private fun ButtonColors.contentColorFor(enabled: Boolean): Color =
  if (enabled) contentColor else disabledContentColor

private fun IconButtonColors.containerColorFor(enabled: Boolean): Color =
  if (enabled) containerColor else disabledContainerColor

private fun IconButtonColors.contentColorFor(enabled: Boolean): Color =
  if (enabled) contentColor else disabledContentColor

private fun IconToggleButtonColors.containerColorFor(enabled: Boolean, checked: Boolean): Color =
  when {
    enabled && checked -> checkedContainerColor
    enabled && !checked -> containerColor
    else -> disabledContainerColor
  }

private fun IconToggleButtonColors.contentColorFor(enabled: Boolean, checked: Boolean): Color =
  when {
    enabled && checked -> checkedContentColor
    enabled && !checked -> contentColor
    else -> disabledContentColor
  }

private fun SwitchColors.trackColorFor(enabled: Boolean, checked: Boolean): Color =
  when {
    enabled && checked -> checkedTrackColor
    enabled && !checked -> uncheckedTrackColor
    !enabled && checked -> disabledCheckedTrackColor
    else -> disabledUncheckedTrackColor
  }

private fun SwitchColors.thumbColorFor(enabled: Boolean, checked: Boolean): Color =
  when {
    enabled && checked -> checkedThumbColor
    enabled && !checked -> uncheckedThumbColor
    !enabled && checked -> disabledCheckedThumbColor
    else -> disabledUncheckedThumbColor
  }

private fun SwitchColors.borderColorFor(enabled: Boolean, checked: Boolean): Color =
  when {
    enabled && checked -> checkedBorderColor
    enabled && !checked -> uncheckedBorderColor
    !enabled && checked -> disabledCheckedBorderColor
    else -> disabledUncheckedBorderColor
  }

private fun SwitchColors.iconColorFor(enabled: Boolean, checked: Boolean): Color =
  when {
    enabled && checked -> checkedIconColor
    enabled && !checked -> uncheckedIconColor
    !enabled && checked -> disabledCheckedIconColor
    else -> disabledUncheckedIconColor
  }

private fun SliderColors.thumbColorFor(enabled: Boolean): Color =
  if (enabled) thumbColor else disabledThumbColor

private fun SliderColors.trackColorFor(enabled: Boolean, active: Boolean): Color =
  when {
    enabled && active -> activeTrackColor
    enabled && !active -> inactiveTrackColor
    !enabled && active -> disabledActiveTrackColor
    else -> disabledInactiveTrackColor
  }

private fun SliderColors.tickColorFor(enabled: Boolean, active: Boolean): Color =
  when {
    enabled && active -> activeTickColor
    enabled && !active -> inactiveTickColor
    !enabled && active -> disabledActiveTickColor
    else -> disabledInactiveTickColor
  }

private fun CardColors.containerColorFor(enabled: Boolean): Color =
  if (enabled) containerColor else disabledContainerColor

private fun CardColors.contentColorFor(enabled: Boolean): Color =
  if (enabled) contentColor else disabledContentColor

private fun Modifier.minimumInteractiveComponentSize(enabled: Boolean): Modifier =
  if (enabled) {
    minimumInteractiveComponentSize()
  } else {
    this
  }

@Composable
private fun ButtonSurface(
  onClick: () -> Unit,
  modifier: Modifier,
  enabled: Boolean,
  shape: Shape,
  backgroundColor: Color,
  contentColor: Color,
  border: BorderStroke?,
  contentPadding: PaddingValues,
  interactionSource: MutableInteractionSource?,
  shadowElevation: Dp = 0.dp,
  minWidth: Dp = ButtonMinWidth,
  minHeight: Dp = ButtonMinHeight,
  content: @Composable RowScope.() -> Unit,
) {
  val resolvedModifier = modifier.shadow(shadowElevation, shape)
  CompositionLocalProvider(LocalContentColor provides contentColor) {
    ProvideTextStyle(MaterialTheme.typography.labelLarge) {
      UnstyledButton(
        onClick = onClick,
        enabled = enabled,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        modifier = (
          resolvedModifier
            .defaultMinSize(minWidth = minWidth, minHeight = minHeight)
            .clip(shape)
            .background(backgroundColor, shape)
          ) then buildModifier {
          if (border != null) {
            add(Modifier.border(border, shape))
          }
        },
      ) {
        Row(content = content)
      }
    }
  }
}

@Composable
private fun ContainerSurface(
  modifier: Modifier,
  shape: Shape,
  color: Color,
  contentColor: Color,
  border: BorderStroke?,
  content: @Composable () -> Unit,
) {
  CompositionLocalProvider(LocalContentColor provides contentColor) {
    Box(
      modifier = (
        modifier
          .clip(shape)
          .background(color, shape)
        ) then buildModifier {
        if (border != null) {
          add(Modifier.border(border, shape))
        }
      },
    ) {
      content()
    }
  }
}

@Composable
private fun CardSurface(
  modifier: Modifier,
  shape: Shape,
  containerColor: Color,
  contentColor: Color,
  border: BorderStroke?,
  onClick: (() -> Unit)? = null,
  enabled: Boolean = true,
  interactionSource: MutableInteractionSource? = null,
  shadowElevation: Dp = 0.dp,
  content: @Composable ColumnScope.() -> Unit,
) {
  val cardInteractionSource = if (onClick != null) {
    interactionSource ?: remember { MutableInteractionSource() }
  } else {
    null
  }
  CompositionLocalProvider(LocalContentColor provides contentColor) {
    Box(
      modifier = (
        modifier
          .shadow(shadowElevation, shape)
          .clip(shape)
          .background(containerColor, shape)
        ) then buildModifier {
        if (border != null) {
          add(Modifier.border(border, shape))
        }
        if (onClick != null) {
          add(
            Modifier.clickable(
              enabled = enabled,
              interactionSource = cardInteractionSource,
              indication = ripple(),
              onClick = onClick,
            ),
          )
        }
      },
    ) {
      Column(content = content)
    }
  }
}

@Composable
private fun PlainSliderTrack(
  progress: Float,
  steps: Int,
  enabled: Boolean,
  thumbActive: Boolean,
  colors: SliderColors,
) {
  val activeColor = colors.trackColorFor(enabled, active = true)
  val inactiveColor = colors.trackColorFor(enabled, active = false)
  val activeTickColor = colors.tickColorFor(enabled, active = true)
  val inactiveTickColor = colors.tickColorFor(enabled, active = false)
  Canvas(modifier = Modifier.fillMaxWidth().height(SliderTrackHeight)) {
    val fraction = progress.coerceIn(0f, 1f)
    val trackWidth = size.width
    val trackHeight = size.height
    val cornerRadius = SliderTrackCornerSize.toPx()
    val insideCornerRadius = SliderTrackInsideCornerSize.toPx()
    val thumbWidth = if (thumbActive) {
      SliderThumbWidth.toPx() / 2f
    } else {
      SliderThumbWidth.toPx()
    }
    val thumbGap = thumbWidth / 2f + SliderThumbTrackGap.toPx()
    val thumbCenter = thumbWidth / 2f + (trackWidth - thumbWidth) * fraction
    val activeEnd = (thumbCenter - thumbGap).coerceAtLeast(0f)
    val inactiveStart = (thumbCenter + thumbGap).coerceAtMost(trackWidth)

    fun drawTrackSegment(
      color: Color,
      start: Float,
      end: Float,
      startRadius: Float,
      endRadius: Float,
    ) {
      if (end <= start) return

      val width = end - start
      val radiusScale = (width / (startRadius + endRadius)).coerceAtMost(1f)
      val resolvedStartRadius = startRadius * radiusScale
      val resolvedEndRadius = endRadius * radiusScale
      val path = Path().apply {
        moveTo(start + resolvedStartRadius, 0f)
        lineTo(end - resolvedEndRadius, 0f)
        quadraticTo(end, 0f, end, resolvedEndRadius)
        lineTo(end, trackHeight - resolvedEndRadius)
        quadraticTo(end, trackHeight, end - resolvedEndRadius, trackHeight)
        lineTo(start + resolvedStartRadius, trackHeight)
        quadraticTo(start, trackHeight, start, trackHeight - resolvedStartRadius)
        lineTo(start, resolvedStartRadius)
        quadraticTo(start, 0f, start + resolvedStartRadius, 0f)
        close()
      }
      drawPath(path = path, color = color)
    }

    if (activeEnd > 0f) {
      drawTrackSegment(
        color = activeColor,
        start = 0f,
        end = activeEnd,
        startRadius = cornerRadius,
        endRadius = insideCornerRadius,
      )
    }

    if (inactiveStart < trackWidth) {
      drawTrackSegment(
        color = inactiveColor,
        start = inactiveStart,
        end = trackWidth,
        startRadius = insideCornerRadius,
        endRadius = cornerRadius,
      )

      val stopRadius = SliderStopIndicatorSize.toPx() / 2f
      drawCircle(
        color = activeColor,
        radius = stopRadius,
        center = Offset(trackWidth - cornerRadius, center.y),
      )
    }

    if (steps > 0) {
      val stopRadius = SliderStopIndicatorSize.toPx() / 2f
      for (step in 1..steps) {
        val tickFraction = step.toFloat() / (steps + 1)
        val tickX = lerp(cornerRadius, trackWidth - cornerRadius, tickFraction)
        if (tickX !in (thumbCenter - thumbGap)..(thumbCenter + thumbGap)) {
          drawCircle(
            color = if (tickFraction <= fraction) activeTickColor else inactiveTickColor,
            radius = stopRadius,
            center = Offset(tickX, center.y),
          )
        }
      }
    }
  }
}

@Composable
private fun rememberSliderThumbActive(interactionSource: MutableInteractionSource): Boolean {
  val interactions = remember { mutableStateListOf<Interaction>() }
  LaunchedEffect(interactionSource) {
    interactionSource.interactions.collect { interaction ->
      when (interaction) {
        is PressInteraction.Press -> interactions.add(interaction)
        is PressInteraction.Release -> interactions.remove(interaction.press)
        is PressInteraction.Cancel -> interactions.remove(interaction.press)
        is DragInteraction.Start -> interactions.add(interaction)
        is DragInteraction.Stop -> interactions.remove(interaction.start)
        is DragInteraction.Cancel -> interactions.remove(interaction.start)
      }
    }
  }
  return interactions.isNotEmpty()
}

@Composable
private fun PlainSliderThumb(color: Color, enabled: Boolean, thumbActive: Boolean) {
  Box(
    modifier = (
      Modifier
        .width(if (thumbActive) SliderThumbWidth / 2 else SliderThumbWidth)
        .height(SliderThumbHeight)
      ) then buildModifier {
      if (enabled) {
        add(Modifier.pointerHoverIcon(PointerIcon.Hand))
      }
      add(Modifier.background(color, MaterialTheme.shapes.extraLarge))
    },
  )
}

@Composable
fun Button(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  shape: Shape = ButtonDefaults.shape,
  colors: ButtonColors = ButtonDefaults.buttonColors(),
  elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
  border: BorderStroke? = null,
  contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
  interactionSource: MutableInteractionSource? = null,
  content: @Composable RowScope.() -> Unit,
) {
  ButtonSurface(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    backgroundColor = colors.containerColorFor(enabled),
    contentColor = colors.contentColorFor(enabled),
    border = border,
    contentPadding = contentPadding,
    interactionSource = interactionSource,
    content = content,
  )
}

@Composable
fun ElevatedButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  shape: Shape = ButtonDefaults.elevatedShape,
  colors: ButtonColors = ButtonDefaults.elevatedButtonColors(),
  elevation: ButtonElevation? = ButtonDefaults.elevatedButtonElevation(),
  border: BorderStroke? = null,
  contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
  interactionSource: MutableInteractionSource? = null,
  content: @Composable RowScope.() -> Unit,
) {
  ButtonSurface(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    backgroundColor = colors.containerColorFor(enabled),
    contentColor = colors.contentColorFor(enabled),
    border = border,
    contentPadding = contentPadding,
    interactionSource = interactionSource,
    shadowElevation = if (enabled && elevation != null) 1.dp else 0.dp,
    content = content,
  )
}

@Composable
fun FilledTonalButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  shape: Shape = ButtonDefaults.filledTonalShape,
  colors: ButtonColors = ButtonDefaults.filledTonalButtonColors(),
  elevation: ButtonElevation? = ButtonDefaults.filledTonalButtonElevation(),
  border: BorderStroke? = null,
  contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
  interactionSource: MutableInteractionSource? = null,
  content: @Composable RowScope.() -> Unit,
) {
  ButtonSurface(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    backgroundColor = colors.containerColorFor(enabled),
    contentColor = colors.contentColorFor(enabled),
    border = border,
    contentPadding = contentPadding,
    interactionSource = interactionSource,
    content = content,
  )
}

@Composable
fun OutlinedButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  shape: Shape = ButtonDefaults.outlinedShape,
  colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
  elevation: ButtonElevation? = null,
  border: BorderStroke? = ButtonDefaults.outlinedButtonBorder(enabled),
  contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
  interactionSource: MutableInteractionSource? = null,
  content: @Composable RowScope.() -> Unit,
) {
  ButtonSurface(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    backgroundColor = colors.containerColorFor(enabled),
    contentColor = colors.contentColorFor(enabled),
    border = border,
    contentPadding = contentPadding,
    interactionSource = interactionSource,
    content = content,
  )
}

@Composable
fun TextButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  shape: Shape = ButtonDefaults.textShape,
  colors: ButtonColors = ButtonDefaults.textButtonColors(),
  elevation: ButtonElevation? = null,
  border: BorderStroke? = null,
  contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
  interactionSource: MutableInteractionSource? = null,
  content: @Composable RowScope.() -> Unit,
) {
  ButtonSurface(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    backgroundColor = colors.containerColorFor(enabled),
    contentColor = colors.contentColorFor(enabled),
    border = border,
    contentPadding = contentPadding,
    interactionSource = interactionSource,
    content = content,
  )
}

@Composable
fun Card(
  modifier: Modifier = Modifier,
  shape: Shape = CardDefaults.shape,
  colors: CardColors = CardDefaults.cardColors(),
  elevation: CardElevation = CardDefaults.cardElevation(),
  border: BorderStroke? = null,
  content: @Composable ColumnScope.() -> Unit,
) {
  CardSurface(
    modifier = modifier,
    shape = shape,
    containerColor = colors.containerColorFor(enabled = true),
    contentColor = colors.contentColorFor(enabled = true),
    border = border,
    content = content,
  )
}

@Composable
fun Card(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  shape: Shape = CardDefaults.shape,
  colors: CardColors = CardDefaults.cardColors(),
  elevation: CardElevation = CardDefaults.cardElevation(),
  border: BorderStroke? = null,
  interactionSource: MutableInteractionSource? = null,
  content: @Composable ColumnScope.() -> Unit,
) {
  CardSurface(
    modifier = modifier,
    shape = shape,
    containerColor = colors.containerColorFor(enabled),
    contentColor = colors.contentColorFor(enabled),
    border = border,
    onClick = onClick,
    enabled = enabled,
    interactionSource = interactionSource,
    content = content,
  )
}

@Composable
fun ElevatedCard(
  modifier: Modifier = Modifier,
  shape: Shape = CardDefaults.elevatedShape,
  colors: CardColors = CardDefaults.elevatedCardColors(),
  elevation: CardElevation = CardDefaults.elevatedCardElevation(),
  content: @Composable ColumnScope.() -> Unit,
) {
  CardSurface(
    modifier = modifier,
    shape = shape,
    containerColor = colors.containerColorFor(enabled = true),
    contentColor = colors.contentColorFor(enabled = true),
    border = null,
    shadowElevation = 1.dp,
    content = content,
  )
}

@Composable
fun ElevatedCard(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  shape: Shape = CardDefaults.elevatedShape,
  colors: CardColors = CardDefaults.elevatedCardColors(),
  elevation: CardElevation = CardDefaults.elevatedCardElevation(),
  interactionSource: MutableInteractionSource? = null,
  content: @Composable ColumnScope.() -> Unit,
) {
  CardSurface(
    modifier = modifier,
    shape = shape,
    containerColor = colors.containerColorFor(enabled),
    contentColor = colors.contentColorFor(enabled),
    border = null,
    onClick = onClick,
    enabled = enabled,
    interactionSource = interactionSource,
    shadowElevation = if (enabled) 1.dp else 0.dp,
    content = content,
  )
}

@Composable
fun OutlinedCard(
  modifier: Modifier = Modifier,
  shape: Shape = CardDefaults.outlinedShape,
  colors: CardColors = CardDefaults.outlinedCardColors(),
  elevation: CardElevation = CardDefaults.outlinedCardElevation(),
  border: BorderStroke = CardDefaults.outlinedCardBorder(),
  content: @Composable ColumnScope.() -> Unit,
) {
  CardSurface(
    modifier = modifier,
    shape = shape,
    containerColor = colors.containerColorFor(enabled = true),
    contentColor = colors.contentColorFor(enabled = true),
    border = border,
    content = content,
  )
}

@Composable
fun OutlinedCard(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  shape: Shape = CardDefaults.outlinedShape,
  colors: CardColors = CardDefaults.outlinedCardColors(),
  elevation: CardElevation = CardDefaults.outlinedCardElevation(),
  border: BorderStroke = CardDefaults.outlinedCardBorder(enabled),
  interactionSource: MutableInteractionSource? = null,
  content: @Composable ColumnScope.() -> Unit,
) {
  CardSurface(
    modifier = modifier,
    shape = shape,
    containerColor = colors.containerColorFor(enabled),
    contentColor = colors.contentColorFor(enabled),
    border = border,
    onClick = onClick,
    enabled = enabled,
    interactionSource = interactionSource,
    content = content,
  )
}

@Composable
fun Checkbox(
  checked: Boolean,
  onCheckedChange: ((Boolean) -> Unit)?,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  colors: CheckboxColors = CheckboxDefaults.colors(),
  interactionSource: MutableInteractionSource? = null,
) {
  val boxColor by animateColorAsState(
    targetValue = checkboxBoxColor(colors, enabled, ToggleableState(checked)),
    animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
  )
  val borderColor by animateColorAsState(
    targetValue = checkboxBorderColor(colors, enabled, ToggleableState(checked)),
    animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
  )
  val checkColor by animateColorAsState(
    targetValue = checkboxCheckmarkColor(colors, enabled, ToggleableState(checked)),
    animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
  )
  UnstyledCheckbox(
    checked = checked,
    modifier = modifier.minimumInteractiveComponentSize(onCheckedChange != null),
    enabled = enabled && onCheckedChange != null,
    onCheckedChange = onCheckedChange ?: {},
    interactionSource = interactionSource,
    indication = ripple(bounded = false, radius = 20.dp),
  ) {
    CheckedIndicator(
      modifier = Modifier
        .size(CheckboxSize)
        .drawBehind {
          drawMaterialCheckboxBox(
            boxColor = boxColor,
            borderColor = borderColor,
            radius = CheckboxCornerRadius.toPx(),
            strokeWidth = CheckboxStrokeWidth.toPx(),
          )
        },
    ) {
      val checkDrawFraction = transition.animateFloat(
        transitionSpec = {
          if (targetState == EnterExitState.PostExit) {
            snap(delayMillis = CheckboxSnapAnimationDelay)
          } else {
            MaterialDefaultSpatialAnimationSpec
          }
        },
      ) { state ->
        if (state == EnterExitState.Visible) 1f else 0f
      }
      val checkPath = remember { Path() }
      val pathToDraw = remember { Path() }
      val pathMeasure = remember { PathMeasure() }
      Canvas(Modifier.fillMaxSize()) {
        drawMaterialCheck(
          checkColor = checkColor,
          checkFraction = checkDrawFraction.value,
          crossCenterGravitation = 0f,
          checkPath = checkPath,
          pathToDraw = pathToDraw,
          pathMeasure = pathMeasure,
        )
      }
    }
  }
}

private fun DrawScope.drawMaterialCheckboxBox(
  boxColor: Color,
  borderColor: Color,
  radius: Float,
  strokeWidth: Float,
) {
  val halfStrokeWidth = strokeWidth / 2f
  val checkboxSize = size.width
  if (boxColor == borderColor) {
    drawRoundRect(
      color = boxColor,
      size = Size(checkboxSize, checkboxSize),
      cornerRadius = CornerRadius(radius),
      style = Fill,
    )
  } else {
    drawRoundRect(
      color = boxColor,
      topLeft = Offset(strokeWidth, strokeWidth),
      size = Size(checkboxSize - strokeWidth * 2, checkboxSize - strokeWidth * 2),
      cornerRadius = CornerRadius(max(0f, radius - strokeWidth)),
      style = Fill,
    )
    drawRoundRect(
      color = borderColor,
      topLeft = Offset(halfStrokeWidth, halfStrokeWidth),
      size = Size(checkboxSize - strokeWidth, checkboxSize - strokeWidth),
      cornerRadius = CornerRadius(radius - halfStrokeWidth),
      style = Stroke(width = strokeWidth),
    )
  }
}

private fun DrawScope.drawMaterialCheck(
  checkColor: Color,
  checkFraction: Float,
  crossCenterGravitation: Float,
  checkPath: Path,
  pathToDraw: Path,
  pathMeasure: PathMeasure,
) {
  val width = size.width
  val checkCrossX = 0.4f
  val checkCrossY = 0.7f
  val leftX = 0.2f
  val leftY = 0.5f
  val rightX = 0.8f
  val rightY = 0.3f
  val gravitatedCrossX = lerp(checkCrossX, 0.5f, crossCenterGravitation)
  val gravitatedCrossY = lerp(checkCrossY, 0.5f, crossCenterGravitation)
  val gravitatedLeftY = lerp(leftY, 0.5f, crossCenterGravitation)
  val gravitatedRightY = lerp(rightY, 0.5f, crossCenterGravitation)

  checkPath.rewind()
  checkPath.moveTo(width * leftX, width * gravitatedLeftY)
  checkPath.lineTo(width * gravitatedCrossX, width * gravitatedCrossY)
  checkPath.lineTo(width * rightX, width * gravitatedRightY)

  pathMeasure.setPath(checkPath, false)
  pathToDraw.rewind()
  pathMeasure.getSegment(0f, pathMeasure.length * checkFraction, pathToDraw, true)
  drawPath(
    path = pathToDraw,
    color = checkColor,
    style = Stroke(
      width = CheckboxStrokeWidth.toPx(),
      cap = StrokeCap.Square,
    ),
  )
}

@Composable
fun TriStateCheckbox(
  state: ToggleableState,
  onClick: (() -> Unit)?,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  colors: CheckboxColors = CheckboxDefaults.colors(),
  interactionSource: MutableInteractionSource? = null,
) {
  val boxColor by animateColorAsState(
    targetValue = checkboxBoxColor(colors, enabled, state),
    animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
  )
  val borderColor by animateColorAsState(
    targetValue = checkboxBorderColor(colors, enabled, state),
    animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
  )
  val checkColor by animateColorAsState(
    targetValue = checkboxCheckmarkColor(colors, enabled, state),
    animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
  )
  UnstyledTriStateCheckbox(
    value = state,
    modifier = modifier.minimumInteractiveComponentSize(onClick != null),
    onClick = { onClick?.invoke() },
    enabled = enabled && onClick != null,
    interactionSource = interactionSource,
    indication = ripple(bounded = false, radius = 20.dp),
  ) {
    StateIndicator(
      modifier = Modifier
        .size(CheckboxSize)
        .drawBehind {
          drawMaterialCheckboxBox(
            boxColor = boxColor,
            borderColor = borderColor,
            radius = CheckboxCornerRadius.toPx(),
            strokeWidth = CheckboxStrokeWidth.toPx(),
          )
        },
    ) { state ->
      val transition = updateTransition(state)
      val checkDrawFraction by transition.animateFloat(
        transitionSpec = {
          when {
            initialState == ToggleableState.Off -> MaterialDefaultSpatialAnimationSpec
            targetState == ToggleableState.Off -> snap(delayMillis = CheckboxSnapAnimationDelay)
            else -> MaterialDefaultSpatialAnimationSpec
          }
        },
      ) {
        when (it) {
          ToggleableState.On -> 1f
          ToggleableState.Off -> 0f
          ToggleableState.Indeterminate -> 1f
        }
      }
      val checkCenterGravitationShiftFraction by transition.animateFloat(
        transitionSpec = {
          when {
            initialState == ToggleableState.Off -> snap()
            targetState == ToggleableState.Off -> snap(delayMillis = CheckboxSnapAnimationDelay)
            else -> MaterialDefaultSpatialAnimationSpec
          }
        },
      ) {
        when (it) {
          ToggleableState.On -> 0f
          ToggleableState.Off -> 0f
          ToggleableState.Indeterminate -> 1f
        }
      }
      val checkPath = remember { Path() }
      val pathToDraw = remember { Path() }
      val pathMeasure = remember { PathMeasure() }
      Canvas(Modifier.fillMaxSize()) {
        drawMaterialCheck(
          checkColor = checkColor,
          checkFraction = checkDrawFraction,
          crossCenterGravitation =
          checkCenterGravitationShiftFraction,
          checkPath = checkPath,
          pathToDraw = pathToDraw,
          pathMeasure = pathMeasure,
        )
      }
    }
  }
}

private fun checkboxBoxColor(
  colors: CheckboxColors,
  enabled: Boolean,
  state: ToggleableState,
): Color {
  return if (enabled) {
    when (state) {
      ToggleableState.On,
      ToggleableState.Indeterminate,
      -> colors.checkedBoxColor
      ToggleableState.Off -> colors.uncheckedBoxColor
    }
  } else {
    when (state) {
      ToggleableState.On -> colors.disabledCheckedBoxColor
      ToggleableState.Indeterminate -> colors.disabledIndeterminateBoxColor
      ToggleableState.Off -> colors.disabledUncheckedBoxColor
    }
  }
}

private fun checkboxBorderColor(
  colors: CheckboxColors,
  enabled: Boolean,
  state: ToggleableState,
): Color {
  return if (enabled) {
    when (state) {
      ToggleableState.On,
      ToggleableState.Indeterminate,
      -> colors.checkedBorderColor
      ToggleableState.Off -> colors.uncheckedBorderColor
    }
  } else {
    when (state) {
      ToggleableState.Indeterminate -> colors.disabledIndeterminateBorderColor
      ToggleableState.On -> colors.disabledBorderColor
      ToggleableState.Off -> colors.disabledUncheckedBorderColor
    }
  }
}

private fun checkboxCheckmarkColor(
  colors: CheckboxColors,
  enabled: Boolean,
  state: ToggleableState,
): Color {
  return if (enabled) {
    if (state == ToggleableState.Off) {
      colors.uncheckedCheckmarkColor
    } else {
      colors.checkedCheckmarkColor
    }
  } else {
    colors.disabledCheckmarkColor
  }
}

@Composable
fun RadioButton(
  selected: Boolean,
  onClick: (() -> Unit)?,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  colors: RadioButtonColors = RadioButtonDefaults.colors(),
  interactionSource: MutableInteractionSource? = null,
) {
  UnstyledRadioGroup(
    value = if (selected) "selected" else null,
    onValueChange = { onClick?.invoke() },
  ) {
    val radioColorTarget = when {
      enabled && selected -> colors.selectedColor
      enabled && !selected -> colors.unselectedColor
      !enabled && selected -> colors.disabledSelectedColor
      else -> colors.disabledUnselectedColor
    }
    val radioColor by animateColorAsState(
      targetValue = radioColorTarget,
      animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
    )
    val dotRadius by animateDpAsState(
      targetValue = if (selected) RadioButtonDotSize / 2 else 0.dp,
      animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
    )
    RadioButton(
      value = "selected",
      modifier = modifier.minimumInteractiveComponentSize(onClick != null),
      enabled = enabled,
      interactionSource = interactionSource,
      indication = ripple(bounded = false, radius = 20.dp),
    ) {
      Canvas(
        Modifier
          .wrapContentSize(Alignment.Center)
          .padding(RadioButtonPadding)
          .requiredSize(RadioButtonSize),
      ) {
        val strokeWidth = RadioButtonStrokeWidth.toPx()
        drawCircle(
          color = radioColor,
          radius = (RadioButtonSize / 2).toPx() - strokeWidth / 2,
          style = Stroke(width = strokeWidth),
        )
        if (dotRadius > 0.dp) {
          drawCircle(
            color = radioColor,
            radius = dotRadius.toPx() - strokeWidth / 2,
            style = Fill,
          )
        }
      }
    }
  }
}

@Composable
fun Switch(
  checked: Boolean,
  onCheckedChange: ((Boolean) -> Unit)?,
  modifier: Modifier = Modifier,
  thumbContent: (@Composable () -> Unit)? = null,
  enabled: Boolean = true,
  colors: SwitchColors = SwitchDefaults.colors(),
  interactionSource: MutableInteractionSource? = null,
) {
  val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
  val pressed by resolvedInteractionSource.collectIsPressedAsState()
  val thumbTargetSize = when {
    pressed -> SwitchPressedThumbSize
    checked || thumbContent != null -> SwitchThumbSize
    else -> SwitchUncheckedThumbSize
  }
  val thumbMotionSpec = if (pressed) {
    snap<Dp>()
  } else {
    MaterialTheme.motionScheme.fastSpatialSpec()
  }
  val thumbSize by animateDpAsState(
    targetValue = thumbTargetSize,
    animationSpec = thumbMotionSpec,
  )
  val checkedThumbOffset = SwitchWidth - SwitchThumbSize - (SwitchHeight - SwitchThumbSize) / 2
  val thumbOffsetTarget = when {
    pressed && checked -> checkedThumbOffset - SwitchTrackOutlineWidth
    pressed && !checked -> SwitchTrackOutlineWidth
    checked -> checkedThumbOffset
    else -> (SwitchHeight - thumbTargetSize) / 2
  }
  val thumbOffset by animateDpAsState(
    targetValue = thumbOffsetTarget,
    animationSpec = thumbMotionSpec,
  )
  val trackShape = MaterialTheme.shapes.extraLarge

  UnstyledSwitch(
    checked = checked,
    onCheckedChange = onCheckedChange,
    enabled = enabled,
    interactionSource = resolvedInteractionSource,
    indication = null,
    modifier = modifier
      .minimumInteractiveComponentSize(onCheckedChange != null)
      .wrapContentSize(Alignment.Center)
      .size(SwitchWidth, SwitchHeight),
  ) {
    Box(
      Modifier.fillMaxSize(),
    ) {
      Box(
        Modifier
          .matchParentSize()
          .background(colors.trackColorFor(enabled, checked), trackShape)
          .border(SwitchTrackOutlineWidth, colors.borderColorFor(enabled, checked), trackShape),
      )

      Box(
        Modifier
          .align(Alignment.CenterStart)
          .offset(x = thumbOffset)
          .size(thumbSize)
          .indication(
            interactionSource = resolvedInteractionSource,
            indication = ripple(
              bounded = false,
              radius = SwitchStateLayerSize / 2,
            ),
          )
          .background(
            colors.thumbColorFor(enabled, checked),
            trackShape,
          ),
        contentAlignment = Alignment.Center,
      ) {
        CompositionLocalProvider(
          LocalContentColor provides colors.iconColorFor(enabled, checked),
        ) {
          thumbContent?.invoke()
        }
      }
    }
  }
}

@Composable
fun Slider(
  value: Float,
  onValueChange: (Float) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
  @IntRange(from = 0) steps: Int = 0,
  onValueChangeFinished: (() -> Unit)? = null,
  colors: SliderColors = SliderDefaults.colors(),
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
  val thumbActive = rememberSliderThumbActive(interactionSource)
  UnstyledSlider(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier.minimumInteractiveComponentSize().height(SliderHeight),
    enabled = enabled,
    interactionSource = interactionSource,
    valueRange = valueRange,
    steps = steps,
    onValueChangeFinished = onValueChangeFinished,
    orientation = Orientation.Horizontal,
    track = { state ->
      PlainSliderTrack(
        progress = state.fraction,
        steps = state.steps,
        enabled = state.enabled,
        thumbActive = thumbActive,
        colors = colors,
      )
    },
    thumb = { state ->
      PlainSliderThumb(colors.thumbColorFor(state.enabled), state.enabled, thumbActive)
    },
  )
}

@Composable
fun Slider(
  state: SliderState,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  colors: SliderColors = SliderDefaults.colors(),
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  thumb: @Composable (SliderState) -> Unit = {},
  track: @Composable (SliderState) -> Unit = {},
) {
  val thumbActive = rememberSliderThumbActive(interactionSource)
  UnstyledSlider(
    value = state.value,
    onValueChange = { state.value = it },
    modifier = modifier.minimumInteractiveComponentSize().height(SliderHeight),
    enabled = enabled,
    interactionSource = interactionSource,
    valueRange = state.valueRange,
    steps = state.steps,
    onValueChangeFinished = state.onValueChangeFinished,
    orientation = Orientation.Horizontal,
    track = { unstyledState ->
      PlainSliderTrack(
        progress = unstyledState.fraction,
        steps = unstyledState.steps,
        enabled = unstyledState.enabled,
        thumbActive = thumbActive,
        colors = colors,
      )
      track(state)
    },
    thumb = { unstyledState ->
      PlainSliderThumb(
        colors.thumbColorFor(unstyledState.enabled),
        unstyledState.enabled,
        thumbActive,
      )
      thumb(state)
    },
  )
}

@Composable
fun RangeSlider(
  value: ClosedFloatingPointRange<Float>,
  onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
  @IntRange(from = 0) steps: Int = 0,
  onValueChangeFinished: (() -> Unit)? = null,
  colors: SliderColors = SliderDefaults.colors(),
) {
}

@Composable
fun RangeSlider(
  value: ClosedFloatingPointRange<Float>,
  onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
  onValueChangeFinished: (() -> Unit)? = null,
  colors: SliderColors = SliderDefaults.colors(),
  startInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  endInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  startThumb: @Composable (RangeSliderState) -> Unit = {},
  endThumb: @Composable (RangeSliderState) -> Unit = {},
  track: @Composable (RangeSliderState) -> Unit = {},
  @IntRange(from = 0) steps: Int = 0,
) {
}

@Composable
fun RangeSlider(
  state: RangeSliderState,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  colors: SliderColors = SliderDefaults.colors(),
  startInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  endInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  startThumb: @Composable (RangeSliderState) -> Unit = {},
  endThumb: @Composable (RangeSliderState) -> Unit = {},
  track: @Composable (RangeSliderState) -> Unit = {},
) {
}

@Composable
fun LinearProgressIndicator(
  progress: () -> Float,
  modifier: Modifier = Modifier,
  color: Color = ProgressIndicatorDefaults.linearColor,
  trackColor: Color = ProgressIndicatorDefaults.linearTrackColor,
  strokeCap: StrokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
  gapSize: Dp = ProgressIndicatorDefaults.LinearIndicatorTrackGapSize,
  drawStopIndicator: DrawScope.() -> Unit = {},
) {
  val coercedProgress = progress().coerceIn(0f, 1f)
  UnstyledProgress(
    progress = coercedProgress,
    modifier = modifier.width(LinearProgressWidth).height(LinearProgressHeight),
  ) {
    Canvas(Modifier.fillMaxSize()) {
      val gapPx = gapSize.toPx()
      val stopRadius = size.height / 2f
      val stopCenter = Offset(size.width - stopRadius, size.height / 2f)
      val activeEnd = (size.width * coercedProgress - gapPx / 2f).coerceAtLeast(0f)
      val inactiveStart = (size.width * coercedProgress + gapPx / 2f).coerceAtMost(size.width)
      val cornerRadius = CornerRadius(size.height / 2f, size.height / 2f)

      if (activeEnd > 0f) {
        drawRoundRect(
          color = color,
          size = Size(activeEnd, size.height),
          cornerRadius = cornerRadius,
        )
      }
      if (inactiveStart < size.width - stopRadius * 3f) {
        drawRoundRect(
          color = trackColor,
          topLeft = Offset(inactiveStart, 0f),
          size = Size(size.width - inactiveStart - stopRadius * 3f, size.height),
          cornerRadius = cornerRadius,
        )
      }
      drawCircle(color = color, radius = stopRadius, center = stopCenter)
    }
  }
}

@Composable
fun LinearProgressIndicator(
  modifier: Modifier = Modifier,
  color: Color = ProgressIndicatorDefaults.linearColor,
  trackColor: Color = ProgressIndicatorDefaults.linearTrackColor,
  strokeCap: StrokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
) {
  UnstyledProgress(
    modifier = modifier.width(LinearProgressWidth).height(LinearProgressHeight),
  ) {
    Box(Modifier.fillMaxSize().background(trackColor, MaterialTheme.shapes.extraSmall))
    Box(
      Modifier
        .fillMaxWidth(0.35f)
        .fillMaxHeight()
        .background(color, MaterialTheme.shapes.extraSmall),
    )
  }
}

@Composable
fun CircularProgressIndicator(
  progress: () -> Float,
  modifier: Modifier = Modifier,
  color: Color = ProgressIndicatorDefaults.circularColor,
  strokeWidth: Dp = ProgressIndicatorDefaults.CircularStrokeWidth,
  trackColor: Color = Color.Transparent,
  strokeCap: StrokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
  gapSize: Dp = ProgressIndicatorDefaults.CircularIndicatorTrackGapSize,
) {
  Canvas(modifier.size(CircularProgressSize)) {
    drawArc(
      color = trackColor,
      startAngle = 0f,
      sweepAngle = 360f,
      useCenter = false,
      style = Stroke(width = strokeWidth.toPx(), cap = strokeCap),
    )
    drawArc(
      color = color,
      startAngle = -90f,
      sweepAngle = progress().coerceIn(0f, 1f) * 360f,
      useCenter = false,
      style = Stroke(width = strokeWidth.toPx(), cap = strokeCap),
    )
  }
}

@Composable
fun CircularProgressIndicator(
  modifier: Modifier = Modifier,
  color: Color = ProgressIndicatorDefaults.circularColor,
  strokeWidth: Dp = ProgressIndicatorDefaults.CircularStrokeWidth,
  trackColor: Color = Color.Transparent,
  strokeCap: StrokeCap = ProgressIndicatorDefaults.CircularIndeterminateStrokeCap,
) {
  Canvas(modifier.size(CircularProgressSize)) {
    if (trackColor != Color.Transparent) {
      drawArc(
        color = trackColor,
        startAngle = 0f,
        sweepAngle = 360f,
        useCenter = false,
        style = Stroke(width = strokeWidth.toPx(), cap = strokeCap),
      )
    }
    drawArc(
      color = color,
      startAngle = -90f,
      sweepAngle = 270f,
      useCenter = false,
      style = Stroke(width = strokeWidth.toPx(), cap = strokeCap),
    )
  }
}

@Composable
fun TextField(
  state: TextFieldState,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  readOnly: Boolean = false,
  textStyle: TextStyle = LocalTextStyle.current,
  label: @Composable (TextFieldLabelScope.() -> Unit)? = null,
  placeholder: @Composable (() -> Unit)? = null,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  prefix: @Composable (() -> Unit)? = null,
  suffix: @Composable (() -> Unit)? = null,
  supportingText: @Composable (() -> Unit)? = null,
  isError: Boolean = false,
  inputTransformation: InputTransformation? = null,
  outputTransformation: OutputTransformation? = null,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  onKeyboardAction: KeyboardActionHandler? = null,
  lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
  onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)? = null,
  scrollState: ScrollState = rememberScrollState(),
  interactionSource: MutableInteractionSource? = null,
  shape: Shape = TextFieldDefaults.shape,
  colors: TextFieldColors = TextFieldDefaults.colors(),
) {
  TextFieldContent(
    state = state,
    modifier = modifier,
    enabled = enabled,
    readOnly = readOnly,
    textStyle = textStyle,
    label = label,
    placeholder = placeholder,
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    prefix = prefix,
    suffix = suffix,
    supportingText = supportingText,
    isError = isError,
    inputTransformation = inputTransformation,
    outputTransformation = outputTransformation,
    keyboardOptions = keyboardOptions,
    onKeyboardAction = onKeyboardAction,
    lineLimits = lineLimits,
    onTextLayout = onTextLayout,
    scrollState = scrollState,
    shape = shape,
    variant = TextFieldVariant.Filled,
    interactionSource = interactionSource,
  )
}

@Composable
private fun TextFieldContent(
  state: TextFieldState,
  modifier: Modifier,
  enabled: Boolean,
  readOnly: Boolean,
  textStyle: TextStyle,
  label: @Composable (TextFieldLabelScope.() -> Unit)? = null,
  placeholder: @Composable (() -> Unit)? = null,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  prefix: @Composable (() -> Unit)? = null,
  suffix: @Composable (() -> Unit)? = null,
  supportingText: @Composable (() -> Unit)? = null,
  isError: Boolean,
  inputTransformation: InputTransformation?,
  outputTransformation: OutputTransformation?,
  keyboardOptions: KeyboardOptions,
  onKeyboardAction: KeyboardActionHandler? = null,
  lineLimits: TextFieldLineLimits,
  onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)?,
  scrollState: ScrollState,
  shape: Shape,
  variant: TextFieldVariant,
  interactionSource: MutableInteractionSource? = null,
) {
  val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
  val isFocused by resolvedInteractionSource.collectIsFocusedAsState()
  val isHovered by resolvedInteractionSource.collectIsHoveredAsState()
  val labelMinimized = label != null && (isFocused || state.text.isNotEmpty())
  var labelSize by remember { mutableStateOf(IntSize.Zero) }
  val labelTransition = updateTransition(labelMinimized, label = "TextFieldLabel")
  val labelProgress by labelTransition.animateFloat(
    transitionSpec = { TextFieldLabelAnimationSpec },
    label = "TextFieldLabelProgress",
  ) { minimized ->
    if (minimized) 1f else 0f
  }

  val activeColor = when {
    !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    isError -> MaterialTheme.colorScheme.error
    isFocused -> MaterialTheme.colorScheme.primary
    else -> MaterialTheme.colorScheme.onSurfaceVariant
  }
  val indicatorColor = when {
    !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    isError -> MaterialTheme.colorScheme.error
    isFocused -> MaterialTheme.colorScheme.primary
    isHovered -> MaterialTheme.colorScheme.onSurface
    variant == TextFieldVariant.Outlined -> MaterialTheme.colorScheme.outline
    else -> MaterialTheme.colorScheme.onSurfaceVariant
  }
  val containerColor = when (variant) {
    TextFieldVariant.Filled -> if (enabled) {
      MaterialTheme.colorScheme.surfaceContainerHighest
    } else {
      MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)
    }
    TextFieldVariant.Outlined -> Color.Transparent
  }
  val indicatorWidth = if (isFocused) {
    TextFieldFocusedIndicatorWidth
  } else {
    TextFieldUnfocusedIndicatorWidth
  }
  val horizontalPadding = if (leadingIcon == null) {
    TextFieldHorizontalPadding
  } else {
    TextFieldIconHorizontalPadding
  }
  val labelStartPadding = if (leadingIcon == null) {
    TextFieldHorizontalPadding
  } else {
    TextFieldIconHorizontalPadding + TextFieldIconSize + TextFieldHorizontalPadding
  }
  val labelScale = lerp(1f, TextFieldLabelScale, labelProgress)
  val outlinedCutoutLabelSize = if (variant == TextFieldVariant.Outlined && labelProgress > 0f) {
    Size(
      width = labelSize.width * labelScale * labelProgress,
      height = labelSize.height * labelScale,
    )
  } else {
    Size.Zero
  }
  val labelTopPadding = when (variant) {
    TextFieldVariant.Filled -> lerp(
      TextFieldLabelExpandedTopPadding.value,
      TextFieldLabelCollapsedTopPadding.value,
      labelProgress,
    ).dp
    TextFieldVariant.Outlined -> lerp(16f, (-7).toFloat(), labelProgress).dp
  }
  val inputTopPadding = if (label == null) {
    8.dp
  } else {
    when (variant) {
      TextFieldVariant.Filled -> 24.dp
      TextFieldVariant.Outlined -> 16.dp
    }
  }
  val inputBottomPadding = if (label == null) 8.dp else 8.dp

  Column(modifier) {
    UnstyledTextField(
      state = state,
      editable = enabled && !readOnly,
      textStyle = textStyle,
      textColor = MaterialTheme.colorScheme.onSurface,
      keyboardOptions = keyboardOptions,
      onKeyboardAction = onKeyboardAction,
      inputTransformation = inputTransformation,
      outputTransformation = outputTransformation,
      lineLimits = lineLimits,
      onTextLayout = onTextLayout,
      interactionSource = resolvedInteractionSource,
      scrollState = scrollState,
    ) {
      Box(Modifier.fillMaxWidth()) {
        TextInput(
          modifier = (
            Modifier
              .fillMaxWidth()
              .heightIn(min = TextFieldContainerHeight)
              .clip(shape)
              .background(containerColor, shape)
            ) then buildModifier {
            when (variant) {
              TextFieldVariant.Filled -> Unit
              TextFieldVariant.Outlined ->
                add(
                  Modifier
                    .outlinedTextFieldCutout(outlinedCutoutLabelSize, labelStartPadding)
                    .border(indicatorWidth, indicatorColor, shape),
                )
            }
          },
          contentPadding = PaddingValues(
            start = horizontalPadding,
            top = inputTopPadding,
            end = TextFieldHorizontalPadding,
            bottom = inputBottomPadding,
          ),
          placeholder = if (label == null || labelMinimized) placeholder else null,
          leading = if (leadingIcon != null || prefix != null) {
            {
              Row(verticalAlignment = Alignment.CenterVertically) {
                leadingIcon?.let {
                  Box(
                    Modifier
                      .padding(end = TextFieldHorizontalPadding)
                      .size(TextFieldIconSize),
                    contentAlignment = Alignment.Center,
                  ) {
                    CompositionLocalProvider(LocalContentColor provides activeColor) {
                      it()
                    }
                  }
                }
                prefix?.let {
                  CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                  ) {
                    it()
                  }
                }
              }
            }
          } else {
            null
          },
          trailing = if (suffix != null || trailingIcon != null) {
            {
              Row(verticalAlignment = Alignment.CenterVertically) {
                suffix?.let {
                  CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                  ) {
                    it()
                  }
                }
                trailingIcon?.let {
                  Box(
                    Modifier
                      .padding(start = TextFieldHorizontalPadding)
                      .size(TextFieldIconSize),
                    contentAlignment = Alignment.Center,
                  ) {
                    CompositionLocalProvider(LocalContentColor provides activeColor) {
                      it()
                    }
                  }
                }
              }
            }
          } else {
            null
          },
        )
        if (variant == TextFieldVariant.Filled) {
          Box(
            Modifier
              .align(Alignment.BottomStart)
              .fillMaxWidth()
              .height(indicatorWidth)
              .background(indicatorColor),
          )
        }
        label?.let {
          Box(
            Modifier
              .padding(start = labelStartPadding)
              .offset(y = labelTopPadding)
              .onSizeChanged { labelSize = it }
              .graphicsLayer {
                scaleX = labelScale
                scaleY = labelScale
                transformOrigin = TransformOrigin(0f, 0f)
              },
          ) {
            CompositionLocalProvider(LocalContentColor provides activeColor) {
              ProvideTextStyle(MaterialTheme.typography.bodyLarge) {
                with(
                  object : TextFieldLabelScope {
                    override val labelMinimizedProgress: Float = labelProgress
                  },
                ) {
                  it()
                }
              }
            }
          }
        }
      }
    }
    supportingText?.let {
      Box(Modifier.padding(start = 16.dp, top = 4.dp, end = 16.dp)) {
        CompositionLocalProvider(
          LocalContentColor provides if (isError) {
            MaterialTheme.colorScheme.error
          } else {
            MaterialTheme.colorScheme.onSurfaceVariant
          },
        ) {
          ProvideTextStyle(MaterialTheme.typography.bodySmall) {
            it()
          }
        }
      }
    }
  }
}

@Composable
fun OutlinedTextField(
  state: TextFieldState,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  readOnly: Boolean = false,
  textStyle: TextStyle = LocalTextStyle.current,
  label: @Composable (TextFieldLabelScope.() -> Unit)? = null,
  placeholder: @Composable (() -> Unit)? = null,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  prefix: @Composable (() -> Unit)? = null,
  suffix: @Composable (() -> Unit)? = null,
  supportingText: @Composable (() -> Unit)? = null,
  isError: Boolean = false,
  inputTransformation: InputTransformation? = null,
  outputTransformation: OutputTransformation? = null,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  onKeyboardAction: KeyboardActionHandler? = null,
  lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
  onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)? = null,
  scrollState: ScrollState = rememberScrollState(),
  interactionSource: MutableInteractionSource? = null,
  shape: Shape = OutlinedTextFieldDefaults.shape,
  colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
) {
  TextFieldContent(
    state = state,
    modifier = modifier,
    enabled = enabled,
    readOnly = readOnly,
    textStyle = textStyle,
    label = label,
    placeholder = placeholder,
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    prefix = prefix,
    suffix = suffix,
    supportingText = supportingText,
    isError = isError,
    keyboardOptions = keyboardOptions,
    inputTransformation = inputTransformation,
    outputTransformation = outputTransformation,
    onKeyboardAction = onKeyboardAction,
    lineLimits = lineLimits,
    onTextLayout = onTextLayout,
    scrollState = scrollState,
    shape = shape,
    variant = TextFieldVariant.Outlined,
    interactionSource = interactionSource,
  )
}

@Composable
fun Icon(
  imageVector: ImageVector,
  contentDescription: String?,
  modifier: Modifier = Modifier,
  tint: Color = LocalContentColor.current,
) {
  UnstyledIcon(imageVector, contentDescription, modifier, tint)
}

@Composable
fun Icon(
  bitmap: ImageBitmap,
  contentDescription: String?,
  modifier: Modifier = Modifier,
  tint: Color = LocalContentColor.current,
) {
  UnstyledIcon(bitmap, contentDescription, modifier, tint)
}

@Composable
fun Icon(
  painter: Painter,
  contentDescription: String?,
  modifier: Modifier = Modifier,
  tint: Color = LocalContentColor.current,
) {
  UnstyledIcon(painter, contentDescription, modifier, tint)
}

@Composable
fun IconButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
  interactionSource: MutableInteractionSource? = null,
  content: @Composable () -> Unit,
) {
  ButtonSurface(
    onClick = onClick,
    modifier = modifier.minimumInteractiveComponentSize().size(IconButtonSize),
    enabled = enabled,
    shape = MaterialTheme.shapes.extraLarge,
    backgroundColor = colors.containerColorFor(enabled),
    contentColor = colors.contentColorFor(enabled),
    border = null,
    contentPadding = PaddingValues(0.dp),
    interactionSource = interactionSource,
    minWidth = 0.dp,
    minHeight = 0.dp,
  ) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
  }
}

@Composable
fun IconToggleButton(
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  colors: IconToggleButtonColors = IconButtonDefaults.iconToggleButtonColors(),
  interactionSource: MutableInteractionSource? = null,
  content: @Composable () -> Unit,
) {
  UnstyledSwitch(
    checked = checked,
    onCheckedChange = onCheckedChange,
    enabled = enabled,
    interactionSource = interactionSource,
    modifier = modifier
      .minimumInteractiveComponentSize()
      .size(IconButtonSize)
      .clip(MaterialTheme.shapes.extraLarge)
      .background(
        colors.containerColorFor(enabled, checked),
        MaterialTheme.shapes.extraLarge,
      ),
  ) {
    CompositionLocalProvider(LocalContentColor provides colors.contentColorFor(enabled, checked)) {
      Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
    }
  }
}

@Composable
fun FilledIconButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  shape: Shape = IconButtonDefaults.filledShape,
  colors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(),
  interactionSource: MutableInteractionSource? = null,
  content: @Composable () -> Unit,
) {
  ButtonSurface(
    onClick = onClick,
    modifier = modifier.minimumInteractiveComponentSize().size(IconButtonSize),
    enabled = enabled,
    shape = shape,
    backgroundColor = colors.containerColorFor(enabled),
    contentColor = colors.contentColorFor(enabled),
    border = null,
    contentPadding = PaddingValues(0.dp),
    interactionSource = interactionSource,
    minWidth = 0.dp,
    minHeight = 0.dp,
  ) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
  }
}

@Composable
fun FilledTonalIconButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  shape: Shape = IconButtonDefaults.filledShape,
  colors: IconButtonColors = IconButtonDefaults.filledTonalIconButtonColors(),
  interactionSource: MutableInteractionSource? = null,
  content: @Composable () -> Unit,
) {
  ButtonSurface(
    onClick = onClick,
    modifier = modifier.minimumInteractiveComponentSize().size(IconButtonSize),
    enabled = enabled,
    shape = shape,
    backgroundColor = colors.containerColorFor(enabled),
    contentColor = colors.contentColorFor(enabled),
    border = null,
    contentPadding = PaddingValues(0.dp),
    interactionSource = interactionSource,
    minWidth = 0.dp,
    minHeight = 0.dp,
  ) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
  }
}

@Composable
fun OutlinedIconButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  shape: Shape = IconButtonDefaults.outlinedShape,
  colors: IconButtonColors = IconButtonDefaults.outlinedIconButtonColors(),
  border: BorderStroke? = IconButtonDefaults.outlinedIconButtonBorder(enabled),
  interactionSource: MutableInteractionSource? = null,
  content: @Composable () -> Unit,
) {
  ButtonSurface(
    onClick = onClick,
    modifier = modifier.minimumInteractiveComponentSize().size(IconButtonSize),
    enabled = enabled,
    shape = shape,
    backgroundColor = colors.containerColorFor(enabled),
    contentColor = colors.contentColorFor(enabled),
    border = border,
    contentPadding = PaddingValues(0.dp),
    interactionSource = interactionSource,
    minWidth = 0.dp,
    minHeight = 0.dp,
  ) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
  }
}

@Composable
private fun FloatingActionButtonSurface(
  onClick: () -> Unit,
  modifier: Modifier,
  shape: Shape,
  containerColor: Color,
  contentColor: Color,
  size: Dp,
  elevation: FloatingActionButtonElevation,
  interactionSource: MutableInteractionSource?,
  content: @Composable () -> Unit,
) {
  M3Surface(
    onClick = onClick,
    modifier = modifier,
    shape = shape,
    color = containerColor,
    contentColor = contentColor,
    tonalElevation = FloatingActionButtonShadowElevation,
    shadowElevation = FloatingActionButtonShadowElevation,
    interactionSource = interactionSource,
  ) {
    ProvideTextStyle(MaterialTheme.typography.labelLarge) {
      Box(
        modifier = Modifier.defaultMinSize(minWidth = size, minHeight = size),
        contentAlignment = Alignment.Center,
      ) {
        content()
      }
    }
  }
}

@Composable
fun FloatingActionButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  shape: Shape = FloatingActionButtonDefaults.shape,
  containerColor: Color = FloatingActionButtonDefaults.containerColor,
  contentColor: Color = androidx.compose.material3.contentColorFor(containerColor),
  elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
  interactionSource: MutableInteractionSource? = null,
  content: @Composable () -> Unit,
) {
  FloatingActionButtonSurface(
    onClick = onClick,
    modifier = modifier,
    shape = shape,
    containerColor = containerColor,
    contentColor = contentColor,
    size = FloatingActionButtonSize,
    elevation = elevation,
    interactionSource = interactionSource,
    content = content,
  )
}

@Composable
fun SmallFloatingActionButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  shape: Shape = FloatingActionButtonDefaults.smallShape,
  containerColor: Color = FloatingActionButtonDefaults.containerColor,
  contentColor: Color = androidx.compose.material3.contentColorFor(containerColor),
  elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
  interactionSource: MutableInteractionSource? = null,
  content: @Composable () -> Unit,
) {
  FloatingActionButtonSurface(
    onClick = onClick,
    modifier = modifier,
    shape = shape,
    containerColor = containerColor,
    contentColor = contentColor,
    size = SmallFloatingActionButtonSize,
    elevation = elevation,
    interactionSource = interactionSource,
    content = content,
  )
}

@Composable
fun LargeFloatingActionButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  shape: Shape = FloatingActionButtonDefaults.largeShape,
  containerColor: Color = FloatingActionButtonDefaults.containerColor,
  contentColor: Color = androidx.compose.material3.contentColorFor(containerColor),
  elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
  interactionSource: MutableInteractionSource? = null,
  content: @Composable () -> Unit,
) {
  FloatingActionButtonSurface(
    onClick = onClick,
    modifier = modifier,
    shape = shape,
    containerColor = containerColor,
    contentColor = contentColor,
    size = LargeFloatingActionButtonSize,
    elevation = elevation,
    interactionSource = interactionSource,
    content = content,
  )
}

@Composable
fun Badge(
  modifier: Modifier = Modifier,
  containerColor: Color = BadgeDefaults.containerColor,
  contentColor: Color = androidx.compose.material3.contentColorFor(containerColor),
  content: @Composable (RowScope.() -> Unit)? = null,
) {
  CompositionLocalProvider(LocalContentColor provides contentColor) {
    Row(
      modifier
        .defaultMinSize(minWidth = BadgeMinSize, minHeight = BadgeMinSize)
        .background(containerColor, MaterialTheme.shapes.extraLarge)
        .padding(horizontal = if (content == null) 0.dp else 4.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      content?.invoke(this)
    }
  }
}

@Composable
fun BadgedBox(
  badge: @Composable BoxScope.() -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable BoxScope.() -> Unit,
) {
  Box(modifier) {
    content()
    Box(Modifier.align(Alignment.TopEnd)) { badge() }
  }
}

@Composable
fun AssistChip(
  onClick: () -> Unit,
  label: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  shape: Shape = AssistChipDefaults.shape,
  colors: androidx.compose.material3.ChipColors = AssistChipDefaults.assistChipColors(),
  elevation: androidx.compose.material3.ChipElevation? = AssistChipDefaults.assistChipElevation(),
  border: BorderStroke? = AssistChipDefaults.assistChipBorder(enabled),
  interactionSource: MutableInteractionSource? = null,
) {
  ButtonSurface(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    backgroundColor = Color.Transparent,
    contentColor = MaterialTheme.colorScheme.onSurface,
    border = border,
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    interactionSource = interactionSource,
  ) {
    leadingIcon?.invoke()
    label()
    trailingIcon?.invoke()
  }
}

@Composable
fun FilterChip(
  selected: Boolean,
  onClick: () -> Unit,
  label: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  shape: Shape = androidx.compose.material3.FilterChipDefaults.shape,
  colors: SelectableChipColors =
    androidx.compose.material3.FilterChipDefaults.filterChipColors(),
  elevation: SelectableChipElevation? =
    androidx.compose.material3.FilterChipDefaults.filterChipElevation(),
  border: BorderStroke? =
    androidx.compose.material3.FilterChipDefaults.filterChipBorder(enabled, selected),
  interactionSource: MutableInteractionSource? = null,
) {
  AssistChip(
    onClick = onClick,
    label = label,
    modifier = modifier,
    enabled = enabled,
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    shape = shape,
    border = border,
    interactionSource = interactionSource,
  )
}

@Composable
fun InputChip(
  selected: Boolean,
  onClick: () -> Unit,
  label: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  avatar: @Composable (() -> Unit)? = null,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  shape: Shape = InputChipDefaults.shape,
  colors: SelectableChipColors = InputChipDefaults.inputChipColors(),
  elevation: SelectableChipElevation? = InputChipDefaults.inputChipElevation(),
  border: BorderStroke? = InputChipDefaults.inputChipBorder(enabled, selected),
  interactionSource: MutableInteractionSource? = null,
) {
  AssistChip(
    onClick = onClick,
    label = label,
    modifier = modifier,
    enabled = enabled,
    leadingIcon = avatar ?: leadingIcon,
    trailingIcon = trailingIcon,
    shape = shape,
    border = border,
    interactionSource = interactionSource,
  )
}

@Composable
fun SuggestionChip(
  onClick: () -> Unit,
  label: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  icon: @Composable (() -> Unit)? = null,
  shape: Shape = SuggestionChipDefaults.shape,
  colors: androidx.compose.material3.ChipColors = SuggestionChipDefaults.suggestionChipColors(),
  elevation: androidx.compose.material3.ChipElevation? =
    SuggestionChipDefaults.suggestionChipElevation(),
  border: BorderStroke? = SuggestionChipDefaults.suggestionChipBorder(enabled),
  interactionSource: MutableInteractionSource? = null,
) {
  AssistChip(
    onClick = onClick,
    label = label,
    modifier = modifier,
    enabled = enabled,
    leadingIcon = icon,
    shape = shape,
    border = border,
    interactionSource = interactionSource,
  )
}

private class MaterialTabRowContext(
  private val tabKeys: List<TabKey>,
) {
  var nextIndex = 0
  private val tabContentWidths = mutableStateMapOf<TabKey, Dp>()
  private val tabSelectionCallbacks = mutableStateMapOf<TabKey, () -> Unit>()

  fun nextTabKey(): TabKey {
    val index = nextIndex++
    return tabKeys.getOrNull(index)
      ?: error("PrimaryTabRow received more tabs than tabKeys. Add a stable key for every tab.")
  }

  fun setContentWidth(tabKey: TabKey, width: Dp) {
    tabContentWidths[tabKey] = width
  }

  fun indicatorWidth(tabKey: TabKey): Dp {
    val contentWidth = tabContentWidths[tabKey] ?: 0.dp
    return if (contentWidth > 24.dp) contentWidth else 24.dp
  }

  fun hasContentWidth(tabKey: TabKey): Boolean = tabContentWidths.containsKey(tabKey)

  fun setSelectionCallback(tabKey: TabKey, onClick: () -> Unit) {
    tabSelectionCallbacks[tabKey] = onClick
  }

  fun select(tabKey: TabKey) {
    tabSelectionCallbacks[tabKey]?.invoke()
  }
}

private class MaterialTabIndicatorState(
  initialOffset: Dp,
  initialWidth: Dp,
) {
  val offset = Animatable(initialOffset, Dp.VectorConverter)
  val width = Animatable(initialWidth, Dp.VectorConverter)
}

private val LocalMaterialTabRowContext = staticCompositionLocalOf<MaterialTabRowContext?> { null }

private fun MaterialTabRowContext?.nextTabKey(): TabKey =
  this?.nextTabKey() ?: error("Tab must be placed inside PrimaryTabRow or SecondaryTabRow.")

@Composable
fun TabListScope<TabKey>.Tab(
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  text: @Composable (() -> Unit)? = null,
  icon: @Composable (() -> Unit)? = null,
  selectedContentColor: Color = LocalContentColor.current,
  unselectedContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
  interactionSource: MutableInteractionSource? = null,
) {
  val height = if (icon == null) PrimaryTabHeight else 64.dp
  val tabRowContext = LocalMaterialTabRowContext.current
  val tabKey = tabRowContext.nextTabKey()
  val density = LocalDensity.current
  val tabIndication = ripple(bounded = true, color = selectedContentColor)
  SideEffect {
    tabRowContext?.setSelectionCallback(tabKey, onClick)
  }

  CompositionLocalProvider(
    LocalContentColor provides if (selected) selectedContentColor else unselectedContentColor,
  ) {
    UnstyledTab(
      key = tabKey,
      enabled = enabled,
      activateOnFocus = false,
      modifier = modifier
        .weight(1f)
        .height(height),
      contentPadding = PaddingValues(horizontal = TabHorizontalPadding),
      indication = tabIndication,
      interactionSource = interactionSource,
    ) {
      Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
          modifier = Modifier.onSizeChanged { size ->
            tabRowContext?.setContentWidth(
              tabKey = tabKey,
              width = with(density) { size.width.toDp() },
            )
          },
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
        ) {
          icon?.let {
            Box(Modifier.size(TabIconSize), contentAlignment = Alignment.Center) {
              it()
            }
            if (text != null) {
              Spacer(Modifier.height(TabIconTextPadding))
            }
          }
          ProvideTextStyle(MaterialTheme.typography.labelLarge) {
            text?.invoke()
          }
        }
      }
    }
  }
}

@Composable
fun TabListScope<TabKey>.Tab(
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  selectedContentColor: Color = LocalContentColor.current,
  unselectedContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
  interactionSource: MutableInteractionSource? = null,
  content: @Composable ColumnScope.() -> Unit,
) {
  val tabRowContext = LocalMaterialTabRowContext.current
  val tabKey = tabRowContext.nextTabKey()
  val density = LocalDensity.current
  val tabIndication = ripple(bounded = true, color = selectedContentColor)
  SideEffect {
    tabRowContext?.setSelectionCallback(tabKey, onClick)
  }

  CompositionLocalProvider(
    LocalContentColor provides if (selected) selectedContentColor else unselectedContentColor,
  ) {
    UnstyledTab(
      key = tabKey,
      enabled = enabled,
      activateOnFocus = false,
      modifier = modifier
        .weight(1f)
        .fillMaxHeight(),
      contentPadding = PaddingValues(horizontal = TabHorizontalPadding),
      indication = tabIndication,
      interactionSource = interactionSource,
    ) {
      Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
          modifier = Modifier.onSizeChanged { size ->
            tabRowContext?.setContentWidth(
              tabKey = tabKey,
              width = with(density) { size.width.toDp() },
            )
          },
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          content = content,
        )
      }
    }
  }
}

@Composable
fun PrimaryTabRow(
  selectedTabIndex: Int,
  tabKeys: List<TabKey>,
  modifier: Modifier = Modifier,
  containerColor: Color = TabRowDefaults.primaryContainerColor,
  contentColor: Color = TabRowDefaults.primaryContentColor,
  indicator: @Composable TabIndicatorScope.() -> Unit = {},
  divider: @Composable () -> Unit = {},
  tabs: @Composable TabListScope<TabKey>.() -> Unit,
) {
  val selectedTab = tabKeys.getOrNull(selectedTabIndex)
    ?: error("selectedTabIndex must reference an entry in tabKeys.")
  val tabRowContext = remember(tabKeys) { MaterialTabRowContext(tabKeys) }
  var indicatorState by remember { mutableStateOf<MaterialTabIndicatorState?>(null) }
  val indicatorAnimationSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Dp>()
  val indicatorScope = rememberCoroutineScope()
  val selectedIndicatorWidth = tabRowContext.indicatorWidth(selectedTab)

  CompositionLocalProvider(LocalContentColor provides contentColor) {
    UnstyledTabGroup(
      selectedTab = selectedTab,
      onSelectedTabChange = { tabRowContext.select(it) },
      tabs = tabKeys,
      modifier = modifier.background(containerColor),
    ) {
      val tabGroup = this
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(PrimaryTabHeight),
      ) {
        val density = LocalDensity.current
        var tabRowSize by remember { mutableStateOf(IntSize.Zero) }
        val tabSlotWidth = with(density) {
          if (tabKeys.isNotEmpty()) (tabRowSize.width / tabKeys.size).toDp() else 0.dp
        }
        val targetIndicatorOffset = tabSlotWidth * selectedTabIndex +
          (tabSlotWidth - selectedIndicatorWidth) / 2
        val isIndicatorReady = tabRowSize != IntSize.Zero &&
          tabRowContext.hasContentWidth(selectedTab)
        LaunchedEffect(
          targetIndicatorOffset,
          selectedIndicatorWidth,
          tabRowSize,
          isIndicatorReady,
        ) {
          if (!isIndicatorReady) {
            return@LaunchedEffect
          }

          val currentIndicatorState = indicatorState
          if (currentIndicatorState == null) {
            indicatorState = MaterialTabIndicatorState(
              initialOffset = targetIndicatorOffset,
              initialWidth = selectedIndicatorWidth,
            )
          } else {
            indicatorScope.launch {
              currentIndicatorState.offset.animateTo(targetIndicatorOffset, indicatorAnimationSpec)
            }
            indicatorScope.launch {
              currentIndicatorState.width.animateTo(selectedIndicatorWidth, indicatorAnimationSpec)
            }
          }
        }

        tabRowContext.nextIndex = 0
        tabGroup.TabList(
          modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { tabRowSize = it },
          verticalAlignment = Alignment.CenterVertically,
        ) {
          CompositionLocalProvider(LocalMaterialTabRowContext provides tabRowContext) {
            tabs()
          }
        }

        Box(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .fillMaxWidth()
            .height(TabDividerHeight)
            .background(MaterialTheme.colorScheme.outlineVariant),
        )
        divider()
        val currentIndicatorState = indicatorState
        if (isIndicatorReady) {
          val indicatorOffset = currentIndicatorState?.offset?.value ?: targetIndicatorOffset
          val indicatorWidth = currentIndicatorState?.width?.value ?: selectedIndicatorWidth
          Box(
            modifier = Modifier
              .align(Alignment.BottomStart)
              .height(PrimaryTabIndicatorHeight)
              .layout { measurable, constraints ->
                val placeable = measurable.measure(
                  constraints.copy(
                    minWidth = indicatorWidth.roundToPx(),
                    maxWidth = indicatorWidth.roundToPx(),
                  ),
                )
                layout(placeable.width, placeable.height) {
                  placeable.place(indicatorOffset.roundToPx(), 0)
                }
              }
              .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
              .background(contentColor),
          )
        }
      }
    }
  }
}

@Composable
fun SecondaryTabRow(
  selectedTabIndex: Int,
  tabKeys: List<TabKey>,
  modifier: Modifier = Modifier,
  containerColor: Color = TabRowDefaults.secondaryContainerColor,
  contentColor: Color = TabRowDefaults.secondaryContentColor,
  indicator: @Composable TabIndicatorScope.() -> Unit = {},
  divider: @Composable () -> Unit = {},
  tabs: @Composable TabListScope<TabKey>.() -> Unit,
) {
  val selectedTab = tabKeys.getOrNull(selectedTabIndex)
    ?: error("selectedTabIndex must reference an entry in tabKeys.")
  val tabRowContext = remember(tabKeys) { MaterialTabRowContext(tabKeys) }
  var indicatorState by remember { mutableStateOf<MaterialTabIndicatorState?>(null) }
  val indicatorAnimationSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Dp>()
  val indicatorScope = rememberCoroutineScope()

  CompositionLocalProvider(LocalContentColor provides contentColor) {
    UnstyledTabGroup(
      selectedTab = selectedTab,
      onSelectedTabChange = { tabRowContext.select(it) },
      tabs = tabKeys,
      modifier = modifier.background(containerColor),
    ) {
      val tabGroup = this
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(PrimaryTabHeight),
      ) {
        val density = LocalDensity.current
        var tabRowSize by remember { mutableStateOf(IntSize.Zero) }
        val tabSlotWidth = with(density) {
          if (tabKeys.isNotEmpty()) (tabRowSize.width / tabKeys.size).toDp() else 0.dp
        }
        val targetIndicatorOffset = tabSlotWidth * selectedTabIndex
        val isIndicatorReady = tabRowSize != IntSize.Zero

        LaunchedEffect(targetIndicatorOffset, tabSlotWidth, tabRowSize, isIndicatorReady) {
          if (!isIndicatorReady) {
            return@LaunchedEffect
          }

          val currentIndicatorState = indicatorState
          if (currentIndicatorState == null) {
            indicatorState = MaterialTabIndicatorState(
              initialOffset = targetIndicatorOffset,
              initialWidth = tabSlotWidth,
            )
          } else {
            indicatorScope.launch {
              currentIndicatorState.offset.animateTo(targetIndicatorOffset, indicatorAnimationSpec)
            }
            indicatorScope.launch {
              currentIndicatorState.width.animateTo(tabSlotWidth, indicatorAnimationSpec)
            }
          }
        }

        tabRowContext.nextIndex = 0
        tabGroup.TabList(
          modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { tabRowSize = it },
          verticalAlignment = Alignment.CenterVertically,
        ) {
          CompositionLocalProvider(LocalMaterialTabRowContext provides tabRowContext) {
            tabs()
          }
        }

        Box(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .fillMaxWidth()
            .height(TabDividerHeight)
            .background(MaterialTheme.colorScheme.outlineVariant),
        )
        divider()
        val currentIndicatorState = indicatorState
        if (isIndicatorReady) {
          val indicatorOffset = currentIndicatorState?.offset?.value ?: targetIndicatorOffset
          val indicatorWidth = currentIndicatorState?.width?.value ?: tabSlotWidth
          Box(
            modifier = Modifier
              .align(Alignment.BottomStart)
              .height(SecondaryTabIndicatorHeight)
              .layout { measurable, constraints ->
                val placeable = measurable.measure(
                  constraints.copy(
                    minWidth = indicatorWidth.roundToPx(),
                    maxWidth = indicatorWidth.roundToPx(),
                  ),
                )
                layout(placeable.width, placeable.height) {
                  placeable.place(indicatorOffset.roundToPx(), 0)
                }
              }
              .background(MaterialTheme.colorScheme.primary),
          )
        }
      }
    }
  }
}

@Composable
fun ModalNavigationDrawer(
  drawerContent: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
  gesturesEnabled: Boolean = true,
  scrimColor: Color = DrawerDefaults.scrimColor,
  content: @Composable () -> Unit,
) {
  Box(modifier.fillMaxSize()) {
    content()
    if (drawerState.isOpen) {
      Box(Modifier.fillMaxSize().background(scrimColor))
      Surface(
        modifier = Modifier.fillMaxHeight().widthIn(max = 360.dp),
        color = DrawerDefaults.modalContainerColor,
        contentColor =
        androidx.compose.material3.contentColorFor(DrawerDefaults.modalContainerColor),
      ) {
        drawerContent()
      }
    }
  }
}

@Composable
fun DropdownMenuItem(
  text: @Composable () -> Unit,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  enabled: Boolean = true,
  colors: androidx.compose.material3.MenuItemColors = MenuDefaults.itemColors(),
  contentPadding: PaddingValues = MenuDefaults.DropdownMenuItemContentPadding,
  interactionSource: MutableInteractionSource? = null,
) {
  Row(
    modifier = modifier
      .clickable(
        enabled = enabled,
        onClick = onClick,
        interactionSource = interactionSource,
        indication = ripple(),
      )
      .fillMaxWidth()
      .defaultMinSize(minHeight = 48.dp)
      .padding(contentPadding),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    ProvideTextStyle(MaterialTheme.typography.labelLarge) {
      if (leadingIcon != null) {
        val leadingIconColor = if (enabled) {
          colors.leadingIconColor
        } else {
          colors.disabledLeadingIconColor
        }
        CompositionLocalProvider(LocalContentColor provides leadingIconColor) {
          Box(Modifier.defaultMinSize(minWidth = 24.dp), contentAlignment = Alignment.CenterStart) {
            leadingIcon()
          }
        }
      }
      val textColor = if (enabled) colors.textColor else colors.disabledTextColor
      CompositionLocalProvider(LocalContentColor provides textColor) {
        Box(
          Modifier
            .weight(1f)
            .padding(
              start = if (leadingIcon != null) 12.dp else 0.dp,
              end = if (trailingIcon != null) 12.dp else 0.dp,
            ),
        ) {
          text()
        }
      }
      if (trailingIcon != null) {
        val trailingIconColor = if (enabled) {
          colors.trailingIconColor
        } else {
          colors.disabledTrailingIconColor
        }
        CompositionLocalProvider(LocalContentColor provides trailingIconColor) {
          Box(Modifier.defaultMinSize(minWidth = 24.dp), contentAlignment = Alignment.CenterEnd) {
            trailingIcon()
          }
        }
      }
    }
  }
}

@Composable
fun DropdownMenu(
  expanded: Boolean,
  onExpandedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
  offset: DpOffset = DpOffset(0.dp, 0.dp),
  scrollState: ScrollState = rememberScrollState(),
  shape: Shape = MenuDefaults.shape,
  containerColor: Color = MenuDefaults.containerColor,
  tonalElevation: Dp = MenuDefaults.TonalElevation,
  shadowElevation: Dp = MenuDefaults.ShadowElevation,
  border: BorderStroke? = null,
  anchor: @Composable () -> Unit,
  content: @Composable ColumnScope.() -> Unit,
) {
  UnstyledDropdownMenu(
    expanded = expanded,
    onExpandedChange = onExpandedChange,
    sideOffset = offset.y,
    alignmentOffset = offset.x,
    panel = {
      DropdownMenuPanel(
        modifier = (
          modifier.width(IntrinsicSize.Max)
            .graphicsLayer {
              this.shadowElevation = shadowElevation.toPx()
              this.shape = shape
              clip = false
            }
            .clip(shape)
            .background(containerColor)
          ) then buildModifier {
          if (border != null) {
            add(Modifier.border(border, shape))
          }
          add(Modifier.verticalScroll(scrollState))
          add(Modifier.padding(vertical = 8.dp))
        },
        enter = DropdownMenuEnterTransition,
        exit = DropdownMenuExitTransition,
      ) {
        Column(content = content)
      }
    },
    anchor = anchor,
  )
}

// @Composable
// fun ExposedDropdownMenuBox(
//   expanded: Boolean,
//   onExpandedChange: (Boolean) -> Unit,
//   modifier: Modifier = Modifier,
//   content: @Composable ExposedDropdownMenuBoxScope.() -> Unit,
// ) {
//   error(
//     "ExposedDropdownMenuBox requires Material3's internal anchor scope, which Unstyled lacks.",
//   )
// }

@Composable
fun ExposedDropdownMenuBoxScope.ExposedDropdownMenu(
  expanded: Boolean,
  onExpandedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
  scrollState: ScrollState = rememberScrollState(),
  matchAnchorWidth: Boolean = true,
  shape: Shape = MenuDefaults.shape,
  containerColor: Color = MenuDefaults.containerColor,
  tonalElevation: Dp = MenuDefaults.TonalElevation,
  shadowElevation: Dp = MenuDefaults.ShadowElevation,
  border: BorderStroke? = null,
  anchor: @Composable ExposedDropdownMenuBoxScope.() -> Unit,
  content: @Composable ColumnScope.() -> Unit,
) {
  UnstyledDropdownMenu(
    expanded = expanded,
    onExpandedChange = onExpandedChange,
    panel = {
      DropdownMenuPanel(
        modifier = (
          modifier.width(IntrinsicSize.Max)
            .graphicsLayer {
              this.shadowElevation = shadowElevation.toPx()
              this.shape = shape
              clip = false
            }
            .clip(shape)
            .background(containerColor)
          ) then buildModifier {
          if (border != null) {
            add(Modifier.border(border, shape))
          }
          add(Modifier.verticalScroll(scrollState))
          add(Modifier.padding(vertical = 8.dp))
        },
        enter = DropdownMenuEnterTransition,
        exit = DropdownMenuExitTransition,
      ) {
        Column(content = content)
      }
    },
    anchor = {
      anchor()
    },
  )
}

@Composable
fun AlertDialog(
  onDismissRequest: () -> Unit,
  confirmButton: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  visible: Boolean = true,
  dismissButton: @Composable (() -> Unit)? = null,
  icon: @Composable (() -> Unit)? = null,
  title: @Composable (() -> Unit)? = null,
  text: @Composable (() -> Unit)? = null,
  shape: Shape = AlertDialogDefaults.shape,
  containerColor: Color = AlertDialogDefaults.containerColor,
  iconContentColor: Color = AlertDialogDefaults.iconContentColor,
  titleContentColor: Color = AlertDialogDefaults.titleContentColor,
  textContentColor: Color = AlertDialogDefaults.textContentColor,
  tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
  properties: DialogProperties = DialogProperties(),
) {
  UnstyledDialog(
    visible = visible,
    onDismissRequest = onDismissRequest,
    properties = com.composeunstyled.DialogProperties(
      dismissOnBackPress = properties.dismissOnBackPress,
      dismissOnClickOutside = properties.dismissOnClickOutside,
    ),
    overlay = {
      Scrim(
        enter = fadeIn(animationSpec = tween(DialogEnterDurationMillis)),
        exit = fadeOut(animationSpec = tween(DialogExitDurationMillis)),
      )
    },
  ) {
    DialogPanel(
      modifier = modifier
        .sizeIn(minWidth = DialogMinWidth, maxWidth = DialogMaxWidth)
        .clip(shape)
        .background(containerColor, shape),
      paneTitle = "Dialog",
      enter = fadeIn(animationSpec = tween(DialogFadeInDurationMillis)) +
        scaleIn(
          initialScale = 0.8f,
          animationSpec = tween(DialogEnterDurationMillis),
        ),
      exit = fadeOut(animationSpec = tween(DialogExitDurationMillis)) +
        scaleOut(
          targetScale = 0.95f,
          animationSpec = tween(DialogExitDurationMillis),
        ),
      contentPadding = DialogPadding,
    ) {
      Column {
        icon?.let {
          CompositionLocalProvider(LocalContentColor provides iconContentColor) {
            Box(Modifier.padding(DialogIconPadding).align(Alignment.CenterHorizontally)) {
              it()
            }
          }
        }
        title?.let {
          CompositionLocalProvider(LocalContentColor provides titleContentColor) {
            ProvideTextStyle(MaterialTheme.typography.headlineSmall) {
              Box(
                Modifier
                  .padding(DialogTitlePadding)
                  .align(
                    if (icon == null) {
                      Alignment.Start
                    } else {
                      Alignment.CenterHorizontally
                    },
                  ),
              ) {
                it()
              }
            }
          }
        }
        text?.let {
          CompositionLocalProvider(LocalContentColor provides textContentColor) {
            ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
              Box(
                Modifier
                  .weight(weight = 1f, fill = false)
                  .padding(DialogTextPadding)
                  .align(Alignment.Start),
              ) {
                it()
              }
            }
          }
        }
        Box(modifier = Modifier.align(Alignment.End)) {
          CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
            ProvideTextStyle(MaterialTheme.typography.labelLarge) {
              Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                dismissButton?.invoke()
                confirmButton()
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun ModalBottomSheet(
  onDismissRequest: () -> Unit,
  modifier: Modifier = Modifier,
  sheetState: ModalBottomSheetState = rememberModalBottomSheetState(
    initialDetent = SheetDetent.Hidden,
    animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
    dismissAnimationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
  ),
  sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,
  sheetGesturesEnabled: Boolean = true,
  shape: Shape = BottomSheetDefaults.ExpandedShape,
  containerColor: Color = BottomSheetDefaults.ContainerColor,
  contentColor: Color = androidx.compose.material3.contentColorFor(containerColor),
  tonalElevation: Dp = 0.dp,
  scrimColor: Color = BottomSheetDefaults.ScrimColor,
  dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
  contentWindowInsets: @Composable () -> WindowInsets = { BottomSheetDefaults.windowInsets },
  properties: ModalBottomSheetProperties = ModalBottomSheetProperties(),
  content: @Composable ColumnScope.() -> Unit,
) {
  UnstyledModalBottomSheet(
    state = sheetState,
    properties = ModalSheetProperties(
      dismissOnBackPress = properties.shouldDismissOnBackPress,
      dismissOnClickOutside = true,
    ),
    onDismiss = onDismissRequest,
    overlay = {
      Scrim(
        scrimColor = scrimColor,
        enter = fadeIn(animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()),
        exit = fadeOut(animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()),
      )
    },
  ) {
    CompositionLocalProvider(LocalContentColor provides contentColor) {
      val windowSize = currentWindowContainerSize()
      val windowWidth = windowSize.width
      val useWideWindowMargins = sheetMaxWidth.isSpecified && windowWidth > sheetMaxWidth
      val topMargin = if (useWideWindowMargins) {
        ModalBottomSheetWideTopMargin
      } else {
        ModalBottomSheetTopMargin
      }
      val maxSheetHeight = (windowSize.height - topMargin).coerceAtLeast(0.dp)
      val sideMargin = if (useWideWindowMargins) {
        ModalBottomSheetWideSideMargin
      } else {
        0.dp
      }
      Sheet(
        modifier = modifier
          .padding(
            start = sideMargin,
            end = sideMargin,
          )
          .heightIn(max = maxSheetHeight)
          .widthIn(max = sheetMaxWidth)
          .fillMaxWidth(),
      ) {
        M3Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = shape,
          color = containerColor,
          contentColor = contentColor,
          tonalElevation = tonalElevation,
        ) {
          Column(Modifier.fillMaxWidth().windowInsetsPadding(contentWindowInsets())) {
            dragHandle?.let {
              Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                it()
              }
            }
            content()
          }
        }
      }
    }
  }
}

@Composable
fun Scaffold(
  modifier: Modifier = Modifier,
  topBar: @Composable () -> Unit = {},
  bottomBar: @Composable () -> Unit = {},
  snackbarHost: @Composable () -> Unit = {},
  floatingActionButton: @Composable () -> Unit = {},
  floatingActionButtonPosition: FabPosition = FabPosition.End,
  containerColor: Color = MaterialTheme.colorScheme.background,
  contentColor: Color = androidx.compose.material3.contentColorFor(containerColor),
  contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
  content: @Composable (PaddingValues) -> Unit,
) {
  CompositionLocalProvider(LocalContentColor provides contentColor) {
    Box(modifier.fillMaxSize().background(containerColor)) {
      Column(Modifier.fillMaxSize()) {
        topBar()
        Box(Modifier.weight(1f)) {
          content(PaddingValues())
        }
        bottomBar()
      }
      Box(Modifier.align(Alignment.BottomCenter)) { snackbarHost() }
      Box(
        Modifier
          .align(
            when (floatingActionButtonPosition) {
              FabPosition.Start -> Alignment.BottomStart
              FabPosition.Center -> Alignment.BottomCenter
              else -> Alignment.BottomEnd
            },
          )
          .padding(16.dp),
      ) {
        floatingActionButton()
      }
    }
  }
}

@Composable
fun Surface(
  modifier: Modifier = Modifier,
  shape: Shape = androidx.compose.ui.graphics.RectangleShape,
  color: Color = MaterialTheme.colorScheme.surface,
  contentColor: Color = androidx.compose.material3.contentColorFor(color),
  tonalElevation: Dp = 0.dp,
  shadowElevation: Dp = 0.dp,
  border: BorderStroke? = null,
  content: @Composable () -> Unit,
) {
  ContainerSurface(
    modifier = modifier,
    shape = shape,
    color = color,
    contentColor = contentColor,
    border = border,
    content = content,
  )
}

@Composable
fun Snackbar(
  modifier: Modifier = Modifier,
  action: @Composable (() -> Unit)? = null,
  dismissAction: @Composable (() -> Unit)? = null,
  actionOnNewLine: Boolean = false,
  shape: Shape = SnackbarDefaults.shape,
  containerColor: Color = SnackbarDefaults.color,
  contentColor: Color = SnackbarDefaults.contentColor,
  actionContentColor: Color = SnackbarDefaults.actionContentColor,
  dismissActionContentColor: Color = SnackbarDefaults.dismissActionContentColor,
  content: @Composable () -> Unit,
) {
  Surface(
    modifier = modifier,
    shape = shape,
    color = containerColor,
    contentColor = contentColor,
  ) {
    if (actionOnNewLine) {
      Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        content()
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
          action?.invoke()
          dismissAction?.invoke()
        }
      }
    } else {
      Row(
        Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Box(Modifier.weight(1f)) { content() }
        action?.invoke()
        dismissAction?.invoke()
      }
    }
  }
}

@Composable
fun Snackbar(
  snackbarData: androidx.compose.material3.SnackbarData,
  modifier: Modifier = Modifier,
  actionOnNewLine: Boolean = false,
  shape: Shape = SnackbarDefaults.shape,
  containerColor: Color = SnackbarDefaults.color,
  contentColor: Color = SnackbarDefaults.contentColor,
  actionColor: Color = SnackbarDefaults.actionColor,
  actionContentColor: Color = SnackbarDefaults.actionContentColor,
  dismissActionContentColor: Color = SnackbarDefaults.dismissActionContentColor,
) {
  Snackbar(
    modifier = modifier,
    action = snackbarData.visuals.actionLabel?.let { label ->
      {
        TextButton(onClick = { snackbarData.performAction() }) {
          Text(label, color = actionColor)
        }
      }
    },
    dismissAction = if (snackbarData.visuals.withDismissAction) {
      {
        TextButton(onClick = { snackbarData.dismiss() }) {
          Text("Dismiss", color = dismissActionContentColor)
        }
      }
    } else {
      null
    },
    actionOnNewLine = actionOnNewLine,
    shape = shape,
    containerColor = containerColor,
    contentColor = contentColor,
    actionContentColor = actionContentColor,
    dismissActionContentColor = dismissActionContentColor,
  ) {
    Text(snackbarData.visuals.message)
  }
}

@Composable
fun SnackbarHost(
  hostState: SnackbarHostState,
  modifier: Modifier = Modifier,
  snackbar: @Composable (androidx.compose.material3.SnackbarData) -> Unit = { Snackbar(it) },
) {
  Box(modifier) {
    hostState.currentSnackbarData?.let { snackbar(it) }
  }
}

@Composable
fun ListItem(
  headlineContent: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  overlineContent: @Composable (() -> Unit)? = null,
  supportingContent: @Composable (() -> Unit)? = null,
  leadingContent: @Composable (() -> Unit)? = null,
  trailingContent: @Composable (() -> Unit)? = null,
  colors: ListItemColors = androidx.compose.material3.ListItemDefaults.colors(),
  tonalElevation: Dp = androidx.compose.material3.ListItemDefaults.Elevation,
  shadowElevation: Dp = androidx.compose.material3.ListItemDefaults.Elevation,
) {
  Row(
    modifier
      .fillMaxWidth()
      .background(MaterialTheme.colorScheme.surface)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    leadingContent?.invoke()
    Column(Modifier.weight(1f)) {
      overlineContent?.invoke()
      headlineContent()
      supportingContent?.invoke()
    }
    trailingContent?.invoke()
  }
}

@Composable
fun HorizontalDivider(
  modifier: Modifier = Modifier,
  thickness: Dp = androidx.compose.material3.DividerDefaults.Thickness,
  color: Color = androidx.compose.material3.DividerDefaults.color,
) {
  UnstyledHorizontalSeparator(color = color, modifier = modifier, thickness = thickness)
}

@Composable
fun VerticalDivider(
  modifier: Modifier = Modifier,
  thickness: Dp = androidx.compose.material3.DividerDefaults.Thickness,
  color: Color = androidx.compose.material3.DividerDefaults.color,
) {
  UnstyledVerticalSeparator(color = color, modifier = modifier, thickness = thickness)
}

interface TooltipScope : com.composeunstyled.TooltipScope

private object MaterialTooltipScope : TooltipScope

@Composable
fun TooltipBox(
  tooltip: @Composable TooltipScope.() -> Unit,
  modifier: Modifier = Modifier,
  enableUserInput: Boolean = true,
  content: @Composable () -> Unit,
) {
  val scope = remember { MaterialTooltipScope }

  Box(modifier = modifier) {
    UnstyledTooltip(
      enabled = enableUserInput,
      side = AnchorSide.Top,
      alignment = AnchorAlignment.Center,
      panel = { scope.tooltip() },
      anchor = content,
    )
  }
}

@Composable
fun TooltipScope.PlainTooltip(
  modifier: Modifier = Modifier,
  caretShape: Shape? = null,
  maxWidth: Dp = TooltipDefaults.plainTooltipMaxWidth,
  shape: Shape = TooltipDefaults.plainTooltipContainerShape,
  contentColor: Color = TooltipDefaults.plainTooltipContentColor,
  containerColor: Color = TooltipDefaults.plainTooltipContainerColor,
  tonalElevation: Dp = 0.dp,
  shadowElevation: Dp = 0.dp,
  content: @Composable () -> Unit,
) {
  TooltipPanel(
    modifier = modifier
      .zIndex(1f)
      .offset(y = (-4).dp),
    enter = fadeIn(animationSpec = tween(durationMillis = 150)) +
      scaleIn(
        animationSpec = tween(durationMillis = 150),
        initialScale = 0.8f,
        transformOrigin = TransformOrigin(0.5f, 1f),
      ),
    exit = fadeOut(animationSpec = tween(durationMillis = 75)) +
      scaleOut(
        animationSpec = tween(durationMillis = 75),
        targetScale = 0.8f,
        transformOrigin = TransformOrigin(0.5f, 1f),
      ),
  ) {
    Box(
      Modifier
        .sizeIn(
          minWidth = 40.dp,
          maxWidth = maxWidth,
          minHeight = 24.dp,
        )
        .background(containerColor, shape)
        .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
      CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalTextStyle provides MaterialTheme.typography.bodySmall,
      ) {
        content()
      }
    }
  }
}
