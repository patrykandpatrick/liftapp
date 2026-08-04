package com.patrykandpatrick.liftapp.app

import android.app.Application
import com.patrykandpatrick.liftapp.shortcut.LauncherShortcuts
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import timber.log.Timber

@HiltAndroidApp
class LiftApp : Application() {

    @Inject lateinit var loggingTrees: Array<Timber.Tree>

    @Inject lateinit var launcherShortcuts: LauncherShortcuts

    override fun onCreate() {
        super.onCreate()
        loggingTrees.forEach(Timber::plant)
        launcherShortcuts.keepUpToDate()
    }
}
