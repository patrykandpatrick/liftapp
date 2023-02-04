package com.patrykandpatryk.liftapp.domain.body

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBodyItemsUseCase @Inject constructor(
    private val bodyRepository: BodyRepository,
) {

    operator fun invoke(): Flow<List<BodyItem>> =
        bodyRepository.getBodyItems()
}
