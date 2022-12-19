package com.patrykandpatryk.liftapp.domain.base64

interface Base64 {

    fun encode(string: String): String

    fun decode(string: String): String
}
