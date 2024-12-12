package com.patrykandpatryk.liftapp.functionality.database

internal fun String.appendSQLOrderByCase(columnName: String, ids: List<Long>): String {
    var index = 1
    return plus(
        "ORDER BY CASE ${ids.joinToString(separator = " ") { "WHEN $columnName = $it THEN ${index++}" }} END"
    )
}
