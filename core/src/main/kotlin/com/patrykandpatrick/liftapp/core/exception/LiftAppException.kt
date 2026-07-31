package com.patrykandpatrick.liftapp.core.exception

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.domain.exception.DisplayableException
import com.patrykandpatrick.liftapp.domain.exception.LiftAppException
import com.patrykandpatrick.liftapp.domain.exception.PlanNotFoundException
import com.patrykandpatrick.liftapp.domain.exception.RoutineNotFoundException

@Composable
fun Throwable.getUIMessage(): String? {
    return when (this) {
        is LiftAppException -> this.getUIMessage()
        is DisplayableException -> message
        else -> null
    }
}

@Composable
private fun LiftAppException.getUIMessage(): String =
    when (this) {
        is RoutineNotFoundException -> R.string.error_routine_not_found
        is PlanNotFoundException -> R.string.error_plan_not_found
    }.let { stringResource(it) }
