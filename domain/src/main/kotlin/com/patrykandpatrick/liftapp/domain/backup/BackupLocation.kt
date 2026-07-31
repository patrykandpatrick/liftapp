package com.patrykandpatrick.liftapp.domain.backup

import kotlinx.serialization.Serializable

/**
 * An opaque handle to something the data layer can read from or write to — in practice a document
 * URI the user granted access to.
 *
 * The domain deliberately does not know that: `:domain` is a plain JVM library, so
 * `android.net.Uri` cannot appear here. Callers pass whatever string the platform gave them and get
 * the same string back.
 */
@JvmInline @Serializable value class BackupLocation(val value: String)
