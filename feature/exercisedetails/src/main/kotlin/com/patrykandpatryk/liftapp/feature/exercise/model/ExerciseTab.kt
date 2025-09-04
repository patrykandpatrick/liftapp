package com.patrykandpatryk.liftapp.feature.exercise.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.ui.TabItem

enum class ExerciseTab(@param:StringRes val nameRes: Int) {
    Statistics(R.string.tab_stats),
    Details(R.string.tab_details),
}

val exerciseTabItems: List<TabItem>
    @Composable
    get() = ExerciseTab.entries.map { tab -> TabItem(text = stringResource(id = tab.nameRes)) }
