package com.patrykandpatryk.liftapp.functionality.database.body

import com.patrykandpatryk.liftapp.domain.body.Body
import com.patrykandpatryk.liftapp.domain.body.BodyRepository
import com.patrykandpatryk.liftapp.domain.body.BodyType
import com.patrykandpatryk.liftapp.domain.model.Name
import javax.inject.Inject

class InsertDefaultBodies @Inject constructor(
    private val repository: BodyRepository,
) {

    private val bodies: List<Body.Insert> = listOf(
        Body.Insert(
            name = Name.Resource("body_body_weight"),
            type = BodyType.Weight,
        ),
        Body.Insert(
            name = Name.Resource("body_fat_percentage"),
            type = BodyType.Percentage,
        ),
        Body.Insert(
            name = Name.Resource("body_muscle_percentage"),
            type = BodyType.Percentage,
        ),
        Body.Insert(
            name = Name.Resource("body_forearm_circumference"),
            type = BodyType.LengthTwoSides,
        ),
        Body.Insert(
            name = Name.Resource("body_bicep_circumference"),
            type = BodyType.LengthTwoSides,
        ),
        Body.Insert(
            name = Name.Resource("body_chest_circumference"),
            type = BodyType.Length,
        ),
        Body.Insert(
            name = Name.Resource("body_ab_circumference"),
            type = BodyType.Length,
        ),
        Body.Insert(
            name = Name.Resource("body_glute_circumference"),
            type = BodyType.Length,
        ),
        Body.Insert(
            name = Name.Resource("body_thigh_circumference"),
            type = BodyType.LengthTwoSides,
        ),
        Body.Insert(
            name = Name.Resource("body_calf_circumference"),
            type = BodyType.LengthTwoSides,
        ),
    )

    suspend operator fun invoke() {
        repository.insertBodies(bodies)
    }
}
