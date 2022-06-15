package com.patrykandpatryk.liftapp.domain.model

interface NameResolver {

    fun getResolvedString(name: Name): String
}
