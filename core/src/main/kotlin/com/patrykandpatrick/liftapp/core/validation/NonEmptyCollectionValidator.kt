package com.patrykandpatrick.liftapp.core.validation

import com.patrykandpatrick.liftapp.domain.text.StringProvider
import com.patrykandpatrick.liftapp.domain.text.getErrorCannotBeEmpty
import com.patrykandpatrick.liftapp.domain.validation.Validatable
import com.patrykandpatrick.liftapp.domain.validation.Validator
import com.patrykandpatrick.liftapp.domain.validation.toInvalid
import com.patrykandpatrick.liftapp.domain.validation.toValid
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
