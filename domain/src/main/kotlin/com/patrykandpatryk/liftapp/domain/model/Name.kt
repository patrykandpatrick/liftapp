package com.patrykandpatryk.liftapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
sealed class Name : java.io.Serializable {

    @Serializable
    open class Raw(val value: String) : Name() {

        override fun toString(): String =
            "Raw(value='$value')"
    }

    @Serializable
    class Resource(val resourceName: String) : Name() {

        override fun toString(): String =
            "Resource(resourceName='$resourceName')"
    }

    @Serializable
    object Empty : Raw("")
}
