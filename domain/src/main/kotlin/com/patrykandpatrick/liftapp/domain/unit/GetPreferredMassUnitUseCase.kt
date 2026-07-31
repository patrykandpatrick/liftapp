package com.patrykandpatrick.liftapp.domain.unit

import kotlinx.coroutines.flow.Flow

fun interface GetPreferredMassUnitUseCase {
    fun getPreferredMassUnit(): Flow<MassUnit>
}

operator fun GetPreferredMassUnitUseCase.invoke(): Flow<MassUnit> = getPreferredMassUnit()
