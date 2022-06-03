package com.patrykandpatryk.liftapp.domain.repository

import com.patrykandpatryk.liftapp.domain.unit.DistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykmichalik.preferencemanager.domain.Preference

interface PreferenceRepository {

    val massUnit: Preference<MassUnit, String, *>
    val distanceUnit: Preference<DistanceUnit, String, *>
}
