package com.patrykandpatrick.liftapp.core.ui.resource

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.extension.joinToPrettyStringIndexed
import com.patrykandpatrick.liftapp.domain.muscle.Muscle

val Muscle.stringRes: Int
    get() =
        when (this) {
            Muscle.Abductors -> R.string.muscle_abductors
            Muscle.Adductors -> R.string.muscle_adductors
            Muscle.Abs -> R.string.muscle_abs
            Muscle.Biceps -> R.string.muscle_biceps
            Muscle.Calves -> R.string.muscle_calves
            Muscle.Chest -> R.string.muscle_chest
            Muscle.Forearms -> R.string.muscle_forearms
            Muscle.Glutes -> R.string.muscle_glutes
            Muscle.Hamstrings -> R.string.muscle_hamstrings
            Muscle.Lats -> R.string.muscle_lats
            Muscle.LowerBack -> R.string.muscle_lower_back
            Muscle.Quadriceps -> R.string.muscle_quadriceps
            Muscle.Shoulders -> R.string.muscle_shoulders
            Muscle.Traps -> R.string.muscle_traps
            Muscle.Triceps -> R.string.muscle_triceps
        }

val Muscle.listContinuationStringRes: Int
    get() =
        when (this) {
            Muscle.Abductors -> R.string.muscle_abductors_list_continuation
            Muscle.Adductors -> R.string.muscle_adductors_list_continuation
            Muscle.Abs -> R.string.muscle_abs_list_continuation
            Muscle.Biceps -> R.string.muscle_biceps_list_continuation
            Muscle.Calves -> R.string.muscle_calves_list_continuation
            Muscle.Chest -> R.string.muscle_chest_list_continuation
            Muscle.Forearms -> R.string.muscle_forearms_list_continuation
            Muscle.Glutes -> R.string.muscle_glutes_list_continuation
            Muscle.Hamstrings -> R.string.muscle_hamstrings_list_continuation
            Muscle.Lats -> R.string.muscle_lats_list_continuation
            Muscle.LowerBack -> R.string.muscle_lower_back_list_continuation
            Muscle.Quadriceps -> R.string.muscle_quadriceps_list_continuation
            Muscle.Shoulders -> R.string.muscle_shoulders_list_continuation
            Muscle.Traps -> R.string.muscle_traps_list_continuation
            Muscle.Triceps -> R.string.muscle_triceps_list_continuation
        }

fun Muscle.getName(context: Context): String = context.getString(stringRes)

val Muscle.prettyName: String
    @Composable get() = stringResource(stringRes)

val getMusclePrettyName: @Composable (Muscle) -> String = { it.prettyName }

@Composable
fun Collection<Muscle>.prettyList(): String = joinToPrettyStringIndexed { index, muscle ->
    stringResource(if (index == 0) muscle.stringRes else muscle.listContinuationStringRes)
}
