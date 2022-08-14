package com.patrykandpatryk.liftapp.domain.body

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetBodiesWithLatestEntriesUseCase @Inject constructor(
    private val repository: BodyRepository,
) {

    operator fun invoke(): Flow<List<BodyWithLatestEntry>> =
        repository.getAllBodies()
}
