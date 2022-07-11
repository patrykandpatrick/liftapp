package com.patrykandpatryk.liftapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
sealed class Name : java.io.Serializable {

    @Serializable
    open class Raw(val value: String) : Name()

    @Serializable
    class Resource(val resourceName: String) : Name()

    @Serializable
    object Empty : Raw("")
}
