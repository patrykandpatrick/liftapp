package com.patrykandpatryk.liftapp.core.logging

sealed class UiMessage {

    class SnackbarText(val message: String) : UiMessage()
}
