package com.patrykandpatryk.liftapp.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import timber.log.Timber

@HiltAndroidApp
class LiftApp : Application() {

    @Inject lateinit var loggingTrees: Array<Timber.Tree>

    override fun onCreate() {
        super.onCreate()
        Timber.plant(*loggingTrees)
    }
}
