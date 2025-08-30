package com.patrykandpatrick.liftapp.ui.icons

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import kotlin.Unit

val LiftAppIcons.Dropdown: ImageVector
    get() =
        dropdown
            ?: Builder(
                    name = "dropdown",
                    defaultWidth = 20.0.dp,
                    defaultHeight = 20.0.dp,
                    viewportWidth = 20f,
                    viewportHeight = 20f,
                )
                .apply {
                    path(fill = SolidColor(Color(0xFF14143D))) {
                        moveTo(14.586f, 7.5f)
                        horizontalLineTo(5.414f)
                        curveTo(4.523f, 7.5f, 4.077f, 8.577f, 4.707f, 9.207f)
                        lineTo(9.293f, 13.793f)
                        curveTo(9.683f, 14.183f, 10.317f, 14.183f, 10.707f, 13.793f)
                        lineTo(15.293f, 9.207f)
                        curveTo(15.923f, 8.577f, 15.477f, 7.5f, 14.586f, 7.5f)
                        close()
                    }
                }
                .build()
                .also { dropdown = it }

private var dropdown: ImageVector? = null

@Preview
@Composable
private fun Preview(): Unit {
    LiftAppTheme {
        LiftAppBackground { Image(imageVector = LiftAppIcons.Dropdown, contentDescription = "") }
    }
}
