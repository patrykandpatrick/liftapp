package com.patrykandpatryk.liftapp.core.ui.name

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.model.NameResolver
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

private const val TYPE_STRING = "string"

@Singleton
class NameResolverImpl @Inject constructor(
    private val application: Application,
) : NameResolver {

    private val solvedNames = HashMap<String, String>()

    init {
        registerOnLocaleChangedReceiver()
    }

    override fun getResolvedString(name: Name): String = when (name) {
        is Name.Raw -> name.value
        is Name.Resource -> getSolvedString(name.resourceName)
    }

    private fun getSolvedString(resourceName: String): String {
        val nameRes = application.resources.getIdentifier(resourceName, TYPE_STRING, application.packageName)

        return if (nameRes != 0) {
            try {
                putToMap(resourceName, application.getString(nameRes))
            } catch (exception: Resources.NotFoundException) {
                Timber.e(exception)
                putToMap(resourceName, resourceName)
            }
        } else {
            putToMap(resourceName, resourceName)
        }
    }

    private fun putToMap(key: String, value: String): String {
        solvedNames[key] = value
        return value
    }

    private fun registerOnLocaleChangedReceiver() {
        application.registerReceiver(
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    solvedNames.clear()
                }
            },
            IntentFilter(Intent.ACTION_LOCALE_CHANGED),
        )
    }
}
