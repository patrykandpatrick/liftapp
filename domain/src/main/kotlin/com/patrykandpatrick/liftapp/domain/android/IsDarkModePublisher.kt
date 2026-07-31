package com.patrykandpatrick.liftapp.domain.android

interface IsDarkModePublisher {

    operator fun invoke(darkMode: Boolean)
}
