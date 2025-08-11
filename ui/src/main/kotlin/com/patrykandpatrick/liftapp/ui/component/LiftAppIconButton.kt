package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import com.patrykandpatrick.liftapp.ui.InteractiveBorderColors
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.ArrowBack
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.modifier.IndicationScale
import com.patrykandpatrick.liftapp.ui.modifier.interactiveButtonEffect
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.Alpha
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.PillShape
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
fun LiftAppIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = LocalContentColor.current,
    colors: InteractiveBorderColors = LiftAppIconButtonDefaults.colors,
    maxBorderSize: Dp? = null,
    content: @Composable () -> Unit,
) {
    Box(
        modifier =
            modifier
                .minimumInteractiveComponentSize()
                .size(dimens.iconButton.size)
                .interactiveButtonEffect(
                    colors = colors,
                    onClick = onClick,
                    enabled = enabled,
                    shape = PillShape,
                    indicationScale = IndicationScale(hover = 1.15f, press = .8f),
                    role = Role.Button,
                    maxBorderWidth = maxBorderSize,
                    maxBorderHeight = maxBorderSize,
                ),
        contentAlignment = Alignment.Center,
    ) {
        val contentColor = color.copy(alpha = Alpha.get(enabled = enabled))
        CompositionLocalProvider(LocalContentColor provides contentColor, content = content)
    }
}

object LiftAppIconButtonDefaults {

    val colors: InteractiveBorderColors
        @Composable
        get() =
            InteractiveBorderColors(
                color = Color.Transparent,
                pressedColor = colorScheme.primary,
                hoverForegroundColor = colorScheme.primary,
                hoverBackgroundColor = colorScheme.outline,
            )

    val outlinedColors: InteractiveBorderColors
        @Composable
        get() =
            InteractiveBorderColors(
                color = colorScheme.outline,
                pressedColor = colorScheme.primary,
                hoverForegroundColor = colorScheme.primary,
                hoverBackgroundColor = colorScheme.outline,
            )
}

@LightAndDarkThemePreview
@Composable
private fun LiftAppIconButtonPreview() {
    LiftAppTheme {
        LiftAppBackground {
            LiftAppIconButton(
                onClick = {},
                content = { Icon(imageVector = LiftAppIcons.ArrowBack, contentDescription = null) },
            )
        }
    }
}
