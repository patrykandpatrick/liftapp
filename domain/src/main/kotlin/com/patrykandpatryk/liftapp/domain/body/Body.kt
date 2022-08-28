package com.patrykandpatryk.liftapp.domain.body

import com.patrykandpatryk.liftapp.domain.model.Name

data class Body(
    val id: Long,
    val name: String,
    val type: BodyType,
) {

    data class Insert(
        val name: Name,
        val type: BodyType,
    )
}
