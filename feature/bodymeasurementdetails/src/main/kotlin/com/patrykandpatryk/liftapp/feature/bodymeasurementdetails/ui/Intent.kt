package com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.ui

sealed class Intent {
    class ExpandItem(val id: Long) : Intent()
    class DeleteBodyMeasurementEntry(val id: Long) : Intent()
    class NewEntry(val bodyMeasurementID: Long, val bodyMeasurementEntryID: Long? = null) : Intent()
    data object DismissNewEntry : Intent()
}
