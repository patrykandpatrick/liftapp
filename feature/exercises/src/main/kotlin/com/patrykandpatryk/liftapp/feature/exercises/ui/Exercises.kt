package com.patrykandpatryk.liftapp.feature.exercises.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Icon
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.core.ui.ExtendedFloatingActionButton
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.ListSectionTitle
import com.patrykandpatryk.liftapp.core.ui.SearchBar
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.dimens.dimens
import com.patrykandpatryk.liftapp.feature.exercises.model.GroupBy

@Composable
fun Exercises(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
    navigate: (String) -> Unit,
) {

    val viewModel: ExerciseViewModel = hiltViewModel()
    val exercises by viewModel.exercises.collectAsState()
    val query by viewModel.query.collectAsState()
    val groupBy by viewModel.groupBy.collectAsState()

    Scaffold(
        modifier = modifier.padding(bottom = padding.calculateBottomPadding()),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = stringResource(id = R.string.action_new_exercise),
                icon = painterResource(id = R.drawable.ic_add),
                onClick = { navigate(Routes.NewExercise.create()) },
            )
        },
        topBar = {
            SearchBar(
                value = query,
                onValueChange = viewModel::setQuery,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(all = MaterialTheme.dimens.padding.contentHorizontal),
            )
        },
    ) { internalPadding ->

        LazyColumn(
            contentPadding = PaddingValues(top = internalPadding.calculateTopPadding()),
        ) {

            if (query.isEmpty()) {

                item {

                    Controls(
                        groupBy = groupBy,
                        onGroupBySelection = viewModel::setGroupBy,
                    )
                }
            }

            items(
                items = exercises,
                key = { it.key },
            ) { item ->

                when (item) {
                    is ExercisesItem.Exercise -> {
                        ListItem(
                            title = item.name,
                            description = item.muscles,
                            iconPainter = painterResource(id = item.iconRes),
                            modifier = Modifier
                                .animateItemPlacement()
                                .clickable { navigate(Routes.Exercise.create(item.id)) },
                        )
                    }

                    is ExercisesItem.Header -> {
                        ListSectionTitle(
                            title = item.title,
                            modifier = Modifier.animateItemPlacement(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Controls(
    groupBy: GroupBy,
    onGroupBySelection: (GroupBy) -> Unit,
) {

    Column(modifier = Modifier.padding(vertical = MaterialTheme.dimens.padding.exercisesControlsVertical)) {

        Text(
            text = stringResource(id = R.string.generic_group_by),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = MaterialTheme.dimens.padding.contentHorizontal),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
            modifier = Modifier
                .horizontalScroll(state = rememberScrollState())
                .padding(horizontal = MaterialTheme.dimens.padding.contentHorizontal),
        ) {

            GroupBy.values().forEach {
                val selected = groupBy == it
                FilterChip(
                    modifier = Modifier.animateContentSize(),
                    selected = selected,
                    onClick = { onGroupBySelection(it) },
                    leadingIcon = {
                        if (selected) {
                            Icon(
                                modifier = Modifier.size(LocalDimens.current.chip.iconSize),
                                painter = painterResource(id = R.drawable.ic_check),
                                contentDescription = null,
                                tint = LocalContentColor.current,
                            )
                        }
                    },
                    label = { Text(text = stringResource(id = it.labelResourceId)) },
                )
            }
        }
    }
}
