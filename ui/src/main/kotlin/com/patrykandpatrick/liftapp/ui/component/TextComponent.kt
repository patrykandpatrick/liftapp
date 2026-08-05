package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.text.AnnotatedString

object TextComponent {
    internal const val COMPLETED_ICON = "completed_icon"
}

fun AnnotatedString.Builder.appendBulletSeparator() {
    append(" • ")
}

fun AnnotatedString.Builder.appendCompletedIcon() {
    appendInlineContent(TextComponent.COMPLETED_ICON)
}

fun AnnotatedString.Builder.appendBulletList(items: List<String>) {
    items.forEachIndexed { index, item ->
        if (index > 0) append('\n')
        append("• ")
        append(item)
    }
}
