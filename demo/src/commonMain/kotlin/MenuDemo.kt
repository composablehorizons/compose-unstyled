package com.composeunstyled.demo

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.core.*
import com.composeunstyled.Text
import kotlinx.coroutines.delay

@Composable
fun DropdownMenuDemo() {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFFFED359), Color(0xFFFFBD66))))
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        var state = rememberMenuState()
        LaunchedEffect(Unit) {
            delay(500)
            state.expanded = true
        }
        Menu(state = state) {
            Box(Modifier.width(240.dp)) {
                MenuButton(
                    shape = RoundedCornerShape(6.dp),
                    backgroundColor = Color.White,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Options",
                            style = TextStyle.Default.copy(fontWeight = FontWeight(500))
                        )
                        Spacer(Modifier.width(8.dp))
                        Image(ChevronDown, null)
                    }
                }
            }

            MenuContent(
                backgroundColor = Color.White,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
                    .width(240.dp)
                    .shadow(4.dp, RoundedCornerShape(8.dp)),
                enter = scaleIn(
                    animationSpec = tween(durationMillis = 120, easing = LinearOutSlowInEasing),
                    initialScale = 0.8f,
                    transformOrigin = TransformOrigin(0f, 0f)
                ) + fadeIn(tween(durationMillis = 30)),
                exit = scaleOut(
                    animationSpec = tween(durationMillis = 1, delayMillis = 75),
                    targetScale = 1f
                ) + fadeOut(tween(durationMillis = 75))
            ) {
                MenuItem(
                    modifier = Modifier.padding(4.dp),
                    shape = RoundedCornerShape(8.dp),
                    onClick = { /* TODO */ },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Image(Maximize, null)
                    Spacer(Modifier.width(4.dp))
                    Text("Select all", modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp))
                }
                HorizontalSeparator(color = Color(0xFFBDBDBD))
                MenuItem(
                    modifier = Modifier.padding(4.dp),
                    shape = RoundedCornerShape(8.dp),
                    onClick = { /* TODO */ },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Image(Copy, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Copy", modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp))
                }
                MenuItem(
                    modifier = Modifier.padding(4.dp),
                    shape = RoundedCornerShape(8.dp),
                    enabled = false,
                    onClick = { /* TODO */ },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Image(Scissors, null, colorFilter = ColorFilter.tint(Color(0xFF9E9E9E)))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Cut",
                        style = TextStyle.Default.copy(color = Color(0xFF9E9E9E)),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp)
                    )
                }
                MenuItem(
                    modifier = Modifier.padding(4.dp),
                    shape = RoundedCornerShape(8.dp),
                    enabled = false,
                    onClick = { /* TODO */ },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Image(Clipboard, null, colorFilter = ColorFilter.tint(Color(0xFF9E9E9E)))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Paste",
                        style = TextStyle.Default.copy(color = Color(0xFF9E9E9E)),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp)
                    )
                }
                HorizontalSeparator(color = Color(0xFFBDBDBD))
                MenuItem(
                    modifier = Modifier.padding(4.dp),
                    shape = RoundedCornerShape(8.dp),
                    onClick = { /* TODO */ }) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(Trash2, null, colorFilter = ColorFilter.tint(Color(0xFFC62828)))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Delete",
                            style = TextStyle.Default.copy(color = Color(0xFFC62828)),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}


private val Maximize: ImageVector
    get() {
        if (_Maximize != null) {
            return _Maximize!!
        }
        _Maximize = ImageVector.Builder(
            name = "Maximize",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(8f, 3f)
                horizontalLineTo(5f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, -2f, 2f)
                verticalLineToRelative(3f)
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(21f, 8f)
                verticalLineTo(5f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, -2f, -2f)
                horizontalLineToRelative(-3f)
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(3f, 16f)
                verticalLineToRelative(3f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 2f, 2f)
                horizontalLineToRelative(3f)
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(16f, 21f)
                horizontalLineToRelative(3f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 2f, -2f)
                verticalLineToRelative(-3f)
            }
        }.build()
        return _Maximize!!
    }

private var _Maximize: ImageVector? = null

private val Copy: ImageVector
    get() {
        if (_Copy != null) {
            return _Copy!!
        }
        _Copy = ImageVector.Builder(
            name = "Copy",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(10f, 8f)
                horizontalLineTo(20f)
                arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 22f, 10f)
                verticalLineTo(20f)
                arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 20f, 22f)
                horizontalLineTo(10f)
                arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8f, 20f)
                verticalLineTo(10f)
                arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 10f, 8f)
                close()
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(4f, 16f)
                curveToRelative(-1.1f, 0f, -2f, -0.9f, -2f, -2f)
                verticalLineTo(4f)
                curveToRelative(0f, -1.1f, 0.9f, -2f, 2f, -2f)
                horizontalLineToRelative(10f)
                curveToRelative(1.1f, 0f, 2f, 0.9f, 2f, 2f)
            }
        }.build()
        return _Copy!!
    }

private var _Copy: ImageVector? = null


private val Scissors: ImageVector
    get() {
        if (_Scissors != null) {
            return _Scissors!!
        }
        _Scissors = ImageVector.Builder(
            name = "Scissors",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(9f, 6f)
                arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, 6f, 9f)
                arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, 3f, 6f)
                arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, 9f, 6f)
                close()
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(8.12f, 8.12f)
                lineTo(12f, 12f)
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(20f, 4f)
                lineTo(8.12f, 15.88f)
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(9f, 18f)
                arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, 6f, 21f)
                arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, 3f, 18f)
                arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, 9f, 18f)
                close()
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(14.8f, 14.8f)
                lineTo(20f, 20f)
            }
        }.build()
        return _Scissors!!
    }

private var _Scissors: ImageVector? = null

private val Clipboard: ImageVector
    get() {
        if (_Clipboard != null) {
            return _Clipboard!!
        }
        _Clipboard = ImageVector.Builder(
            name = "Clipboard",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(9f, 2f)
                horizontalLineTo(15f)
                arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 16f, 3f)
                verticalLineTo(5f)
                arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 15f, 6f)
                horizontalLineTo(9f)
                arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8f, 5f)
                verticalLineTo(3f)
                arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 9f, 2f)
                close()
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(16f, 4f)
                horizontalLineToRelative(2f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, 2f)
                verticalLineToRelative(14f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2f, 2f)
                horizontalLineTo(6f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2f, -2f)
                verticalLineTo(6f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, -2f)
                horizontalLineToRelative(2f)
            }
        }.build()
        return _Clipboard!!
    }

private var _Clipboard: ImageVector? = null


private val Trash2: ImageVector
    get() {
        if (_Trash2 != null) {
            return _Trash2!!
        }
        _Trash2 = ImageVector.Builder(
            name = "Trash2",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(3f, 6f)
                horizontalLineToRelative(18f)
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(19f, 6f)
                verticalLineToRelative(14f)
                curveToRelative(0f, 1f, -1f, 2f, -2f, 2f)
                horizontalLineTo(7f)
                curveToRelative(-1f, 0f, -2f, -1f, -2f, -2f)
                verticalLineTo(6f)
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(8f, 6f)
                verticalLineTo(4f)
                curveToRelative(0f, -1f, 1f, -2f, 2f, -2f)
                horizontalLineToRelative(4f)
                curveToRelative(1f, 0f, 2f, 1f, 2f, 2f)
                verticalLineToRelative(2f)
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(10f, 11f)
                lineTo(10f, 17f)
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(14f, 11f)
                lineTo(14f, 17f)
            }
        }.build()
        return _Trash2!!
    }

private var _Trash2: ImageVector? = null
