package com.patrykandpatryk.liftapp.core.validation

import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.domain.validation.Validatable
import com.patrykandpatryk.liftapp.domain.validation.Validator
import javax.inject.Inject
import javax.inject.Qualifier

class HigherThanZeroValidator @Inject constructor(
    private val stringProvider: StringProvider,
) : Validator<Float> {

    override fun validate(value: Float): Validatable<Float> =
        if (value > 0f) Validatable.Valid(value)
        else Validatable.Invalid(
            value = value,
            message = stringProvider.errorMustBeHigherThanZero,
        )
}

@Qualifier
annotation class HigherThanZero
