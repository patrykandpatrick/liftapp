package com.patrykandpatryk.liftapp.core.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview

object BottomAppBar {
    @Composable
    fun Button(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        @DrawableRes iconRes: Int? = null,
        enabled: Boolean = true,
    ) {
        Button(modifier = modifier.fillMaxWidth(), onClick = onClick, enabled = enabled) {
            if (iconRes != null) {
                Icon(painterResource(id = iconRes), null)
                Spacer(modifier = Modifier.width(LocalDimens.current.button.iconPadding))
            }
            Text(text)
        }
    }

    @Composable
    fun Save(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
        BottomAppBar(modifier) {
            Button(
                text = stringResource(id = R.string.action_save),
                iconRes = R.drawable.ic_save,
                onClick = onClick,
                enabled = enabled,
            )
        }
    }
}

@Composable
fun BottomAppBar(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues =
        PaddingValues(
            horizontal = LocalDimens.current.padding.contentHorizontal,
            vertical = LocalDimens.current.padding.itemVertical,
        ),
    content: @Composable RowScope.() -> Unit,
) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier =
            modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .navigationBarsPadding(),
    ) {
        HorizontalDivider()
        Row(modifier = Modifier.padding(paddingValues), content = content)
    }
}

@LightAndDarkThemePreview
@Composable
fun BottomAppBarPreview() {
    LiftAppTheme { BottomAppBar.Save(onClick = {}) }
}
