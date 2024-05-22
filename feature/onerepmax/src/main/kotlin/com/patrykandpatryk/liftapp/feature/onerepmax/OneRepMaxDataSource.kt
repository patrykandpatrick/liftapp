package com.patrykandpatryk.liftapp.feature.onerepmax

import com.patrykandpatryk.liftapp.domain.preference.PreferenceRepository
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OneRepMaxDataSource(
    val massUnit: Flow<MassUnit>,
) {
    @Inject
    constructor(
        preferenceRepository: PreferenceRepository,
    ) : this(preferenceRepository.massUnit.get())
}
