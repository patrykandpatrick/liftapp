package com.patrykandpatryk.liftapp.domain.model

// TODO Remove
@Deprecated(
    message = "Use StringProvider instead",
    replaceWith = ReplaceWith("com.patrykandpatryk.liftapp.domain.text.StringProvider"),
)
interface NameResolver {

    fun getResolvedString(name: Name): String
}
