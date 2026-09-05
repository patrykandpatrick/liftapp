package com.patrykandpatrick.liftapp.feature.musclevolume.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatrick.liftapp.domain.date.DateInterval
import com.patrykandpatrick.liftapp.domain.date.currentDateFlow
import com.patrykandpatrick.liftapp.domain.model.Loadable
import com.patrykandpatrick.liftapp.domain.muscle.GetMuscleVolumeUseCase
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.domain.preference.PreferenceRepository
import com.patrykandpatrick.liftapp.feature.musclevolume.model.Action
import com.patrykandpatrick.liftapp.feature.musclevolume.model.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel
class MuscleVolumeViewModel
@Inject
constructor(
    getMuscleVolume: GetMuscleVolumeUseCase,
    savedStateHandle: SavedStateHandle,
    coroutineScope: CoroutineScope,
    private val navigationCommander: NavigationCommander,
    preferenceRepository: PreferenceRepository,
) : ViewModel(coroutineScope) {

    private val dateIntervalOptions: List<DateInterval> =
        DateInterval.volumeOptions(preferenceRepository.currentFirstDayOfWeek.value)

    private val dateInterval =
        savedStateHandle.getMutableStateFlow(DATE_INTERVAL_KEY, dateIntervalOptions.first())

    val screenState: StateFlow<Loadable<ScreenState>> =
        combine(currentDateFlow(), dateInterval, ::Pair)
            .flatMapLatest { (_, interval) ->
                getMuscleVolume(interval).map { muscleVolume ->
                    ScreenState(
                        muscleVolume = muscleVolume,
                        dateInterval = interval,
                        dateIntervalOptions = dateIntervalOptions,
                    )
                }
            }
            .toLoadableStateFlow(viewModelScope)

    fun onAction(action: Action) {
        when (action) {
            Action.PopBackStack -> popBackStack()
            is Action.SetDateInterval -> dateInterval.value = action.dateInterval
            Action.IncrementDateInterval -> dateInterval.value = dateInterval.value.increment()
            Action.DecrementDateInterval -> dateInterval.value = dateInterval.value.decrement()
        }
    }

    private fun popBackStack() {
        viewModelScope.launch { navigationCommander.popBackStack() }
    }

    private companion object {
        const val DATE_INTERVAL_KEY = "dateInterval"
    }
}
