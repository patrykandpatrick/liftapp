package com.patrykandpatryk.liftapp.feature.newroutine.domain

sealed class Intent {

    class UpdateName(val name: String) : Intent()

    object Save : Intent()
}
