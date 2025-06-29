package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
fun ColumnScope.SupportingText(
    text: String,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    isError: Boolean = false,
) {
    val contentColor = if (isError) colorScheme.error else colorScheme.onSurfaceVariant

    AnimatedVisibility(visible = visible) {
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            LocalTextStyle provides MaterialTheme.typography.bodySmall,
        ) {
            Box(
                modifier =
                    modifier.padding(
                        horizontal = dimens.padding.supportingTextHorizontal,
                        vertical = dimens.padding.supportingTextVertical,
                    )
            ) {
                Text(text = text)
            }
        }
    }
}
