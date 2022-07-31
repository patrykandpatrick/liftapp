package com.patrykandpatryk.liftapp.core.validation

import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.domain.message.LocalizableMessage
import com.patrykandpatryk.liftapp.domain.validation.Validatable
import com.patrykandpatryk.liftapp.domain.validation.Validator
import javax.inject.Qualifier

object HigherThanZeroValidator : Validator<Float> {

    override fun validate(value: Float): Validatable<Float> =
        if (value > 0f) Validatable.Valid(value)
        else Validatable.Invalid(
            value = value,
            message = LocalizableMessage(
                messageResId = R.string.error_must_be_higher_than_zero,
            ),
        )
}

@Qualifier
annotation class HigherThanZero
