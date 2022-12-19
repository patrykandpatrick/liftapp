package com.patrykandpatryk.liftapp.domain.base64

import java.nio.charset.Charset
import javax.inject.Inject

/**
 * A wrapper for the Java implementation of Base64.
 * It’s used in the app on Android Oreo and higher, as well as unit tests.
 */
@Suppress("NewApi")
class JavaBase64 @Inject constructor() : Base64 {

    private val charset: Charset = Charsets.UTF_8

    override fun encode(string: String): String =
        java.util.Base64.getEncoder()
            .encodeToString(string.toByteArray(charset))

    override fun decode(string: String): String =
        java.util.Base64.getDecoder()
            .decode(string)
            .toString(charset)
}
