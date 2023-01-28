package com.patrykandpatryk.liftapp.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Name : java.io.Serializable {

    @Serializable
    @SerialName("Raw")
    open class Raw(val value: String) : Name() {

        override fun toString(): String =
            "Raw(value='$value')"
    }

    @Serializable
    @SerialName("Resource")
    class Resource(val resource: StringResource) : Name() {

        override fun toString(): String =
            "Resource(resourceName='$resource')"
    }

    @Serializable
    object Empty : Raw("")
}
