package com.patrykandpatrick.liftapp.feature.journal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.domain.workout.DeleteWorkoutUseCase
import com.patrykandpatrick.liftapp.domain.workout.GetPastWorkoutPageContract
import com.patrykandpatrick.liftapp.feature.journal.model.Action
import com.patrykandpatrick.liftapp.feature.journal.model.PastWorkoutPagingSource
import com.patrykandpatrick.liftapp.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class JournalViewModel
@Inject
constructor(
    private val getPastWorkoutPage: GetPastWorkoutPageContract,
    private val deleteWorkout: DeleteWorkoutUseCase,
    private val navigationCommander: NavigationCommander,
) : ViewModel() {

    private val pagingGeneration = MutableStateFlow(0)

    val workouts =
        pagingGeneration
            .flatMapLatest {
                Pager(PagingConfig(pageSize = PAGE_SIZE)) {
                        PastWorkoutPagingSource(getPastWorkoutPage, PAGE_SIZE)
                    }
                    .flow
            }
            .cachedIn(viewModelScope)

    fun onAction(action: Action) {
        when (action) {
            is Action.GoToWorkout -> navigate(Routes.Workout.edit(action.workoutID))
            is Action.DeleteWorkout -> delete(action.workoutID)
            is Action.PopBackStack -> viewModelScope.launch { navigationCommander.popBackStack() }
        }
    }

    /**
     * The dashboard only offers the journal while there is something in it, so deleting the last
     * workout leaves a screen with nothing to show and no way back to it. It steps back instead, to
     * the dashboard the section has just disappeared from.
     */
    private fun delete(workoutID: Long) {
        viewModelScope.launch {
            deleteWorkout(workoutID)
            if (getPastWorkoutPage.getPastWorkoutPage(limit = 1, offset = 0).isEmpty()) {
                navigationCommander.popBackStack()
            } else {
                pagingGeneration.update(Int::inc)
            }
        }
    }

    private fun navigate(route: Any) {
        viewModelScope.launch { navigationCommander.navigateTo(route) }
    }

    private companion object {
        /** The page the published journal read at a time. */
        const val PAGE_SIZE = 8
    }
}
