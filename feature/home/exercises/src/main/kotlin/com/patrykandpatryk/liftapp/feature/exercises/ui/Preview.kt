package com.patrykandpatryk.liftapp.feature.exercises.ui

import androidx.compose.runtime.Composable
import com.patrykandpatrick.liftapp.navigation.data.ExerciseListRouteData
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.preview.PreviewResource
import com.patrykandpatryk.liftapp.feature.exercises.model.ScreenState

@Composable
internal fun getScreenState(mode: ExerciseListRouteData.Mode): ScreenState =
    ScreenState(
        mode = mode,
        query = PreviewResource.textFieldStateManager().stringTextField(),
        exercises =
            listOf(
                ExercisesItem.Header("A"),
                ExercisesItem.Exercise(
                    id = 0,
                    key = 0,
                    name = "Arnold Shoulder Press",
                    muscles = "Shoulders",
                    iconRes = R.drawable.ic_workout,
                    checked = true,
                ),
                ExercisesItem.Exercise(
                    id = 1,
                    key = 1,
                    name = "Australian Push-Up",
                    muscles = "Lats",
                    iconRes = R.drawable.ic_calisthenics,
                    checked = true,
                ),
                ExercisesItem.Exercise(
                    id = 2,
                    key = 2,
                    name = "Axe Hold",
                    muscles = "Shoulders",
                    iconRes = R.drawable.ic_time,
                ),
                ExercisesItem.Header("B"),
                ExercisesItem.Exercise(
                    id = 3,
                    key = 3,
                    name = "Back Extension",
                    muscles = "Lower Back",
                    iconRes = R.drawable.ic_workout,
                ),
                ExercisesItem.Exercise(
                    id = 4,
                    key = 4,
                    name = "Barbell Bicep Curl",
                    muscles = "Biceps",
                    iconRes = R.drawable.ic_workout,
                    checked = true,
                ),
                ExercisesItem.Exercise(
                    id = 5,
                    key = 5,
                    name = "Barbell French Press",
                    muscles = "Triceps",
                    iconRes = R.drawable.ic_workout,
                ),
                ExercisesItem.Exercise(
                    id = 6,
                    key = 6,
                    name = "Barbell Row",
                    muscles = "Lats",
                    iconRes = R.drawable.ic_workout,
                ),
                ExercisesItem.Exercise(
                    id = 7,
                    key = 7,
                    name = "Bulgarian Split Squat",
                    muscles = "Quadriceps",
                    iconRes = R.drawable.ic_workout,
                ),
            ),
    )
