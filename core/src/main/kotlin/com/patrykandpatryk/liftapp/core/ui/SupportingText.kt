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
import com.patrykandpatryk.liftapp.core.ui.dimens.dimens

@Composable
fun ColumnScope.SupportingText(
    visible: Boolean,
    text: String,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(visible = visible) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.error,
            LocalTextStyle provides MaterialTheme.typography.bodySmall,
        ) {
            Box(
                modifier = modifier.padding(
                    horizontal = MaterialTheme.dimens.padding.supportingTextHorizontal,
                    vertical = MaterialTheme.dimens.padding.supportingTextVertical,
                ),
            ) {
                Text(text = text)
            }
        }
    }
}
