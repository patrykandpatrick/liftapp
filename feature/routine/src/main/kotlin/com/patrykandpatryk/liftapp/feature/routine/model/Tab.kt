package com.patrykandpatryk.liftapp.feature.routine.model

import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.Tab
import com.patrykandpatryk.liftapp.feature.routine.navigator.RoutineNavigator
import com.patrykandpatryk.liftapp.feature.routine.ui.Details
import com.patrykandpatryk.liftapp.feature.routine.ui.Exercises
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal fun tabs(navigator: RoutineNavigator): ImmutableList<Tab> =
    persistentListOf(
        Tab(nameRes = R.string.tab_exercises, content = { Exercises(navigator) }),
        Tab(nameRes = R.string.tab_details, content = { Details() }),
    )
