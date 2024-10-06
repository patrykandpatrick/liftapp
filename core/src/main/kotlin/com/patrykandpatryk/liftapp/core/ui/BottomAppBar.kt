package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens

object BottomAppBar {
    @Composable
    fun Save(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(
                    horizontal = LocalDimens.current.padding.contentHorizontal,
                    vertical = LocalDimens.current.padding.itemVertical,
                ),
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onClick,
            ) {
                Icon(painterResource(id = R.drawable.ic_save), null)
                Spacer(modifier = Modifier.width(LocalDimens.current.button.iconPadding))
                Text(stringResource(id = R.string.action_save))
            }
        }
    }
}
