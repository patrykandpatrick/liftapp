package pl.patrykgoworowski.mintlift.core.di

import android.app.Activity
import androidx.core.view.WindowInsetsControllerCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
interface ActivityModule {

    companion object {

        @Provides
        fun provide(activity: Activity): WindowInsetsControllerCompat =
            WindowInsetsControllerCompat(activity.window, activity.window.decorView)
    }
}
