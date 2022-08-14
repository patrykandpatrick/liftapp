package com.patrykandpatryk.liftapp.domain.body

import javax.inject.Inject

class InsertBodyUseCase @Inject constructor(
    private val repository: BodyRepository,
) {

    suspend operator fun invoke(body: Body.Insert) {
        repository.insertBody(body)
    }
}
