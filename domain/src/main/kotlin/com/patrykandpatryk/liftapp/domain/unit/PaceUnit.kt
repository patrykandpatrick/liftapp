package com.patrykandpatryk.liftapp.domain.unit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("pace")
enum class PaceUnit : RatioUnit<DurationUnit, LongDistanceUnit> {
    @SerialName("duration_per_km")
    DurationPerKilometer {
        override val divisor: LongDistanceUnit = LongDistanceUnit.Kilometer
        override val hasLeadingSpace: Boolean = true
        override val isMetric: Boolean = true
    },
    @SerialName("duration_per_mile")
    DurationPerMile {
        override val divisor: LongDistanceUnit = LongDistanceUnit.Mile
        override val hasLeadingSpace: Boolean = true
        override val isMetric: Boolean = false
    };

    override val dividend: DurationUnit = DurationUnit
}
