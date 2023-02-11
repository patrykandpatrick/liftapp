package com.patrykandpatryk.liftapp.functionality.database.string

import com.patrykandpatryk.liftapp.domain.model.StringResource
import com.patrykandpatryk.liftapp.functionality.database.R
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("body")
enum class BodyMeasurementStringResource(override val resourceId: Int) : StringResource {
    BodyWeight(R.string.body_body_weight),
    FatPercentage(R.string.body_fat_percentage),
    MusclePercentage(R.string.body_muscle_percentage),
    ForearmCircumference(R.string.body_forearm_circumference),
    BicepCircumference(R.string.body_bicep_circumference),
    ChestCircumference(R.string.body_chest_circumference),
    AbCircumference(R.string.body_ab_circumference),
    GluteCircumference(R.string.body_glute_circumference),
    ThighCircumference(R.string.body_thigh_circumference),
    CalfCircumference(R.string.body_calf_circumference),
}
