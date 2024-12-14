package com.patrykandpatryk.liftapp.testing

import com.patrykandpatrick.opto.domain.Preference
import com.patrykandpatryk.liftapp.domain.date.HourFormat
import com.patrykandpatryk.liftapp.domain.model.AllPreferences
import com.patrykandpatryk.liftapp.domain.preference.PreferenceRepository
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.unit.MediumDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.ShortDistanceUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class TestPreferenceRepository : PreferenceRepository {

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

    override val is24H: Flow<Boolean> =
        hourFormat.get().map { hourFormat ->
            when (hourFormat) {
                HourFormat.H12 -> false
                HourFormat.Auto,
                HourFormat.H24 -> true
            }
        }

    override val goalInfoVisible: Preference<Boolean> = preference(true)

    override val allPreferences: Flow<AllPreferences> =
        combine(massUnit.get(), longDistanceUnit.get(), hourFormat.get()) {
            massUnit,
            longDistanceUnit,
            hourFormat ->
            AllPreferences(
                massUnit = massUnit,
                longDistanceUnit = longDistanceUnit,
                mediumDistanceUnit = longDistanceUnit.getCorrespondingMediumDistanceUnit(),
                shortDistanceUnit = longDistanceUnit.getCorrespondingShortDistanceUnit(),
                hourFormat = hourFormat,
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
