package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.component.LiftAppButtonDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppCard
import com.patrykandpatrick.liftapp.ui.component.LiftAppCardDefaults
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.Check
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.StepConnector
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.Alpha
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatryk.liftapp.core.R

@Composable
fun StepperItem(
    setIndex: Int,
    selected: Boolean,
    completed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: (@Composable ColumnScope.(index: Int, completed: Boolean, enabled: Boolean) -> Unit)? =
        null,
) {
    LiftAppCard(
        onClick = onClick,
        enabled = enabled,
        contentPadding =
            PaddingValues(
                horizontal = dimens.stepper.stepBorderPaddingHorizontal,
                vertical = dimens.stepper.stepBorderPaddingVertical,
            ),
        colors =
            if (selected) {
                LiftAppCardDefaults.tonalCardColors
            } else {
                LiftAppButtonDefaults.noBackgroundColors
            },
        modifier = modifier,
        minSize = DpSize.Zero,
        shape = CircleShape,
    ) {
        label?.invoke(this, setIndex, completed, enabled)
    }
}

@Composable
fun StepConnector(modifier: Modifier = Modifier) {
    Icon(
        imageVector = LiftAppIcons.StepConnector,
        contentDescription = null,
        tint = colorScheme.outline,
        modifier = modifier,
    )
}

@Composable
fun ColumnScope.StepperItemLabel(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean,
    completed: Boolean,
) {
    Row(
        modifier = modifier.align(Alignment.CenterHorizontally),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (completed) {
            Icon(
                imageVector = LiftAppIcons.Check,
                contentDescription = null,
                tint = colorScheme.onPrimary,
                modifier =
                    Modifier.background(color = colorScheme.primary, shape = CircleShape)
                        .padding(1.dp)
                        .size(dimens.stepper.stepIconSize),
            )
        }

        Text(
            text = text,
            color = colorScheme.onSurface.copy(Alpha.get(enabled = enabled)),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
@LightAndDarkThemePreview
fun StepperItemPreview() {
    LiftAppTheme {
        val label: @Composable ColumnScope.(Int, Boolean, Boolean) -> Unit =
            { index, completed, enabled ->
                StepperItemLabel(
                    text = stringResource(R.string.exercise_set_set_index, index + 1),
                    enabled = enabled,
                    completed = completed,
                )
            }
        Surface {
            val (selectedIndex, setSelectedIndex) = remember { mutableIntStateOf(0) }
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(dimens.stepper.spacing),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StepperItem(
                    setIndex = 0,
                    selected = selectedIndex == 0,
                    completed = true,
                    onClick = { setSelectedIndex(0) },
                    label = label,
                )
                StepConnector()
                StepperItem(
                    setIndex = 1,
                    selected = selectedIndex == 1,
                    completed = false,
                    onClick = { setSelectedIndex(1) },
                    label = label,
                )
                StepConnector()
                StepperItem(
                    setIndex = 2,
                    selected = selectedIndex == 3,
                    completed = false,
                    onClick = { setSelectedIndex(3) },
                    label = label,
                )
                StepConnector()
                StepperItem(
                    setIndex = 3,
                    selected = selectedIndex == 4,
                    completed = false,
                    onClick = { setSelectedIndex(4) },
                    label = label,
                    enabled = false,
                )
            }
        }
    }
}
