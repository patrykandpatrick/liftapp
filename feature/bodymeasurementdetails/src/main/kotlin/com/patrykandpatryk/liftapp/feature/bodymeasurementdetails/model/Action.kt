package com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.model

sealed class Action {
    class ExpandItem(val id: Long) : Action()

    class DeleteBodyMeasurementEntry(val id: Long) : Action()

    data object PopBackStack : Action()

    data object AddBodyMeasurement : Action()

    data class EditBodyMeasurement(val bodyEntryMeasurementId: Long) : Action()
}
