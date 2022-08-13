package com.patrykandpatryk.liftapp.domain.message

class LocalizableMessage(
    val messageResId: Int,
    vararg val messageArgs: Any,
): java.io.Serializable
