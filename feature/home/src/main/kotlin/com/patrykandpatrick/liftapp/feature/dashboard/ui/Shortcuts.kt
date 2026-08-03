package com.patrykandpatrick.liftapp.feature.dashboard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.preview.PreviewTheme
import com.patrykandpatrick.liftapp.feature.dashboard.model.Action
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.ui.VerticalGrid
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.component.LiftAppCard
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItemDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.BicepsFlexed
import com.patrykandpatrick.liftapp.ui.icons.Calculator
import com.patrykandpatrick.liftapp.ui.icons.ChevronRight
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Plan
import com.patrykandpatrick.liftapp.ui.icons.Routine
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.PillShape
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
internal fun Shortcuts(modifier: Modifier = Modifier, onAction: (Action) -> Unit) {
    VerticalGrid(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
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
    LiftAppCard(
        onClick = onClick,
        contentPadding = PaddingValues(start = 8.dp, top = 8.dp, end = 12.dp, bottom = 8.dp),
        modifier = modifier.fillMaxWidth(),
        shape = PillShape,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LiftAppListItemDefaults.IconCircle {
                Icon(icon, contentDescription = null)
            }
            LiftAppText(
                text = text,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = LiftAppIcons.ChevronRight,
                contentDescription = null,
                tint = colorScheme.foregroundVariant,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@LightAndDarkThemePreview
@Composable
private fun ShortcutsPreview() {
    PreviewTheme {
        LiftAppBackground {
            Shortcuts(
                modifier =
                    Modifier.padding(
                        start = dimens.screen.padding,
                        top = 8.dp,
                        end = dimens.screen.padding,
                        bottom = 16.dp,
                    ),
                onAction = {},
            )
        }
    }
}
