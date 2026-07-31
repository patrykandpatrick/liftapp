package com.patrykandpatrick.liftapp.core.ui.resource

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.patrykandpatrick.liftapp.core.model.MuscleModel
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

val MuscleModel.Type.color: Color
    @Composable
    get() =
        when (this) {
            MuscleModel.Type.Primary -> colorScheme.green
            MuscleModel.Type.Secondary -> colorScheme.yellow
            MuscleModel.Type.Tertiary -> colorScheme.orange
        }
