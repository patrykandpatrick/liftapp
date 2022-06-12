package com.patrykandpatryk.liftapp.feature.exercise.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Suppress("UnusedPrivateMember")
@Composable
fun Exercises(modifier: Modifier = Modifier) {

    val viewModel: ExerciseViewModel = hiltViewModel()

    viewModel.exercises.collectAsState(initial = emptyList())
}
