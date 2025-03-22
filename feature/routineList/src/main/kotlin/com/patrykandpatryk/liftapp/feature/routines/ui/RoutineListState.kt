package com.patrykandpatryk.liftapp.feature.routines.ui

import androidx.compose.runtime.Immutable
import com.patrykandpatryk.liftapp.feature.routines.model.RoutineItem

@Immutable
data class RoutineListState(val routines: List<RoutineItem>, val isPickingRoutine: Boolean) :
    List<RoutineItem> by routines
