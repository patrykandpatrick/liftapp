package com.patrykandpatrick.liftapp.feature.exercises.ui

import androidx.compose.runtime.Composable
import com.patrykandpatrick.liftapp.core.preview.PreviewResource
import com.patrykandpatrick.liftapp.feature.exercises.model.ScreenState
import com.patrykandpatrick.liftapp.navigation.data.ExerciseListRouteData
import com.patrykandpatrick.liftapp.ui.icons.BicepsFlexed
import com.patrykandpatrick.liftapp.ui.icons.Dumbbell
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Timer

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
                    icon = LiftAppIcons.Dumbbell,
                    checked = true,
                ),
                ExercisesItem.Exercise(
                    id = 1,
                    key = 1,
                    name = "Australian Push-Up",
                    muscles = "Lats",
                    icon = LiftAppIcons.BicepsFlexed,
                    checked = true,
                ),
                ExercisesItem.Exercise(
                    id = 2,
                    key = 2,
                    name = "Axe Hold",
                    muscles = "Shoulders",
                    icon = LiftAppIcons.Timer,
                ),
                ExercisesItem.Header("B"),
                ExercisesItem.Exercise(
                    id = 3,
                    key = 3,
                    name = "Back Extension",
                    muscles = "Lower Back",
                    icon = LiftAppIcons.Dumbbell,
                ),
                ExercisesItem.Exercise(
                    id = 4,
                    key = 4,
                    name = "Barbell Bicep Curl",
                    muscles = "Biceps",
                    icon = LiftAppIcons.Dumbbell,
                    checked = true,
                ),
                ExercisesItem.Exercise(
                    id = 5,
                    key = 5,
                    name = "Barbell French Press",
                    muscles = "Triceps",
                    icon = LiftAppIcons.Dumbbell,
                ),
                ExercisesItem.Exercise(
                    id = 6,
                    key = 6,
                    name = "Barbell Row",
                    muscles = "Lats",
                    icon = LiftAppIcons.Dumbbell,
                ),
                ExercisesItem.Exercise(
                    id = 7,
                    key = 7,
                    name = "Bulgarian Split Squat",
                    muscles = "Quadriceps",
                    icon = LiftAppIcons.Dumbbell,
                ),
            ),
    )
