package com.patrykandpatrick.liftapp.core.ui.routine

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.domain.routine.RoutineWithExercises
import com.patrykandpatrick.liftapp.ui.component.LiftAppButtonDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.component.appendBulletList
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.TreePalm
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
fun RestCard(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        Icon(imageVector = LiftAppIcons.TreePalm, contentDescription = null)
        LiftAppText(
            text = stringResource(R.string.training_plan_item_rest_day),
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

@Composable
fun RoutineCard(
    routineWithExercises: RoutineWithExercises,
    modifier: Modifier = Modifier,
    actionsRow: (@Composable RowScope.() -> Unit)? = null,
) {
    RoutineCard(
        routineName = routineWithExercises.name,
        exerciseNames = routineWithExercises.exercises.map { it.name },
        modifier = modifier,
        actionsRow = actionsRow,
    )
}

@Composable
fun RoutineCard(
    routineName: String,
    exerciseNames: List<String>,
    modifier: Modifier = Modifier,
    actionsRow: (@Composable RowScope.() -> Unit)? = null,
) {
    val buttonPadding = LiftAppButtonDefaults.plainContentPadding
    val layoutDirection = LocalLayoutDirection.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        LiftAppText(text = routineName, style = MaterialTheme.typography.titleMedium)

        LiftAppText(
            text =
                if (exerciseNames.isEmpty()) {
                    AnnotatedString(stringResource(R.string.state_no_exercises))
                } else {
                    buildAnnotatedString { appendBulletList(exerciseNames) }
                },
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
            color = colorScheme.foregroundVariant,
        )

        if (actionsRow != null) {
            Row(
                modifier =
                    Modifier.align(Alignment.End)
                        .offset(
                            x = buttonPadding.calculateEndPadding(layoutDirection),
                            y = buttonPadding.calculateBottomPadding(),
                        ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                content = actionsRow,
            )
        }
    }
}
