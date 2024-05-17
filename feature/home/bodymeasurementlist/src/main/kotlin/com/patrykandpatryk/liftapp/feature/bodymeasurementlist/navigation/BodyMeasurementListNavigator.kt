package com.patrykandpatryk.liftapp.feature.bodymeasurementlist.navigation

import androidx.compose.runtime.Stable

@Stable
interface BodyMeasurementListNavigator {
    fun bodyMeasurementDetails(bodyMeasurementID: Long)
}
