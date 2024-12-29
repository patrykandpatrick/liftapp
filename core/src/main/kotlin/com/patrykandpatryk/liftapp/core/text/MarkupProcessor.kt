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

    class Config(private val map: Map<Type, SpanStyle>) {
        fun getSpanStyle(tag: String): SpanStyle =
            map.getOrElse(Type.forTag(tag)) {
                Timber.w("No SpanStyle found for tag: $tag")
                SpanStyle()
            }

        companion object {
            fun build(block: MutableMap<Type, SpanStyle>.() -> Unit): Config =
                Config(mutableMapOf<Type, SpanStyle>().apply(block))
        }
    }

    private class IndexLookup private constructor(private val map: Map<Int, Int>) {
        fun provideIndexes(offset: Int = 0): IndexProvider =
            object : IndexProvider {
                override fun withOffset(offset: Int): IndexProvider = provideIndexes(offset)

                override fun get(index: Int): Int = checkNotNull(map[index + offset])
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

    enum class Type(val tag: String) {
        Bold("b"),
        BoldSurfaceColor("b-surface"),
        Italic("i"),
        Small("small");

        fun wrap(content: String): String = "<$tag>$content</$tag>"

        companion object {
            fun forTag(tag: String): Type =
                checkNotNull(Type.entries.find { it.tag == tag }) { "No Type found for tag: $tag" }
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
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    return remember(colors, typography) {
        MarkupProcessor.Config.build {
            put(MarkupProcessor.Type.Bold, SpanStyle(fontWeight = FontWeight.ExtraBold))
            put(
                MarkupProcessor.Type.BoldSurfaceColor,
                SpanStyle(fontWeight = FontWeight.ExtraBold, color = colors.onSurface),
            )
            put(MarkupProcessor.Type.Italic, SpanStyle(fontStyle = FontStyle.Italic))
            put(
                MarkupProcessor.Type.Small,
                SpanStyle(fontSize = .7.em, color = colors.onSurfaceVariant),
            )
        }
    }
}

@Composable
fun rememberDefaultMarkupProcessor(): MarkupProcessor {
    val config = rememberDefaultMarkupConfig()
    return remember(config) { MarkupProcessor(config) }
}
