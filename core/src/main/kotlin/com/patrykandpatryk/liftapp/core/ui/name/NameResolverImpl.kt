package com.patrykandpatryk.liftapp.core.ui.name

import android.app.Application
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.model.NameResolver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NameResolverImpl @Inject constructor(
    private val application: Application,
) : NameResolver {

    override fun getResolvedString(name: Name): String = when (name) {
        is Name.Raw -> name.value
        is Name.Resource -> application.getString(name.resource.resourceId)
    }
}
