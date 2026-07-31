package com.patrykandpatrick.liftapp.functionality.backup.file

import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import java.io.StringReader
import java.io.StringWriter
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class PreferencesCsvTest {

    @Test
    fun `device-specific backup settings are neither written nor restored`() {
        val theme = stringPreferencesKey("theme")
        val autoBackup = stringPreferencesKey("auto_backup")
        val lastDestination = stringPreferencesKey("last_export_destination")
        val writer = StringWriter()

        PreferencesCsv.write(
            mutablePreferencesOf(
                theme to "Dark",
                autoBackup to "enabled",
                lastDestination to "content://old-device",
            ),
            writer,
            Json,
        )

        val restored = mutablePreferencesOf()
        PreferencesCsv.read(StringReader(writer.toString()), Json)(restored)
        assertEquals("Dark", restored[theme])
        assertNull(restored[autoBackup])
        assertNull(restored[lastDestination])
    }
}
