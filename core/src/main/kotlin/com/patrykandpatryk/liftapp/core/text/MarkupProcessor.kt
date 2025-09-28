package com.patrykandpatryk.liftapp.core.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatryk.liftapp.domain.markup.MarkupType
import timber.log.Timber

class MarkupProcessor(private val config: Config) {
    @Stable
    fun toAnnotatedString(text: String): AnnotatedString {
        val strippedText = stripMarkupRegex.replace(text, "")
        val indexLookup = IndexLookup.create(text, strippedText)
        return buildAnnotatedString {
            append(strippedText)
            addMarkupStyles(text, indexLookup.provideIndexes())
        }
    }

    private fun AnnotatedString.Builder.addMarkupStyles(
        text: String,
        indexProvider: IndexLookup.IndexProvider,
    ) {
        markupRegex.findAll(text).forEach { match ->
            val (tag, content) = match.destructured
            addStyle(
                style = config.getSpanStyle(tag),
                start = indexProvider[match.range.first],
                end = indexProvider[match.range.last],
            )
            addMarkupStyles(
                text = content,
                indexProvider =
                    indexProvider.withOffset(checkNotNull(match.groups.last()).range.first),
            )
        }
    }

    class Config(
        private val tags: Map<String, MarkupType>,
        private val spanStyles: Map<MarkupType, SpanStyle>,
    ) {
        fun getSpanStyle(tag: String): SpanStyle =
            tags[tag]?.let { markupType -> spanStyles[markupType] }
                ?: run {
                    Timber.w("No SpanStyle found for tag: $tag")
                    SpanStyle()
                }

        class Builder {
            private val tags = mutableMapOf<String, MarkupType>()
            private val spanStyles = mutableMapOf<MarkupType, SpanStyle>()

            infix fun MarkupType.using(style: SpanStyle) {
                tags[tag] = this
                spanStyles[this] = style
            }

            fun build(): Config = Config(tags, spanStyles)
        }

        companion object {
            fun build(builder: Builder.() -> Unit): Config = Builder().apply(builder).build()
        }
    }

    private class IndexLookup private constructor(private val map: Map<Int, Int>) {
        fun provideIndexes(providedOffset: Int = 0): IndexProvider =
            object : IndexProvider {
                override fun withOffset(offset: Int): IndexProvider =
                    provideIndexes(offset + providedOffset)

                override fun get(index: Int): Int = checkNotNull(map[index + providedOffset])
            }

        interface IndexProvider {
            operator fun get(index: Int): Int

            fun withOffset(offset: Int): IndexProvider
        }

        companion object {
            fun create(textWithMarkup: String, strippedText: String): IndexLookup {
                var transformedIndex = 0
                return buildMap {
                        textWithMarkup.forEachIndexed { index, char ->
                            put(index, transformedIndex)
                            if (strippedText.getOrNull(transformedIndex) == char) {
                                transformedIndex++
                            }
                        }
                    }
                    .let(::IndexLookup)
            }
        }
    }

    companion object {
        private val markupRegex = Regex("""<(\w+[-\w+]*)>(.*?)</\1>""")
        private val stripMarkupRegex = Regex("""<(/?\w+[-\w+]*)>""")
    }
}

val LocalMarkupProcessor: ProvidableCompositionLocal<MarkupProcessor> = staticCompositionLocalOf {
    error("No MarkupProcessor provided")
}

@Composable
fun rememberDefaultMarkupConfig(): MarkupProcessor.Config {
    val colors = colorScheme
    val typography = MaterialTheme.typography

    return remember(colors, typography) {
        MarkupProcessor.Config.build {
            MarkupType.Style.Bold using SpanStyle(fontWeight = FontWeight.ExtraBold)
            MarkupType.Style.Italic using SpanStyle(fontStyle = FontStyle.Italic)

            MarkupType.Size.Small using SpanStyle(fontSize = .7.em)
            MarkupType.Size.Medium using SpanStyle(fontSize = .85.em)

            MarkupType.Color.Primary using SpanStyle(color = colors.primary)
            MarkupType.Color.Secondary using SpanStyle(color = colors.secondary)
            MarkupType.Color.SurfaceVariant using SpanStyle(color = colors.onSurfaceVariant)
            MarkupType.Color.Green using SpanStyle(color = colors.green)
            MarkupType.Color.Yellow using SpanStyle(color = colors.yellow)
        }
    }
}

@Composable
fun rememberDefaultMarkupProcessor(): MarkupProcessor {
    val config = rememberDefaultMarkupConfig()
    return remember(config) { MarkupProcessor(config) }
}

@Composable
fun parseMarkup(text: String): AnnotatedString {
    val processor = LocalMarkupProcessor.current
    return remember(processor, text) { processor.toAnnotatedString(text) }
}
