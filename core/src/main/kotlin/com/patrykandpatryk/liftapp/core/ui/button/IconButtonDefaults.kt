package com.patrykandpatryk.liftapp.core.ui.button

import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object IconButtonDefaults {

    @Composable
    fun iconButtonColors(
        containerColor: Color = Color.Transparent,
        contentColor: Color = LocalContentColor.current,
        disabledContainerColor: Color = Color.Transparent,
        disabledContentColor: Color =
            contentColor.copy(alpha = IconButtonTokens.DisabledContentColorAlpha),
    ) =
        IconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        )
}
