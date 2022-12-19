package com.patrykandpatryk.liftapp.core.base64

import com.patrykandpatryk.liftapp.domain.base64.Base64
import java.nio.charset.Charset
import javax.inject.Inject
import android.util.Base64.DEFAULT

/**
 * A wrapper for the Android implementation of Base64.
 * It’s used in the app on Android versions older than Oreo.
 */
class AndroidBase64 @Inject constructor() : Base64 {

    private val charset: Charset = Charsets.UTF_8

    override fun encode(string: String): String =
        android.util.Base64.encodeToString(string.toByteArray(charset), DEFAULT)

    override fun decode(string: String): String =
        android.util.Base64.decode(string, DEFAULT)
            .toString(charset)
}
