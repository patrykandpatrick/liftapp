package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Bullet
import androidx.compose.ui.unit.em
import com.patrykandpatrick.liftapp.ui.component.TextComponent.listBullet

object TextComponent {
    internal const val BULLET_SEPARATOR = "bullet_separator"

    internal const val COMPLETED_ICON = "completed_icon"

    val listBullet = Bullet.Default.copy(width = .35.em, height = .35.em, padding = .5.em)

    val separatorBullet = Bullet.Default.copy(width = .3.em, height = .3.em, padding = .4.em)
}

fun AnnotatedString.Builder.appendBulletSeparator() {
    appendInlineContent(TextComponent.BULLET_SEPARATOR)
}

fun AnnotatedString.Builder.appendCompletedIcon() {
    appendInlineContent(TextComponent.COMPLETED_ICON)
}

fun AnnotatedString.Builder.appendBulletList(items: List<String>) {
    withBulletList(bullet = listBullet) { items.forEach { withBulletListItem { append(it) } } }
}
