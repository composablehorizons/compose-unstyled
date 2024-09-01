import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val Favorite: ImageVector
	get() {
		if (_Favorite != null) {
			return _Favorite!!
		}
		_Favorite = ImageVector.Builder(
            name = "Favorite",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
			path(
    			fill = SolidColor(Color.Black),
    			fillAlpha = 1.0f,
    			stroke = null,
    			strokeAlpha = 1.0f,
    			strokeLineWidth = 1.0f,
    			strokeLineCap = StrokeCap.Butt,
    			strokeLineJoin = StrokeJoin.Miter,
    			strokeLineMiter = 1.0f,
    			pathFillType = PathFillType.NonZero
			) {
				moveTo(480f, 840f)
				lineToRelative(-58f, -52f)
				quadToRelative(-101f, -91f, -167f, -157f)
				reflectiveQuadTo(150f, 512.5f)
				reflectiveQuadTo(95.5f, 416f)
				reflectiveQuadTo(80f, 326f)
				quadToRelative(0f, -94f, 63f, -157f)
				reflectiveQuadToRelative(157f, -63f)
				quadToRelative(52f, 0f, 99f, 22f)
				reflectiveQuadToRelative(81f, 62f)
				quadToRelative(34f, -40f, 81f, -62f)
				reflectiveQuadToRelative(99f, -22f)
				quadToRelative(94f, 0f, 157f, 63f)
				reflectiveQuadToRelative(63f, 157f)
				quadToRelative(0f, 46f, -15.5f, 90f)
				reflectiveQuadTo(810f, 512.5f)
				reflectiveQuadTo(705f, 631f)
				reflectiveQuadTo(538f, 788f)
				close()
				moveToRelative(0f, -108f)
				quadToRelative(96f, -86f, 158f, -147.5f)
				reflectiveQuadToRelative(98f, -107f)
				reflectiveQuadToRelative(50f, -81f)
				reflectiveQuadToRelative(14f, -70.5f)
				quadToRelative(0f, -60f, -40f, -100f)
				reflectiveQuadToRelative(-100f, -40f)
				quadToRelative(-47f, 0f, -87f, 26.5f)
				reflectiveQuadTo(518f, 280f)
				horizontalLineToRelative(-76f)
				quadToRelative(-15f, -41f, -55f, -67.5f)
				reflectiveQuadTo(300f, 186f)
				quadToRelative(-60f, 0f, -100f, 40f)
				reflectiveQuadToRelative(-40f, 100f)
				quadToRelative(0f, 35f, 14f, 70.5f)
				reflectiveQuadToRelative(50f, 81f)
				reflectiveQuadToRelative(98f, 107f)
				reflectiveQuadTo(480f, 732f)
				moveToRelative(0f, -273f)
			}
		}.build()
		return _Favorite!!
	}

private var _Favorite: ImageVector? = null
