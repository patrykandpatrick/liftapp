package com.patrykandpatryk.liftapp.domain.unit

import com.patrykandpatryk.liftapp.domain.preference.PreferenceRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetPreferredMassUnitUseCase
@Inject
constructor(private val preferenceRepository: PreferenceRepository) {
    operator fun invoke(): Flow<MassUnit> = preferenceRepository.massUnit.get()
}
