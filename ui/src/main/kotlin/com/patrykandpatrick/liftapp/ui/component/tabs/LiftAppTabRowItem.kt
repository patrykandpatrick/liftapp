package com.patrykandpatrick.liftapp.ui.component.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
fun LiftAppTabRowItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.spacedBy(dimens.tab.iconToTextPadding, Alignment.CenterVertically),
        modifier =
            modifier
                .selectable(selected = selected, role = Role.Tab, onClick = onClick)
                .padding(vertical = dimens.tab.verticalPadding),
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.titleSmall,
            LocalContentColor provides if (selected) colorScheme.primary else colorScheme.onSurface,
        ) {
            content()
        }
    }
}
