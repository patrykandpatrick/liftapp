package com.patrykandpatryk.liftapp.domain.unit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("speed")
enum class SpeedUnit : RatioUnit<LongDistanceUnit, DurationUnit> {
    @SerialName("kilometers_per_hour")
    KilometersPerHour {
        override val dividend: LongDistanceUnit = LongDistanceUnit.Kilometer

        override val hasLeadingSpace: Boolean = true
        override val isMetric: Boolean = true
    },
    @SerialName("miles_per_hour")
    MilesPerHour {
        override val dividend: LongDistanceUnit = LongDistanceUnit.Mile
        override val hasLeadingSpace: Boolean = true
        override val isMetric: Boolean = false
    };

    override val divisor: DurationUnit = DurationUnit
}
