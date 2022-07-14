package com.patrykandpatryk.liftapp.feature.exercises.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

                item {

                    Row(horizontalArrangement = Arrangement.spacedBy(space = 8.dp)) {

                        GroupBy.values().forEach {

                            Text(
                                text = it.name,
                                modifier = Modifier
                                    .background(color = if (groupBy == it) Color.Green else Color.Transparent)
                                    .clickable { viewModel.setGroupBy(groupBy = it) },
                            )
                        }
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
