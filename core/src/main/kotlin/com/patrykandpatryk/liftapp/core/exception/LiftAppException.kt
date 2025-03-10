package com.patrykandpatryk.liftapp.core.exception

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.domain.exception.DisplayableException
import com.patrykandpatryk.liftapp.domain.exception.LiftAppException
import com.patrykandpatryk.liftapp.domain.exception.PlanNotFoundException
import com.patrykandpatryk.liftapp.domain.exception.RoutineNotFoundException

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
