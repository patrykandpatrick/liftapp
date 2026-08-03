package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
fun LiftAppHorizontalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = dimens.divider.thickness,
    color: Color = colorScheme.outline,
) {
    HorizontalDivider(modifier, thickness, color)
}
