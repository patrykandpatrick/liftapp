package com.patrykandpatrick.liftapp.functionality.database.converter

import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import timber.log.Timber

/**
 * Reads the three muscle columns of the `exercise` table, tolerating names this version of the app
 * no longer knows.
 *
 * A schema migration can rewrite every row in the database, but it cannot reach a `.lfa` archive
 * sitting in a user's cloud storage: `SqliteCsv` replays raw column text straight back into the
 * table at any later date, so a backup taken before the deltoid was split into three still hands us
 * `"Shoulders"` years later. The app's `Json` sets neither `ignoreUnknownKeys` nor
 * `coerceInputValues`, so without this the restore throws — a crash, not a degradation.
 *
 * Unknown names are dropped with a warning rather than failing the whole list. Encoding only ever
 * writes current names, so a row rewrites itself into the new vocabulary the next time it is saved.
 */
internal object MuscleListSerializer : KSerializer<List<Muscle>> {

    /**
     * Names that used to be [Muscle] constants, mapped onto what replaced them.
     *
     * `Shoulders` becomes [Muscle.FrontDelts] because every press loads the front head, making it
     * the highest-probability single answer. It deliberately under-claims the side and rear heads:
     * mapping to all three would treble the volume and rebuild the exact lump the split removed.
     */
    val ALIASES: Map<String, Muscle> = mapOf("Shoulders" to Muscle.FrontDelts)

    private val delegate = ListSerializer(String.serializer())

    private val musclesByName: Map<String, Muscle> = Muscle.entries.associateBy { it.name }

    override val descriptor: SerialDescriptor
        get() = delegate.descriptor

    override fun deserialize(decoder: Decoder): List<Muscle> =
        delegate.deserialize(decoder).mapNotNull(::muscleOrNull)

    override fun serialize(encoder: Encoder, value: List<Muscle>) {
        delegate.serialize(encoder, value.map(Muscle::name))
    }

    /** Resolves a stored name, whether current or legacy. Null means "no longer a muscle". */
    fun muscleOrNull(name: String): Muscle? =
        musclesByName[name]
            ?: ALIASES[name]
            ?: null.also { Timber.w("Dropping unknown muscle `%s`.", name) }
}
