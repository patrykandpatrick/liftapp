package com.patrykandpatrick.liftapp.testing

import com.patrykandpatrick.liftapp.domain.datastore.Preference
import com.patrykandpatrick.liftapp.domain.date.HourFormat
import com.patrykandpatrick.liftapp.domain.model.AllPreferences
import com.patrykandpatrick.liftapp.domain.plan.ActivePlan
import com.patrykandpatrick.liftapp.domain.preference.PreferenceRepository
import com.patrykandpatrick.liftapp.domain.theme.Theme
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.unit.MediumDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.ShortDistanceUnit
import java.time.DayOfWeek
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class TestPreferenceRepository(
    coroutineScope: CoroutineScope = CoroutineScope(EmptyCoroutineContext)
) : PreferenceRepository {

    override val massUnit: Preference<MassUnit> = preference(MassUnit.Kilograms)

    override val longDistanceUnit: Preference<LongDistanceUnit> =
        preference(LongDistanceUnit.Kilometer)

    override val mediumDistanceUnit: Flow<MediumDistanceUnit> =
        longDistanceUnit.get().map { longDistanceUnit ->
            longDistanceUnit.getCorrespondingMediumDistanceUnit()
        }

    override val shortDistanceUnit: Flow<ShortDistanceUnit> =
        longDistanceUnit.get().map { longDistanceUnit ->
            longDistanceUnit.getCorrespondingShortDistanceUnit()
        }

    override val hourFormat: Preference<HourFormat> = preference(HourFormat.H24)

    override val firstDayOfWeek: Preference<DayOfWeek> = preference(DayOfWeek.MONDAY)

    override val theme: Preference<Theme> = preference(Theme.FollowSystem)

    override val is24H: StateFlow<Boolean> =
        hourFormat
            .get()
            .map { hourFormat ->
                when (hourFormat) {
                    HourFormat.H12 -> false
                    HourFormat.Auto,
                    HourFormat.H24 -> true
                }
            }
            .stateIn(coroutineScope, SharingStarted.Eagerly, true)

    override val currentFirstDayOfWeek: StateFlow<DayOfWeek> =
        firstDayOfWeek.get().stateIn(coroutineScope, SharingStarted.Eagerly, DayOfWeek.MONDAY)

    override val goalInfoVisible: Preference<Boolean> = preference(true)

    override val activePlan: Preference<ActivePlan?> = preference(null)

    override val allPreferences: Flow<AllPreferences> =
        combine(
            massUnit.get(),
            longDistanceUnit.get(),
            hourFormat.get(),
            firstDayOfWeek.get(),
            theme.get(),
        ) { massUnit, longDistanceUnit, hourFormat, firstDayOfWeek, theme ->
            AllPreferences(
                massUnit = massUnit,
                longDistanceUnit = longDistanceUnit,
                mediumDistanceUnit = longDistanceUnit.getCorrespondingMediumDistanceUnit(),
                shortDistanceUnit = longDistanceUnit.getCorrespondingShortDistanceUnit(),
                hourFormat = hourFormat,
                firstDayOfWeek = firstDayOfWeek,
                theme = theme,
            )
        }

    private fun <T> preference(defaultValue: T): Preference<T> =
        object : Preference<T> {

            private val impl = MutableStateFlow(defaultValue)

            override fun get(): Flow<T> = impl

            override suspend fun set(value: T) {
                impl.value = value
            }

            override suspend fun update(function: (T) -> T) {
                impl.update(function)
            }
        }
}
