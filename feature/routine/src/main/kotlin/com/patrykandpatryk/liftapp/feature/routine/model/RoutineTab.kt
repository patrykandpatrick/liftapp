package com.patrykandpatryk.liftapp.feature.routine.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.ui.TabItem

internal enum class RoutineTab(@param:StringRes val nameRes: Int) {
    Exercises(R.string.tab_exercises),
    Details(R.string.tab_details),
}

val routineTabItems: List<TabItem>
    @Composable
    get() = RoutineTab.entries.map { tab -> TabItem(text = stringResource(id = tab.nameRes)) }
