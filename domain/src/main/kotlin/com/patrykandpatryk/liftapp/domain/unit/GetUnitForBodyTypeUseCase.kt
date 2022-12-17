package com.patrykandpatryk.liftapp.domain.unit

import com.patrykandpatryk.liftapp.domain.body.BodyType
import com.patrykandpatryk.liftapp.domain.repository.PreferenceRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetUnitForBodyTypeUseCase @Inject constructor(
    private val preferences: PreferenceRepository,
) {

    suspend operator fun invoke(bodyType: BodyType): ValueUnit =
        when (bodyType) {
            BodyType.Weight,
            -> preferences.massUnit.get().first()

            BodyType.Length,
            BodyType.LengthTwoSides,
            -> preferences.shortDistanceUnit.first()

            BodyType.Percentage,
            -> PercentageUnit
        }
}
