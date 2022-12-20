package com.patrykandpatryk.liftapp.domain.android

interface IsDarkModePublisher {

    operator fun invoke(darkMode: Boolean)
}
