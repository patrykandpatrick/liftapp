package com.patrykandpatryk.liftapp.core.validation

import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.domain.text.getErrorCannotBeEmpty
import com.patrykandpatryk.liftapp.domain.validation.Validatable
import com.patrykandpatryk.liftapp.domain.validation.Validator
import com.patrykandpatryk.liftapp.domain.validation.toInvalid
import com.patrykandpatryk.liftapp.domain.validation.toValid
import javax.inject.Inject

class NonEmptyCollectionValidator<T, C : Collection<T>>
@Inject
constructor(private val stringProvider: StringProvider) : Validator<C> {

    override fun validate(value: C): Validatable<C> =
        if (value.isEmpty()) {
            value.toInvalid(stringProvider.getErrorCannotBeEmpty { list })
        } else {
            value.toValid()
        }
}
