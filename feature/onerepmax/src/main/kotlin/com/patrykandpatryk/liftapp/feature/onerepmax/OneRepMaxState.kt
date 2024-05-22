package com.patrykandpatryk.liftapp.feature.onerepmax

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import kotlinx.collections.immutable.ImmutableList

@Stable
interface OneRepMaxState {
    val reps: State<String>
    val mass: State<String>
    val oneRepMax: State<Float>
    val massUnit: State<MassUnit?>
    val history: State<ImmutableList<HistoryEntryModel>>

    fun updateReps(reps: String)
    fun updateMass(mass: String)
}
