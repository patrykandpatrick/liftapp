package com.patrykandpatryk.liftapp.feature.exercise.model

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.ui.TabItem
import com.patrykandpatryk.liftapp.feature.exercise.ui.Details
import javax.annotation.concurrent.Immutable

@Immutable
internal data class Tab(
    @StringRes val nameRes: Int,
    val content: @Composable () -> Unit,
)

internal val tabs: List<Tab>
    get() = listOf(
        Tab(
            nameRes = R.string.exercise_tab_stats,
            content = {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(id = R.string.exercise_tab_stats),
                    )
                }
            },
        ),
        Tab(
            nameRes = R.string.exercise_tab_details,
            content = {
                Details()
            },
        ),
    )

internal val List<Tab>.tabItems: List<TabItem>
    @Composable get() = map { tab ->
        TabItem(text = stringResource(id = tab.nameRes))
    }
