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
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.ui.*

@Composable
fun App() {
    Box(
        modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFFFED359), Color(0xFFFFBD66)))).padding(vertical = 40.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Menu(state = rememberMenuState(expanded = true)) {
            // setting fixed width so that the MenuContents is visually centered
            Box(Modifier.width(240.dp)) {
                MenuButton(Modifier.clip(RoundedCornerShape(6.dp)).background(Color.White)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text("Options", style = TextStyle.Default.copy(fontWeight = FontWeight(500)))
                        Spacer(Modifier.width(8.dp))
                        Image(ChevronDown, null)
                    }
                }
            }

            MenuContent(
                modifier = Modifier.padding(vertical = 4.dp).width(240.dp).shadow(4.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp)).background(Color.White),
                showTransition = scaleIn(
                    tween(durationMillis = 120, easing = LinearOutSlowInEasing),
                    initialScale = 0.8f,
                    transformOrigin = TransformOrigin(0f, 0f)
                ) + fadeIn(tween(durationMillis = 30)),
                hideTransition = scaleOut(tween(durationMillis = 1, delayMillis = 75), targetScale = 1f) + fadeOut(
                    tween(durationMillis = 75)
                )
            ) {
                MenuItem(modifier = Modifier.padding(4.dp).clip(RoundedCornerShape(8.dp)), onClick = { /* TODO */ }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Image(SelectAll, null)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Select all",
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp)
                        )
                    }
                }
                Divider()
                MenuItem(modifier = Modifier.padding(4.dp).clip(RoundedCornerShape(8.dp)), onClick = { /* TODO */ }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Image(Copy, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Copy", modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp))
                    }
                }
                MenuItem(
                    modifier = Modifier.padding(4.dp).clip(RoundedCornerShape(8.dp)),
                    enabled = false,
                    onClick = { }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Image(Cut, null, colorFilter = ColorFilter.tint(Color(0xFF9E9E9E)))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Cut",
                            style = TextStyle.Default.copy(color = Color(0xFF9E9E9E)),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp)
                        )
                    }
                }
                MenuItem(
                    modifier = Modifier.padding(4.dp).clip(RoundedCornerShape(8.dp)),
                    enabled = false,
                    onClick = { }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Image(Paste, null, colorFilter = ColorFilter.tint(Color(0xFF9E9E9E)))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Paste",
                            style = TextStyle.Default.copy(color = Color(0xFF9E9E9E)),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp)
                        )
                    }
                }
                Divider()
                MenuItem(modifier = Modifier.padding(4.dp).clip(RoundedCornerShape(8.dp)), onClick = { /* TODO */ }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Image(Copy, null, colorFilter = ColorFilter.tint(Color(0xFFC62828)))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Delete",
                            style = TextStyle.Default.copy(color = Color(0xFFC62828)),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

val ChevronDown: ImageVector by lazy {
    ImageVector.Builder(
        name = "ChevronDown", defaultWidth = 16.dp, defaultHeight = 16.dp, viewportWidth = 24f, viewportHeight = 24f
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
            moveTo(6f, 9f)
            lineToRelative(6f, 6f)
            lineToRelative(6f, -6f)
        }
    }.build()
}

@Composable
fun Text(text: String, style: TextStyle = TextStyle.Default, modifier: Modifier = Modifier) {
    BasicText(text, style = style, modifier = modifier)
}

@Composable
fun Divider(modifier: Modifier = Modifier) {
    Box(modifier.height(1.dp).fillMaxWidth().background(Color(0xFFBDBDBD)))
}

private var _Cut: ImageVector? = null

public val Cut: ImageVector
    get() {
        if (_Cut != null) {
            return _Cut!!
        }
        _Cut = ImageVector.Builder(
            name = "Scissors", defaultWidth = 20.dp, defaultHeight = 20.dp, viewportWidth = 24f, viewportHeight = 24f
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
        return _Cut!!
    }


private var _Paste: ImageVector? = null

public val Paste: ImageVector
    get() {
        if (_Paste != null) {
            return _Paste!!
        }
        _Paste = ImageVector.Builder(
            name = "Clipboard", defaultWidth = 20.dp, defaultHeight = 20.dp, viewportWidth = 24f, viewportHeight = 24f
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
        return _Paste!!
    }


private var _Copy: ImageVector? = null

public val Copy: ImageVector
    get() {
        if (_Copy != null) {
            return _Copy!!
        }
        _Copy = ImageVector.Builder(
            name = "Copy", defaultWidth = 20.dp, defaultHeight = 20.dp, viewportWidth = 24f, viewportHeight = 24f
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


private var _SelectAll: ImageVector? = null

public val SelectAll: ImageVector
    get() {
        if (_SelectAll != null) {
            return _SelectAll!!
        }
        _SelectAll = ImageVector.Builder(
            name = "Scan", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f
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
                moveTo(3f, 7f)
                verticalLineTo(5f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, -2f)
                horizontalLineToRelative(2f)
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
                moveTo(17f, 3f)
                horizontalLineToRelative(2f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, 2f)
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
                moveTo(21f, 17f)
                verticalLineToRelative(2f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2f, 2f)
                horizontalLineToRelative(-2f)
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
                moveTo(7f, 21f)
                horizontalLineTo(5f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2f, -2f)
                verticalLineToRelative(-2f)
            }
        }.build()
        return _SelectAll!!
    }
