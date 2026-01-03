package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.liftapp.ui.component.LiftAppButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppHorizontalDivider
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Save
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatryk.liftapp.core.R

object BottomAppBar {
    @Composable
    fun Button(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        imageVector: ImageVector? = null,
        enabled: Boolean = true,
    ) {
        LiftAppButton(modifier = modifier.fillMaxWidth(), onClick = onClick, enabled = enabled) {
            if (imageVector != null) {
                Icon(imageVector, null)
            }
            Text(text)
        }
    }

    @Composable
    fun Save(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
        BottomAppBar(modifier) {
            Button(
                text = stringResource(id = R.string.action_save),
                imageVector = LiftAppIcons.Save,
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
        modifier = modifier.background(colorScheme.surface).fillMaxWidth().navigationBarsPadding(),
    ) {
        LiftAppHorizontalDivider()
        Row(modifier = Modifier.padding(paddingValues), content = content)
    }
}

@LightAndDarkThemePreview
@Composable
fun BottomAppBarPreview() {
    LiftAppTheme { BottomAppBar.Save(onClick = {}) }
}
