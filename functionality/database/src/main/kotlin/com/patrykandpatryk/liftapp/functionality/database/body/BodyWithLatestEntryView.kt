@file:Suppress("Indentation")
package com.patrykandpatryk.liftapp.functionality.database.body

import androidx.room.DatabaseView
import androidx.room.Embedded

@DatabaseView(
    value = "SELECT body.*, latest_entry.* FROM body " +
            "LEFT JOIN " +
            "(SELECT * FROM body_entry AS entry WHERE entry.timestamp IN" +
            "(SELECT MAX(E.timestamp) FROM body_entry AS E GROUP BY E.parent_id) " +
            "ORDER BY entry.entry_id DESC LIMIT 1) " +
            "AS latest_entry ON body.id = latest_entry.parent_id",
    viewName = "body_with_latest_entry",
)
class BodyWithLatestEntryView(
    @Embedded val body: BodyEntity,
    @Embedded val entry: BodyEntryEntity?,
)
