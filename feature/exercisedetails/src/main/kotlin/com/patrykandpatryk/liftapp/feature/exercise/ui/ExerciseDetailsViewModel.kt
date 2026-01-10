package com.patrykandpatryk.liftapp.feature.exercise.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.navigation.data.ExerciseDetailsRouteData
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.columnSeries
import com.patrykandpatryk.liftapp.core.chart.ExtraStoreKey
import com.patrykandpatryk.liftapp.core.logging.LogPublisher
import com.patrykandpatryk.liftapp.core.logging.UiLogger
import com.patrykandpatryk.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatryk.liftapp.domain.date.DateInterval
import com.patrykandpatryk.liftapp.domain.exercise.DeleteExerciseUseCase
import com.patrykandpatryk.liftapp.domain.exercise.GetExerciseUseCase
import com.patrykandpatryk.liftapp.domain.exerciseset.ExerciseSetGroup
import com.patrykandpatryk.liftapp.domain.exerciseset.ExerciseSummaryType
import com.patrykandpatryk.liftapp.domain.exerciseset.GetExerciseSetsUseCase
import com.patrykandpatryk.liftapp.domain.exerciseset.getSummaryTypes
import com.patrykandpatryk.liftapp.domain.exerciseset.invoke
import com.patrykandpatryk.liftapp.domain.exerciseset.summary.ExerciseSetToChartEntryMapper
import com.patrykandpatryk.liftapp.domain.exerciseset.summary.GetValueUnitForExerciseSetSummaryUseCase
import com.patrykandpatryk.liftapp.domain.model.Loadable
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.feature.exercise.model.Action
import com.patrykandpatryk.liftapp.feature.exercise.model.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@HiltViewModel
class ExerciseDetailsViewModel
@Inject
constructor(
    private val routeData: ExerciseDetailsRouteData,
    private val logger: UiLogger,
    getExercise: GetExerciseUseCase,
    getExerciseSetsUseCase: GetExerciseSetsUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val deleteExercise: DeleteExerciseUseCase,
    private val stringProvider: StringProvider,
    private val navigationCommander: NavigationCommander,
    private val exerciseSetToChartEntryMapper: ExerciseSetToChartEntryMapper,
    private val getValueUnitForExerciseSetSummaryUseCase: GetValueUnitForExerciseSetSummaryUseCase,
) : ViewModel(), LogPublisher by logger {

    private val cartesianChartModelProducer = CartesianChartModelProducer()

    private val dateIntervalOptions: List<DateInterval> = DateInterval.exerciseOptions

    private val dateInterval =
        savedStateHandle.getMutableStateFlow(DATE_INTERVAL_KEY, dateIntervalOptions.first())

    private val summaryType =
        savedStateHandle.getMutableStateFlow<ExerciseSummaryType?>(SUMMARY_TYPE_KEY, null)

    private val exerciseSets =
        dateInterval.flatMapLatest { dateInterval ->
            getExerciseSetsUseCase(routeData.exerciseID, dateInterval)
        }

    val screenState: StateFlow<Loadable<ScreenState>> =
        combine(
                getExercise(routeData.exerciseID),
                savedStateHandle.getStateFlow(SHOW_DELETE_DIALOG_KEY, false),
                exerciseSets,
                dateInterval,
                summaryType,
            ) { exercise, showDeleteDialog, exerciseSetGroups, dateInterval, summaryType ->
                if (exercise == null) {
                    error("Exercise with id ${routeData.exerciseID} not found, or deleted.")
                } else {
                    val summaryTypes = exercise.exerciseType.getSummaryTypes()
                    val summaryType = summaryType ?: summaryTypes.first()
                    updateChartModel(exerciseSetGroups, dateInterval, summaryType)

                    ScreenState(
                        name = stringProvider.getResolvedName(exercise.name),
                        showDeleteDialog = showDeleteDialog,
                        primaryMuscles = exercise.primaryMuscles,
                        secondaryMuscles = exercise.secondaryMuscles,
                        tertiaryMuscles = exercise.tertiaryMuscles,
                        exerciseSetGroups = exerciseSetGroups,
                        cartesianChartModelProducer = cartesianChartModelProducer,
                        dateInterval = dateInterval,
                        dateIntervalOptions = dateIntervalOptions,
                        summaryType = summaryType,
                        summaryTypeOptions = summaryTypes,
                    )
                }
            }
            .toLoadableStateFlow(viewModelScope)

    private suspend fun updateChartModel(
        exerciseSetGroups: List<ExerciseSetGroup>,
        dateInterval: DateInterval,
        summaryType: ExerciseSummaryType,
    ) {
        val valueUnit = getValueUnitForExerciseSetSummaryUseCase(summaryType)
        val entries = exerciseSetToChartEntryMapper(summaryType, exerciseSetGroups)
        cartesianChartModelProducer.runTransaction {
            extras {
                it[ExtraStoreKey.MinX] =
                    dateInterval.periodStartTime.toLocalDate().toEpochDay().toDouble()
                it[ExtraStoreKey.MaxX] =
                    dateInterval.periodEndTime.toLocalDate().toEpochDay().toDouble()
                it[ExtraStoreKey.DateInterval] = dateInterval
                if (valueUnit != null) {
                    it[ExtraStoreKey.ValueUnit] = valueUnit
                }
            }
            if (entries.isNotEmpty()) {
                columnSeries { entries.forEach { (x, y) -> series(x, y) } }
            }
        }
    }

    fun handleIntent(action: Action) {
        when (action) {
            Action.Delete -> deleteExercise()
            Action.Edit -> sendEditExerciseEvent()
            Action.HideDeleteDialog -> setShowDeleteDialog(false)
            Action.ShowDeleteDialog -> setShowDeleteDialog(true)
            Action.PopBackStack -> popBackStack()
            is Action.SetDateInterval -> setDateInterval(action.dateInterval)
            is Action.SetSummaryType -> setSummaryType(action.summaryType)
            Action.DecrementDateInterval -> decrementDateInterval()
            Action.IncrementDateInterval -> incrementDateInterval()
        }
    }

    private fun sendEditExerciseEvent() {
        viewModelScope.launch {
            navigationCommander.navigateTo(Routes.Exercise.edit(routeData.exerciseID))
        }
    }

    private fun setShowDeleteDialog(show: Boolean) {
        savedStateHandle[SHOW_DELETE_DIALOG_KEY] = show
    }

    private fun deleteExercise() {
        setShowDeleteDialog(false)
        viewModelScope.launch { deleteExercise(routeData.exerciseID) }
    }

    private fun popBackStack() {
        viewModelScope.launch { navigationCommander.popBackStack() }
    }

    private fun setDateInterval(dateInterval: DateInterval) {
        this.dateInterval.value = dateInterval
    }

    private fun setSummaryType(summaryType: ExerciseSummaryType) {
        this.summaryType.value = summaryType
    }

    private fun incrementDateInterval() {
        setDateInterval(dateInterval.value.increment())
    }

    private fun decrementDateInterval() {
        setDateInterval(dateInterval.value.decrement())
    }

    companion object {
        private const val SHOW_DELETE_DIALOG_KEY = "showDeleteDialog"
        private const val DATE_INTERVAL_KEY = "dateInterval"
        private const val SUMMARY_TYPE_KEY = "summaryType"
    }
}
