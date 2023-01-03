package com.patrykandpatryk.liftapp.core.validation

import com.patrykandpatryk.liftapp.domain.Constants
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.domain.text.getErrorCannotBeEmpty
import com.patrykandpatryk.liftapp.domain.validation.Validatable
import com.patrykandpatryk.liftapp.domain.validation.Validator
import com.patrykandpatryk.liftapp.domain.validation.toInvalid
import com.patrykandpatryk.liftapp.domain.validation.toValid
import javax.inject.Inject

class NameValidator @Inject constructor(
    private val stringProvider: StringProvider,
) : Validator<String> {

    override fun validate(value: String): Validatable<String> {
        return when {
            value.length > Constants.Input.NAME_MAX_CHARS -> value.toInvalid(
                message = stringProvider.getErrorNameTooLong(actual = value.length),
            )
            value.isBlank() -> value.toInvalid(
                message = stringProvider.getErrorCannotBeEmpty { name },
            )
            else -> value.toValid()
        }
    }
}
