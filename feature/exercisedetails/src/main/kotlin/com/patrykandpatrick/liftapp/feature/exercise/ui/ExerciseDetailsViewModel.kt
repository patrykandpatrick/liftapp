package com.patrykandpatrick.liftapp.feature.exercise.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.core.chart.ExtraStoreKey
import com.patrykandpatrick.liftapp.core.logging.LogPublisher
import com.patrykandpatrick.liftapp.core.logging.UiLogger
import com.patrykandpatrick.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatrick.liftapp.domain.date.DateInterval
import com.patrykandpatrick.liftapp.domain.exercise.DeleteExerciseUseCase
import com.patrykandpatrick.liftapp.domain.exercise.GetExerciseUseCase
import com.patrykandpatrick.liftapp.domain.exercise.invoke
import com.patrykandpatrick.liftapp.domain.exerciseset.ExerciseSetGroup
import com.patrykandpatrick.liftapp.domain.exerciseset.ExerciseSummaryType
import com.patrykandpatrick.liftapp.domain.exerciseset.GetExerciseSetsUseCase
import com.patrykandpatrick.liftapp.domain.exerciseset.GetExerciseStatisticsUseCase
import com.patrykandpatrick.liftapp.domain.exerciseset.getSummaryTypes
import com.patrykandpatrick.liftapp.domain.exerciseset.invoke
import com.patrykandpatrick.liftapp.domain.exerciseset.summary.ExerciseSetToChartEntryMapper
import com.patrykandpatrick.liftapp.domain.exerciseset.summary.GetValueUnitForExerciseSetSummaryUseCase
import com.patrykandpatrick.liftapp.domain.model.Loadable
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.domain.preference.PreferenceRepository
import com.patrykandpatrick.liftapp.domain.text.StringProvider
import com.patrykandpatrick.liftapp.feature.exercise.model.Action
import com.patrykandpatrick.liftapp.feature.exercise.model.ScreenState
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.navigation.data.ExerciseDetailsRouteData
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.columnModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    private val getExerciseStatistics: GetExerciseStatisticsUseCase,
    preferenceRepository: PreferenceRepository,
) : ViewModel(), LogPublisher by logger {

    private val cartesianChartModelProducer = CartesianChartModelProducer()

    private val dateIntervalOptions: List<DateInterval> =
        DateInterval.exerciseOptions(preferenceRepository.currentFirstDayOfWeek.value)

    private val dateInterval =
        savedStateHandle.getMutableStateFlow(DATE_INTERVAL_KEY, dateIntervalOptions.first())

    private val summaryType =
        savedStateHandle.getMutableStateFlow<ExerciseSummaryType?>(SUMMARY_TYPE_KEY, null)

    private val exerciseSets = dateInterval.flatMapLatest { dateInterval ->
        getExerciseSetsUseCase(routeData.exerciseID, dateInterval)
    }

    private val exerciseSetState =
        combine(
            exerciseSets,
            getExerciseSetsUseCase.hasExerciseSets(routeData.exerciseID),
        ) { exerciseSetGroups, hasExerciseHistory ->
            exerciseSetGroups to hasExerciseHistory
        }

    val screenState: StateFlow<Loadable<ScreenState>> =
        combine(
                getExercise(routeData.exerciseID),
                savedStateHandle.getStateFlow(SHOW_DELETE_DIALOG_KEY, false),
                exerciseSetState,
                dateInterval,
                summaryType,
            ) { exercise, showDeleteDialog, exerciseSetState, dateInterval, summaryType ->
                if (exercise == null) {
                    error("Exercise with id ${routeData.exerciseID} not found, or deleted.")
                } else {
                    val (exerciseSetGroups, hasExerciseHistory) = exerciseSetState
                    val summaryTypes = exercise.exerciseType.getSummaryTypes()
                    val summaryType = summaryType ?: summaryTypes.first()
                    updateChartModel(exerciseSetGroups, dateInterval, summaryType)
                    val exerciseStatistics =
                        getExerciseStatistics(exercise.exerciseType, exerciseSetGroups)

                    ScreenState(
                        name = stringProvider.getResolvedName(exercise.name),
                        showDeleteDialog = showDeleteDialog,
                        primaryMuscles = exercise.primaryMuscles,
                        secondaryMuscles = exercise.secondaryMuscles,
                        tertiaryMuscles = exercise.tertiaryMuscles,
                        hasExerciseHistory = hasExerciseHistory,
                        exerciseSetGroups = exerciseSetGroups,
                        exerciseStatistics = exerciseStatistics,
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
                columnModel { entries.forEach { (x, y) -> series(x, y) } }
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
        viewModelScope.launch {
            navigationCommander.popBackStack()
            withContext(NonCancellable) { deleteExercise(routeData.exerciseID) }
        }
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
