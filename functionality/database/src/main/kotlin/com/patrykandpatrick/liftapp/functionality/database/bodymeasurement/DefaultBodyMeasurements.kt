package com.patrykandpatrick.liftapp.functionality.database.bodymeasurement

import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurement
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementType
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.functionality.database.string.BodyMeasurementStringResource

/**
 * The built-in body measurement catalog, in insertion order. On a fresh database the rows receive
 * the autoincrement IDs 1 through 10; the legacy migration maps the published app's measurement IDs
 * (1–5 and 7–11) onto the same sequence.
 */
object DefaultBodyMeasurements {

    val bodyMeasurements: List<BodyMeasurement.Insert> =
        listOf(
            BodyMeasurement.Insert(
                name = Name.Resource(BodyMeasurementStringResource.BodyWeight),
                type = BodyMeasurementType.Weight,
            ),
            BodyMeasurement.Insert(
                name = Name.Resource(BodyMeasurementStringResource.FatPercentage),
                type = BodyMeasurementType.Percentage,
            ),
            BodyMeasurement.Insert(
                name = Name.Resource(BodyMeasurementStringResource.MusclePercentage),
                type = BodyMeasurementType.Percentage,
            ),
            BodyMeasurement.Insert(
                name = Name.Resource(BodyMeasurementStringResource.ForearmCircumference),
                type = BodyMeasurementType.LengthTwoSides,
            ),
            BodyMeasurement.Insert(
                name = Name.Resource(BodyMeasurementStringResource.BicepCircumference),
                type = BodyMeasurementType.LengthTwoSides,
            ),
            BodyMeasurement.Insert(
                name = Name.Resource(BodyMeasurementStringResource.ChestCircumference),
                type = BodyMeasurementType.Length,
            ),
            BodyMeasurement.Insert(
                name = Name.Resource(BodyMeasurementStringResource.AbCircumference),
                type = BodyMeasurementType.Length,
            ),
            BodyMeasurement.Insert(
                name = Name.Resource(BodyMeasurementStringResource.GluteCircumference),
                type = BodyMeasurementType.Length,
            ),
            BodyMeasurement.Insert(
                name = Name.Resource(BodyMeasurementStringResource.ThighCircumference),
                type = BodyMeasurementType.LengthTwoSides,
            ),
            BodyMeasurement.Insert(
                name = Name.Resource(BodyMeasurementStringResource.CalfCircumference),
                type = BodyMeasurementType.LengthTwoSides,
            ),
        )
}
