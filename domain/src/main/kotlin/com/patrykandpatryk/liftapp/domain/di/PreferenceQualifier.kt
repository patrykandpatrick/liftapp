package com.patrykandpatryk.liftapp.domain.di

import javax.inject.Qualifier

object PreferenceQualifier {
    @Qualifier annotation class GoalInfoVisible

    @Qualifier annotation class LongDistanceUnit
}
