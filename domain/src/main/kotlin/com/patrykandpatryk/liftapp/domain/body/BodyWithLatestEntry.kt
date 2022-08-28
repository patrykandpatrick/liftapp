package com.patrykandpatryk.liftapp.domain.body

data class BodyWithLatestEntry(
    val id: Long,
    val name: String,
    val type: BodyType,
    val latestEntry: BodyEntry?,
)
