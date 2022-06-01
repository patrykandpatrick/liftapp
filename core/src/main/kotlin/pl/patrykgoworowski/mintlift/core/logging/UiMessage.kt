package pl.patrykgoworowski.mintlift.core.logging

sealed class UiMessage {

    class SnackbarText(val message: String) : UiMessage()
}
