package com.patrykandpatrick.liftapp.core.logging

sealed class UiMessage {

    class SnackbarText(val message: String) : UiMessage()
}
