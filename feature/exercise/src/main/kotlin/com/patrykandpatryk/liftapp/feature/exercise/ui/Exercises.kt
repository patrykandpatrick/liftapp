package com.patrykandpatryk.liftapp.feature.exercise.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.TopAppBar
import com.patrykandpatryk.liftapp.core.ui.topAppBarScrollBehavior

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Exercises(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
) {

    val viewModel: ExerciseViewModel = hiltViewModel()
    val topAppBarScrollBehavior = topAppBarScrollBehavior()
    val exercises by viewModel.exercises.collectAsState(emptyList())

    Scaffold(
        modifier = modifier
            .padding(padding)
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = stringResource(id = R.string.route_exercises),
                scrollBehavior = topAppBarScrollBehavior,
            )
        }
    ) { paddingValues ->

        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(exercises) { exercise ->
                ListItem(title = exercise.name)
            }
        }
    }
}
