package com.patrykandpatrick.liftapp.shortcut

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.patrykandpatrick.liftapp.R
import com.patrykandpatrick.liftapp.core.R as CoreR
import com.patrykandpatrick.liftapp.core.deeplink.DeepLink
import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementRepository
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementType
import com.patrykandpatrick.liftapp.domain.di.DefaultDispatcher
import com.patrykandpatrick.liftapp.domain.plan.GetPlanItemContract
import com.patrykandpatrick.liftapp.domain.plan.Plan
import com.patrykandpatrick.liftapp.domain.workout.GetActiveWorkoutsUseCase
import com.patrykandpatrick.liftapp.feature.home.currentDateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Keeps the launcher's shortcuts in step with what the app can currently do, as the published app
 * did: recording body weight is always available, while starting a workout only appears once there
 * is a workout to start.
 *
 * Both shortcuts open a `liftapp:` deep link, so the destination they name is resolved here rather
 * than when the user taps one. Every input is a flow, so a shortcut that goes stale is republished.
 */
@Singleton
class LauncherShortcuts
@Inject
constructor(
    private val context: Context,
    private val bodyMeasurementRepository: BodyMeasurementRepository,
    private val getActiveWorkouts: GetActiveWorkoutsUseCase,
    private val getPlanItem: GetPlanItemContract,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) {

    private val coroutineScope = CoroutineScope(dispatcher + SupervisorJob())

    fun keepUpToDate() {
        coroutineScope.launch {
            bodyMeasurementRepository
                .getBodyMeasurementsWithLatestEntries()
                .map { measurements ->
                    measurements.firstOrNull { it.type == BodyMeasurementType.Weight }?.id
                }
                .distinctUntilChanged()
                .collect { bodyWeightID -> publishAddWeight(bodyWeightID) }
        }

        coroutineScope.launch {
            val currentPlanItem =
                currentDateFlow().flatMapLatest { date -> getPlanItem.getPlanItem(date) }
            combine(getActiveWorkouts(), currentPlanItem) { activeWorkouts, planItem ->
                    val activeWorkout = activeWorkouts.firstOrNull()
                    when {
                        activeWorkout != null -> activeWorkout.routineID to activeWorkout.id
                        planItem is Plan.Item.Routine -> planItem.routine.id to ID_NOT_SET
                        else -> null
                    }
                }
                .distinctUntilChanged()
                .collect { target -> publishStartWorkout(target) }
        }
    }

    private fun publishAddWeight(bodyWeightID: Long?) {
        if (bodyWeightID == null) {
            ShortcutManagerCompat.removeDynamicShortcuts(context, listOf(ADD_WEIGHT_ID))
            return
        }

        ShortcutManagerCompat.pushDynamicShortcut(
            context,
            shortcut(
                id = ADD_WEIGHT_ID,
                label = CoreR.string.shortcut_add_weight,
                icon = R.drawable.ic_shortcut_add_weight,
                uri = DeepLink.NewBodyMeasurementRoute.createLink(bodyWeightID, ID_NOT_SET),
            ),
        )
    }

    private fun publishStartWorkout(target: Pair<Long, Long>?) {
        if (target == null) {
            ShortcutManagerCompat.removeDynamicShortcuts(context, listOf(START_WORKOUT_ID))
            return
        }

        val (routineID, workoutID) = target
        ShortcutManagerCompat.pushDynamicShortcut(
            context,
            shortcut(
                id = START_WORKOUT_ID,
                label = CoreR.string.shortcut_start_workout,
                icon = R.drawable.ic_shortcut_start_workout,
                uri = DeepLink.WorkoutRoute.createLink(routineID, workoutID),
            ),
        )
    }

    private fun shortcut(
        id: String,
        label: Int,
        icon: Int,
        uri: android.net.Uri,
    ): ShortcutInfoCompat =
        ShortcutInfoCompat.Builder(context, id)
            .setShortLabel(context.getString(label))
            .setLongLabel(context.getString(label))
            .setIcon(IconCompat.createWithResource(context, icon))
            .setIntent(
                Intent(Intent.ACTION_VIEW, uri).apply {
                    setPackage(context.packageName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
            )
            .build()

    private companion object {
        const val START_WORKOUT_ID = "start_workout"
        const val ADD_WEIGHT_ID = "add_weight"
    }
}
