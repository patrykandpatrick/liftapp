package com.patrykandpatryk.liftapp.bodyrecord.ui

import com.patrykandpatryk.liftapp.domain.measurement.MeasurementRepository
import com.patrykandpatryk.liftapp.domain.message.LocalizableMessage
import com.patrykandpatryk.liftapp.domain.validation.Validatable
import com.patrykandpatryk.liftapp.core.R
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class GetBodyModelUseCase @Inject constructor(
    private val repository: MeasurementRepository,
) {

    suspend operator fun invoke(id: Long): BodyModel {
        val measurement = repository.getMeasurement(id).first()
        return BodyModel.Insert(
            name = measurement.name,
            values = List(size = measurement.type.fields) {
                Validatable.Invalid(
                    value = "",
                    message = LocalizableMessage(R.string.error_must_be_higher_than_zero),
                )
            },
        )
    }
}
