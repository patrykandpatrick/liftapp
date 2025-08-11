package com.patrykandpatrick.liftapp.ui.icons

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme

val LiftAppIcons.StepConnector: ImageVector
    get() =
        _StepConnector
            ?: ImageVector.Builder(
                    name = "StepConnector",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                .apply {
                    path(stroke = SolidColor(Color.White), strokeLineWidth = 1.5f) {
                        moveTo(24f * .375f, 24f * .75f)
                        lineTo(24f * .625f, 24f * .5f)
                        lineTo(24f * .375f, 24f * .25f)
                    }
                }
                .build()
                .also { _StepConnector = it }

private var _StepConnector: ImageVector? = null

@LightAndDarkThemePreview
@Composable
private fun StepConnectorPreview() {
    LiftAppTheme { LiftAppBackground { Icon(LiftAppIcons.StepConnector, null) } }
}
