package com.patrykandpatryk.liftapp.feature.exercise.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.core.ui.ExtendedFloatingActionButton
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.ListSectionTitle
import com.patrykandpatryk.liftapp.core.ui.TopAppBar
import com.patrykandpatryk.liftapp.core.ui.topAppBarScrollBehavior
import com.patrykandpatryk.liftapp.feature.exercise.model.GroupBy

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Exercises(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
    navigate: (String) -> Unit,
) {

    val viewModel: ExerciseViewModel = hiltViewModel()
    val topAppBarScrollBehavior = topAppBarScrollBehavior()
    val exercises by viewModel.exercises.collectAsState()
    val query by viewModel.query.collectAsState()
    val groupBy by viewModel.groupBy.collectAsState()

    Scaffold(
        modifier = modifier
            .padding(padding)
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            Column {

                TopAppBar(
                    title = stringResource(id = R.string.route_exercises),
                    scrollBehavior = topAppBarScrollBehavior,
                )

                TextField(
                    value = query,
                    onValueChange = viewModel::setQuery,
                )

                Row {

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
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = stringResource(id = R.string.action_new_exercise),
                icon = painterResource(id = R.drawable.ic_add),
                onClick = { navigate(Routes.NewExercise.value) },
            )
        },
    ) { paddingValues ->

        LazyColumn(modifier = Modifier.padding(paddingValues)) {

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
                        )
                    }
                    is ExercisesItem.Header -> {
                        ListSectionTitle(title = item.title)
                    }
                }
            }
        }
    }
}
