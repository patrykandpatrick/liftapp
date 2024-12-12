package com.patrykandpatryk.liftapp.feature.exercise.model

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.Tab
import com.patrykandpatryk.liftapp.feature.exercise.ui.Details
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal val tabs: ImmutableList<Tab> =
    persistentListOf(
        Tab(
            nameRes = R.string.tab_stats,
            content = {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(id = R.string.tab_stats),
                    )
                }
            },
        ),
        Tab(nameRes = R.string.tab_details, content = { Details() }),
    )
