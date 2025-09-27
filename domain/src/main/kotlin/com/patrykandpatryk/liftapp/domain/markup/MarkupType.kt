package com.patrykandpatryk.liftapp.domain.markup

sealed class MarkupType(val tag: String) {

    fun wrap(content: String): String = "<$tag>$content</$tag>"

    data object Style {
        data object Bold : MarkupType("b")

        data object Italic : MarkupType("i")
    }

    data object Size {
        data object Small : MarkupType("small")

        data object Medium : MarkupType("medium")
    }

    data object Color {
        data object SurfaceVariant : MarkupType("color-surface-variant")

        data object Primary : MarkupType("color-primary")

        data object Secondary : MarkupType("color-secondary")
    }

    companion object {
        fun wrap(content: String, vararg types: MarkupType): String =
            types.fold(content) { acc, markupType -> markupType.wrap(acc) }
    }
}
