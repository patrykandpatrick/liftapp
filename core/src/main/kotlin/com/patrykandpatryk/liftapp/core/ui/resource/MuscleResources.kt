package com.patrykandpatryk.liftapp.core.ui.resource

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.domain.muscle.Muscle

val Muscle.stringRes: Int
    get() = when (this) {
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

fun Muscle.getName(context: Context): String = context.getString(stringRes)

@Composable
fun Muscle.getName(): String = stringResource(stringRes)
