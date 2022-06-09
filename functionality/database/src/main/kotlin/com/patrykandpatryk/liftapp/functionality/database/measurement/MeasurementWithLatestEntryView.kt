@file:Suppress("Indentation")
package com.patrykandpatryk.liftapp.functionality.database.measurement

import androidx.room.DatabaseView
import androidx.room.Embedded

@DatabaseView(
    value = "SELECT measurement.*, latest_entry.* FROM measurement " +
            "LEFT JOIN " +
            "(SELECT * FROM measurement_entry AS entry WHERE entry.timestamp IN" +
            "(SELECT MAX(E.timestamp) FROM measurement_entry AS E GROUP BY E.parent_id) " +
            "ORDER BY entry.entry_id DESC) " +
            "AS latest_entry ON measurement.id = latest_entry.parent_id",
    viewName = "measurement_with_latest_entry",
)
class MeasurementWithLatestEntryView(
    @Embedded val measurement: MeasurementEntity,
    @Embedded val entry: MeasurementEntryEntity?,
)
