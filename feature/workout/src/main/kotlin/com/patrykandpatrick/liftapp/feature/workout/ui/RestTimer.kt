package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.preview.PreviewTheme
import com.patrykandpatrick.liftapp.core.text.parseMarkup
import com.patrykandpatrick.liftapp.core.time.formattedRemainingTime
import com.patrykandpatrick.liftapp.feature.workout.Constants
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.component.LiftAppCard
import com.patrykandpatrick.liftapp.ui.component.LiftAppCardDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButtonDefaults
import com.patrykandpatrick.liftapp.ui.icons.Cross
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Pause
import com.patrykandpatrick.liftapp.ui.icons.Play
import com.patrykandpatrick.liftapp.ui.modifier.interactiveButtonEffect
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.Shapes
import com.patrykandpatrick.liftapp.ui.theme.Typography
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

internal val RestTimerContainerHeight = 96.dp

@Composable
fun RestTimer(
    remainingDuration: Duration,
    isPaused: Boolean,
    onToggleIsPaused: () -> Unit,
    onUpdateTimerBy: (Duration) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors =
        LiftAppCardDefaults.tonalCardColors.run {
            copy(backgroundColor = backgroundColor.compositeOver(colorScheme.background))
        }

    LiftAppCard(
        colors = colors,
        contentPadding =
            PaddingValues(
                8.dp,
                vertical = 8.dp,
            ),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            LiftAppIconButton(onClick = onCancel) {
                Icon(
                    imageVector = LiftAppIcons.Cross,
                    contentDescription = stringResource(R.string.rest_timer_action_cancel),
                )
            }

            UpdateTimeBy(
                sign = "-",
                seconds = Constants.UPDATE_TIMER_BY_SECONDS,
                onClick = { onUpdateTimerBy(-Constants.UPDATE_TIMER_BY_SECONDS.seconds) },
            )

            Text(
                text = remainingDuration.formattedRemainingTime,
                style = Typography.titleLargeMono,
            )

            UpdateTimeBy(
                sign = "+",
                seconds = Constants.UPDATE_TIMER_BY_SECONDS,
                onClick = { onUpdateTimerBy(Constants.UPDATE_TIMER_BY_SECONDS.seconds) },
            )

            LiftAppIconButton(onClick = onToggleIsPaused) {
                AnimatedContent(isPaused) { isPaused ->
                    Icon(
                        imageVector = if (isPaused) LiftAppIcons.Play else LiftAppIcons.Pause,
                        contentDescription =
                            stringResource(
                                if (isPaused) {
                                    R.string.rest_timer_action_resume
                                } else {
                                    R.string.rest_timer_action_pause
                                }
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun UpdateTimeBy(
    sign: String,
    seconds: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier =
            modifier
                .interactiveButtonEffect(
                    colors = LiftAppIconButtonDefaults.colors,
                    indicationScale = LiftAppIconButtonDefaults.indicationScale,
                    onClick = onClick,
                    shape = Shapes.small,
                    role = Role.Button,
                )
                .padding(8.dp),
    ) {
        Text(text = sign, style = Typography.titleSmallMono)
        Text(
            text = parseMarkup(stringResource(R.string.rest_timer_update_by_seconds, seconds)),
            style = Typography.titleSmallMono,
            textAlign = TextAlign.Center,
        )
    }
}

@LightAndDarkThemePreview
@Composable
private fun RestTimerPreview() {
    PreviewTheme {
        LiftAppBackground {
            RestTimer(
                remainingDuration = 1.minutes + 30.seconds,
                isPaused = true,
                onToggleIsPaused = {},
                onUpdateTimerBy = {},
                onCancel = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
