package pl.patrykgoworowski.mintlift.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class LiftApp : Application() {

    @Inject
    lateinit var loggingTrees: Array<Timber.Tree>

    override fun onCreate() {
        super.onCreate()
        Timber.plant(*loggingTrees)
    }
}
