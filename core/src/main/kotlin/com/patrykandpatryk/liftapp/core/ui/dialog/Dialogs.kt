package com.patrykandpatryk.liftapp.core.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens

@Composable
fun DialogButtons(
    onNegativeButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    onPositiveButtonClick: (() -> Unit)? = null,
    positiveButtonText: String = stringResource(id = android.R.string.ok),
    negativeButtonText: String = stringResource(id = android.R.string.cancel),
    isPositiveButtonEnabled: Boolean = true,
) {

    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement
            .spacedBy(
                space = LocalDimens.current.padding.itemHorizontalSmall,
                alignment = Alignment.End,
            ),
    ) {

        TextButton(onClick = onNegativeButtonClick) {
            Text(text = negativeButtonText)
        }

        if (onPositiveButtonClick != null) {
            TextButton(
                onClick = onPositiveButtonClick,
                enabled = isPositiveButtonEnabled,
            ) {
                Text(text = positiveButtonText)
            }
        }
    }
}
