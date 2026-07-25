package com.patrykandpatryk.liftapp.feature.dashboard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.ui.VerticalGrid
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.component.LiftAppCardDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.component.StatefulContainerColors
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.BicepsFlexed
import com.patrykandpatrick.liftapp.ui.icons.Calculator
import com.patrykandpatrick.liftapp.ui.icons.ChevronRight
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Plan
import com.patrykandpatrick.liftapp.ui.icons.Routine
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.preview.PreviewTheme
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.feature.dashboard.model.Action

@Composable
internal fun Shortcuts(modifier: Modifier = Modifier, onAction: (Action) -> Unit) {
    VerticalGrid(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dimens.padding.itemHorizontalSmall),
        verticalArrangement = Arrangement.spacedBy(dimens.padding.itemVerticalSmall),
    ) {
        Item(
            text = stringResource(R.string.shortcut_1rm_calculator),
            icon = LiftAppIcons.Calculator,
            onClick = { onAction(Action.Navigate(Routes.OneRepMax)) },
        )
        Item(
            text = stringResource(R.string.shortcut_plan),
            icon = LiftAppIcons.Plan,
            onClick = { onAction(Action.Navigate(Routes.Home.Plan)) },
        )
        Item(
            text = stringResource(R.string.shortcut_exercises),
            icon = LiftAppIcons.BicepsFlexed,
            onClick = { onAction(Action.Navigate(Routes.Home.Exercises)) },
        )
        Item(
            text = stringResource(R.string.shortcut_routines),
            icon = LiftAppIcons.Routine,
            onClick = { onAction(Action.Navigate(Routes.Routine.list())) },
        )
    }
}

@Composable
private fun Item(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        title = { LiftAppText(text = text, style = MaterialTheme.typography.titleSmall) },
        icon = { Icon(icon, null, Modifier.size(16.dp)) },
        actions = {
            Icon(
                imageVector = LiftAppIcons.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = colorScheme.onSurfaceVariant,
            )
        },
        onClick = onClick,
        colors = StatefulContainerColors(LiftAppCardDefaults.cardColors),
        modifier = modifier,
    )
}

@LightAndDarkThemePreview
@Composable
private fun ShortcutsPreview() {
    PreviewTheme {
        LiftAppBackground {
            Shortcuts(
                modifier =
                    Modifier.padding(
                        horizontal = dimens.padding.contentHorizontal,
                        vertical = dimens.padding.contentVertical,
                    ),
                onAction = {},
            )
        }
    }
}
