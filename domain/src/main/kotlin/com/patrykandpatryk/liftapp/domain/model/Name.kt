package com.patrykandpatryk.liftapp.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Name : java.io.Serializable {

    @Serializable
    @SerialName("Raw")
    data class Raw(val value: String) : Name() {

        override fun toString(): String =
            "Raw(value='$value')"
    }

    @Serializable
    @SerialName("Resource")
    data class Resource(val resource: StringResource) : Name() {

        override fun toString(): String =
            "Resource(resourceName='$resource')"
    }

    companion object {
        val Empty = Raw("")
    }
}
