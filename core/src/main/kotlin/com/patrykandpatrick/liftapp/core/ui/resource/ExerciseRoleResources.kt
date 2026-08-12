package com.patrykandpatrick.liftapp.core.ui.resource

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseRole

val ExerciseRole?.nameRes: Int
    @StringRes
    get() =
        when (this) {
            ExerciseRole.PrimaryCompound -> R.string.exercise_role_primary_compound
            ExerciseRole.SecondaryCompound -> R.string.exercise_role_secondary_compound
            ExerciseRole.Isolation -> R.string.exercise_role_isolation
            null -> R.string.exercise_role_unclassified
        }

val ExerciseRole?.prettyName: String
    @Composable get() = stringResource(id = nameRes)

fun ExerciseRole?.getName(context: Context): String = context.getString(nameRes)

val getExerciseRolePrettyName: @Composable (ExerciseRole?) -> String = { it.prettyName }
