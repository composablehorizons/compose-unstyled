import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val Search: ImageVector
	get() {
		if (_Search != null) {
			return _Search!!
		}
		_Search = ImageVector.Builder(
            name = "Search",
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
				moveTo(784f, 840f)
				lineTo(532f, 588f)
				quadToRelative(-30f, 24f, -69f, 38f)
				reflectiveQuadToRelative(-83f, 14f)
				quadToRelative(-109f, 0f, -184.5f, -75.5f)
				reflectiveQuadTo(120f, 380f)
				reflectiveQuadToRelative(75.5f, -184.5f)
				reflectiveQuadTo(380f, 120f)
				reflectiveQuadToRelative(184.5f, 75.5f)
				reflectiveQuadTo(640f, 380f)
				quadToRelative(0f, 44f, -14f, 83f)
				reflectiveQuadToRelative(-38f, 69f)
				lineToRelative(252f, 252f)
				close()
				moveTo(380f, 560f)
				quadToRelative(75f, 0f, 127.5f, -52.5f)
				reflectiveQuadTo(560f, 380f)
				reflectiveQuadToRelative(-52.5f, -127.5f)
				reflectiveQuadTo(380f, 200f)
				reflectiveQuadToRelative(-127.5f, 52.5f)
				reflectiveQuadTo(200f, 380f)
				reflectiveQuadToRelative(52.5f, 127.5f)
				reflectiveQuadTo(380f, 560f)
			}
		}.build()
		return _Search!!
	}

private var _Search: ImageVector? = null
