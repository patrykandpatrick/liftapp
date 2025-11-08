package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.feature.workout.Constants
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.component.LiftAppCard
import com.patrykandpatrick.liftapp.ui.component.LiftAppCardDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButtonDefaults
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.Cross
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.modifier.interactiveButtonEffect
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.Shapes
import com.patrykandpatrick.liftapp.ui.theme.Typography
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.preview.PreviewTheme
import com.patrykandpatryk.liftapp.core.text.parseMarkup
import com.patrykandpatryk.liftapp.core.time.formattedRemainingTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun RestTimer(
    remainingDuration: Duration,
    isPaused: Boolean,
    onToggleIsPaused: () -> Unit,
    onUpdateTimerBy: (Duration) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LiftAppCard(
        colors = LiftAppCardDefaults.tonalCardColors,
        contentPadding =
            PaddingValues(
                dimens.padding.itemHorizontalSmall,
                vertical = dimens.padding.itemVerticalSmall,
            ),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            LiftAppIconButton(onClick = onCancel) {
                Icon(imageVector = LiftAppIcons.Cross, contentDescription = null)
            }

            UpdateTimeBy(
                sign = "-",
                seconds = -Constants.UPDATE_TIMER_BY_SECONDS,
                onClick = { onUpdateTimerBy(-Constants.UPDATE_TIMER_BY_SECONDS.seconds) },
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier,
            ) {
                Text(
                    text = remainingDuration.formattedRemainingTime,
                    style = Typography.titleLargeMono,
                )
            }

            UpdateTimeBy(
                sign = "+",
                seconds = Constants.UPDATE_TIMER_BY_SECONDS,
                onClick = { onUpdateTimerBy(Constants.UPDATE_TIMER_BY_SECONDS.seconds) },
            )

            LiftAppIconButton(onClick = onToggleIsPaused) {
                AnimatedContent(isPaused) { isPaused ->
                    Icon(
                        painter =
                            painterResource(
                                id = if (isPaused) R.drawable.ic_play else R.drawable.ic_pause
                            ),
                        contentDescription = null,
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
