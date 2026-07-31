package com.patrykandpatrick.liftapp.core.ui.error

sealed interface Action {
    object PopBackStack : Action
}
