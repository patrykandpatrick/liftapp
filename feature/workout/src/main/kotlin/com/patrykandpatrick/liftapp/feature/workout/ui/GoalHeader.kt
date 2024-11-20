package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.model.getPrettyStringLong
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.ui.VerticalDivider
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.ui.theme.PillShape
import com.patrykandpatryk.liftapp.domain.goal.Goal
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun GoalHeader(
    goal: Goal,
    onAddSetClick: () -> Unit,
    onRemoveSetClick: () -> Unit,
    modifier: Modifier = Modifier.Companion,
) {
    Row(
        verticalAlignment = Alignment.Companion.CenterVertically,
        modifier = modifier
            .padding(
                horizontal = LocalDimens.current.padding.contentHorizontal,
                vertical = LocalDimens.current.padding.itemVertical,
            )
            .fillMaxWidth(),
    ) {
        Text(
            text = goal.getPrettyStringLong(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.Companion.weight(1f))

        Row(
            verticalAlignment = Alignment.Companion.CenterVertically,
            modifier = Modifier.Companion
                .height(IntrinsicSize.Min)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = PillShape,
                )
                .clip(PillShape)
                .padding(horizontal = 2.dp)
        ) {
            Box(
                contentAlignment = Alignment.Companion.Center,
                modifier = Modifier.Companion
                    .width(56.dp)
                    .height(40.dp)
                    .clickable(onClick = onRemoveSetClick, role = Role.Companion.Button)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_remove_circle),
                    contentDescription = stringResource(R.string.content_description_remove_set),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            VerticalDivider(Modifier.Companion.padding(vertical = 8.dp))

            Box(
                contentAlignment = Alignment.Companion.Center,
                modifier = Modifier.Companion
                    .width(56.dp)
                    .height(40.dp)
                    .clickable(onClick = onAddSetClick, role = Role.Companion.Button)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_circle),
                    contentDescription = stringResource(R.string.content_description_add_set),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@LightAndDarkThemePreview
@Composable
private fun GoalHeaderPreview() {
    LiftAppTheme {
        Column(modifier = Modifier.Companion.background(MaterialTheme.colorScheme.surface)) {
            GoalHeader(Goal.Companion.Default, {}, {})
            GoalHeader(Goal(1, 1, 1, 0.seconds), {}, {})
        }
    }
}