package pl.patrykgoworowski.mintlift.domain.exception

sealed class DisplayableException(message: String?) : Exception(message) {

    class Text(override val message: String) : DisplayableException(message)
}
