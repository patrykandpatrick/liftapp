package com.patrykandpatryk.liftapp.feature.exercise.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.core.navigation.composable

fun NavGraphBuilder.addExercise() {
    composable(route = Routes.Exercise) {
        Exercise()
    }
}

@Composable
fun Exercise() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(text = "Exercise Details")
    }
}
