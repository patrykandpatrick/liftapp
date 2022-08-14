package com.patrykandpatryk.liftapp.feature.body.ui

import com.patrykandpatryk.liftapp.domain.body.BodyRepository
import com.patrykandpatryk.liftapp.domain.body.BodyWithLatestEntry
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetBodyItemsUseCase @Inject constructor(
    private val bodyRepository: BodyRepository,
    private val mapper: Mapper<BodyWithLatestEntry, BodyItem>,
) {

    operator fun invoke(): Flow<List<BodyItem>> =
        bodyRepository
            .getAllBodies()
            .map { mapper(it) }
}
