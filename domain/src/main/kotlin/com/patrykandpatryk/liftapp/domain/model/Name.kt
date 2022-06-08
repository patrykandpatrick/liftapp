package com.patrykandpatryk.liftapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
sealed class Name {

    @Serializable
    class Raw(val value: String) : Name()

    @Serializable
    class Resource(val resourceName: String) : Name()
}
