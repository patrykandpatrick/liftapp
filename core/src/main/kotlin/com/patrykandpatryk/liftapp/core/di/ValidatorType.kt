package com.patrykandpatryk.liftapp.core.di

import javax.inject.Qualifier

object ValidatorType {

    @Qualifier
    annotation class Name

    @Qualifier
    annotation class HigherThanZero
}
