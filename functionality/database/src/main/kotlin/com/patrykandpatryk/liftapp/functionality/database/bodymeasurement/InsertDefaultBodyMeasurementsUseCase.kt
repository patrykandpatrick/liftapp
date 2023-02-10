package com.patrykandpatryk.liftapp.functionality.database.body

import com.patrykandpatryk.liftapp.domain.body.Body
import com.patrykandpatryk.liftapp.domain.body.BodyRepository
import com.patrykandpatryk.liftapp.domain.body.BodyType
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.functionality.database.string.BodyStringResource
import javax.inject.Inject

class InsertDefaultBodies @Inject constructor(
    private val repository: BodyRepository,
) {

    private val bodies: List<Body.Insert> = listOf(
        Body.Insert(
            name = Name.Resource(BodyStringResource.BodyWeight),
            type = BodyType.Weight,
        ),
        Body.Insert(
            name = Name.Resource(BodyStringResource.FatPercentage),
            type = BodyType.Percentage,
        ),
        Body.Insert(
            name = Name.Resource(BodyStringResource.MusclePercentage),
            type = BodyType.Percentage,
        ),
        Body.Insert(
            name = Name.Resource(BodyStringResource.ForearmCircumference),
            type = BodyType.LengthTwoSides,
        ),
        Body.Insert(
            name = Name.Resource(BodyStringResource.BicepCircumference),
            type = BodyType.LengthTwoSides,
        ),
        Body.Insert(
            name = Name.Resource(BodyStringResource.ChestCircumference),
            type = BodyType.Length,
        ),
        Body.Insert(
            name = Name.Resource(BodyStringResource.AbCircumference),
            type = BodyType.Length,
        ),
        Body.Insert(
            name = Name.Resource(BodyStringResource.GluteCircumference),
            type = BodyType.Length,
        ),
        Body.Insert(
            name = Name.Resource(BodyStringResource.ThighCircumference),
            type = BodyType.LengthTwoSides,
        ),
        Body.Insert(
            name = Name.Resource(BodyStringResource.CalfCircumference),
            type = BodyType.LengthTwoSides,
        ),
    )

    suspend operator fun invoke() {
        repository.insertBodies(bodies)
    }
}
