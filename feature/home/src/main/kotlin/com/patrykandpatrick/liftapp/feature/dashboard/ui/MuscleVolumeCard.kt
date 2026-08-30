package com.patrykandpatrick.liftapp.feature.dashboard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.preview.PreviewMuscleVolume
import com.patrykandpatrick.liftapp.core.preview.PreviewTheme
import com.patrykandpatrick.liftapp.core.ui.volume.MuscleVolumeDefaults
import com.patrykandpatrick.liftapp.core.ui.volume.MuscleVolumeRow
import com.patrykandpatrick.liftapp.core.ui.volume.VolumeBandLegend
import com.patrykandpatrick.liftapp.core.ui.volume.VolumeScale
import com.patrykandpatrick.liftapp.domain.muscle.MuscleVolume
import com.patrykandpatrick.liftapp.feature.dashboard.model.Action
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.component.LiftAppButtonDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppCard
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.component.PlainLiftAppButton
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.BicepsFlexed
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

/**
 * The three hardest-worked muscles of the rolling week, as a way in rather than as the picture: the
 * full table runs to fifteen rows and belongs on its own screen, where the window can be changed.
 */
@Composable
internal fun MuscleVolumeCard(
    muscleVolume: MuscleVolume,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val openScreen = { onAction(Action.Navigate(Routes.MuscleVolume)) }
    // The sort runs over every muscle to keep three, and the ramp is the same for every row.
    val topMuscles = remember(muscleVolume) { muscleVolume.workedMuscles.take(TopMuscleCount) }
    val colorStops = VolumeScale.rememberColorStops()

    LiftAppCard(onClick = openScreen, modifier = modifier.fillMaxWidth()) {
        if (topMuscles.isEmpty()) {
            EmptyContent()
        } else {
            Header(subtitle = stringResource(R.string.muscle_volume_rolling_week))

            Column(
                verticalArrangement = Arrangement.spacedBy(MuscleVolumeDefaults.rowSpacing),
                modifier = Modifier.padding(top = 16.dp),
            ) {
                topMuscles.forEach { muscle ->
                    MuscleVolumeRow(
                        muscle = muscle,
                        volume = muscleVolume,
                        colorStops = colorStops,
                    )
                }
            }

            // Under the rows, where the screen puts it over them: three rows are taken in at
            // one glance, so nothing is met too late and the card can lead with what it is about.
            VolumeBandLegend(modifier = Modifier.padding(top = 12.dp), compact = true)
        }

        ShowAllButton(onClick = openScreen)
    }
}

@Composable
private fun Header(subtitle: String, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        Icon(imageVector = LiftAppIcons.BicepsFlexed, contentDescription = null)

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            LiftAppText(
                text = stringResource(R.string.muscle_volume_title),
                style = typography.titleMedium,
            )

            LiftAppText(
                text = subtitle,
                style = typography.labelMedium,
                color = colorScheme.foregroundVariant,
            )
        }
    }
}

/**
 * An untrained week is a state the card can be in, not an absence of the card: taking it away would
 * make the dashboard's shape depend on whether the user has trained.
 */
@Composable
private fun EmptyContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Header(subtitle = stringResource(R.string.muscle_volume_empty))

        LiftAppText(
            text = stringResource(R.string.muscle_volume_empty_hint),
            style = typography.bodyMedium,
            color = colorScheme.foregroundVariant,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}

@Composable
private fun ColumnScope.ShowAllButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val buttonPadding = LiftAppButtonDefaults.plainContentPadding
    val layoutDirection = LocalLayoutDirection.current

    PlainLiftAppButton(
        onClick = onClick,
        modifier =
            modifier
                .align(Alignment.End)
                .padding(top = 16.dp)
                .offset(
                    x = buttonPadding.calculateEndPadding(layoutDirection),
                    y = buttonPadding.calculateBottomPadding(),
                ),
    ) {
        Text(stringResource(R.string.action_show_all))
    }
}

/** Enough to say where the week went without reprinting the screen it links to. */
private const val TopMuscleCount = 3

@LightAndDarkThemePreview
@Composable
private fun MuscleVolumeCardPreview() {
    PreviewTheme {
        LiftAppBackground {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(dimens.screen.padding),
            ) {
                MuscleVolumeCard(muscleVolume = PreviewMuscleVolume, onAction = {})
                MuscleVolumeCard(muscleVolume = MuscleVolume.Empty, onAction = {})
            }
        }
    }
}

/**
 * The card at the text size that broke it: its content has to answer "how tall are you?" before it
 * knows how wide it will be, and a legend that wraps answers wrongly. The standard previews all run
 * at the default scale.
 */
@Preview(fontScale = 1.5f, widthDp = 360)
@Composable
private fun MuscleVolumeCardLargeTextPreview() {
    PreviewTheme {
        LiftAppBackground {
            MuscleVolumeCard(
                muscleVolume = PreviewMuscleVolume,
                onAction = {},
                modifier = Modifier.padding(dimens.screen.padding),
            )
        }
    }
}
