package com.patrykandpatryk.liftapp.feature.exercises.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.toPaddingValues
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.core.ui.ExtendedFloatingActionButton
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.ListSectionTitle
import com.patrykandpatryk.liftapp.core.ui.SearchBar
import com.patrykandpatryk.liftapp.core.ui.dimens.dimens
import com.patrykandpatryk.liftapp.feature.exercises.model.GroupBy

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
        modifier = modifier.padding(padding),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = stringResource(id = R.string.action_new_exercise),
                icon = painterResource(id = R.drawable.ic_add),
                onClick = { navigate(Routes.NewExercise.create()) },
            )
        },
    ) { paddingValues ->

        Box(modifier = Modifier.padding(paddingValues)) {

            LazyColumn(
                contentPadding = WindowInsets.statusBars.toPaddingValues(
                    additionalTop = MaterialTheme.dimens.padding.contentHorizontal * 2 +
                        MaterialTheme.dimens.height.searchBar,
                ),
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
                                modifier = Modifier
                                    .animateItemPlacement()
                                    .clickable { navigate(Routes.Exercise.create(item.id)) },
                                title = item.name,
                                description = item.muscles,
                                iconPainter = painterResource(id = item.iconRes),
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

            SearchBar(
                value = query,
                onValueChange = viewModel::setQuery,
                modifier = Modifier
                    .align(alignment = Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(
                        start = MaterialTheme.dimens.padding.contentHorizontal,
                        end = MaterialTheme.dimens.padding.contentHorizontal,
                        top = MaterialTheme.dimens.padding.contentHorizontal,
                    ),
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
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

                FilterChip(
                    selected = groupBy == it,
                    onClick = { onGroupBySelection(it) },
                    label = { Text(text = stringResource(id = it.labelResourceId)) },
                )
            }
        }
    }
}
