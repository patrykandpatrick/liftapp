package com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.model

import com.patrykandpatryk.liftapp.domain.date.DateInterval

sealed class Action {
    class DeleteBodyMeasurementEntry(val id: Long) : Action()

    data object PopBackStack : Action()

    data object AddBodyMeasurement : Action()

    data class EditBodyMeasurement(val bodyEntryMeasurementId: Long) : Action()

    data class SetDateInterval(val dateInterval: DateInterval) : Action()

    data object IncrementDateInterval : Action()

    data object DecrementDateInterval : Action()
}
