package com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.navigation

import androidx.compose.runtime.Stable

@Stable
interface BodyMeasurementDetailsNavigator {
    fun back()

    fun newBodyMeasurement(bodyMeasurementId: Long, bodyEntryMeasurementId: Long? = null)
}
