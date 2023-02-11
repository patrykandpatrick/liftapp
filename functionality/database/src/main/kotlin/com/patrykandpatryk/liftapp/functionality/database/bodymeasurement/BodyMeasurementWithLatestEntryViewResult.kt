package com.patrykandpatryk.liftapp.functionality.database.bodymeasurement

import androidx.room.DatabaseView
import androidx.room.Embedded

@DatabaseView(
    value = """SELECT body_measurements.*,
                      latest_body_measurement_entry.id AS bme_id,
                      latest_body_measurement_entry.body_measurement_id as bme_body_measurement_id,
                      latest_body_measurement_entry.value as bme_value,
                      latest_body_measurement_entry.timestamp as bme_timestamp
                 FROM body_measurements
                      LEFT JOIN (SELECT *
                                   FROM body_measurement_entries AS bme1
                                  WHERE bme1.timestamp IN (SELECT MAX(bme2.timestamp)
                                                             FROM body_measurement_entries AS bme2
                                                            GROUP BY bme2.body_measurement_id)
                                  GROUP BY bme1.body_measurement_id
                                  ORDER BY bme1.id DESC) AS latest_body_measurement_entry
                             ON body_measurements.id = latest_body_measurement_entry.body_measurement_id""",
    viewName = "body_measurements_with_latest_entries",
)
class BodyMeasurementWithLatestEntryViewResult(
    @Embedded val bodyMeasurement: BodyMeasurementEntity,
    @Embedded(prefix = "bme_")
    val entry: BodyMeasurementEntryEntity?,
)
