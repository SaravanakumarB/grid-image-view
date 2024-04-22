package com.example.grid_image_view.application

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.res.Configuration
import android.os.Bundle
import androidx.multidex.MultiDexApplication
import com.example.grid_image_view.koin.activity.activityModuleKoin
import com.example.grid_image_view.koin.di.commonModuleKoin
import com.example.grid_image_view.koin.di.dataModuleKoin
import com.example.grid_image_view.koin.di.domainModuleKoin
import com.example.grid_image_view.koin.di.networkModuleKoin
import com.example.grid_image_view.koin.fragment.fragmentModuleKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


/** The root [Application] of the app. */
class GridImageViewApplication : MultiDexApplication() {
    /** The root [ApplicationComponent]. */

    override fun onCreate() {
        super.onCreate()
        appContext = this
        val handler = ApplicationLifecycleHandler()
        registerActivityLifecycleCallbacks(handler)
        initKoin()
        registerComponentCallbacks(handler)
    }

    private fun initKoin() {
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@GridImageViewApplication)
            modules(appComponent)
        }
    }

    private val appComponent = listOf(
        dataModuleKoin,
        commonModuleKoin,
        domainModuleKoin,
        networkModuleKoin,
        activityModuleKoin,
        fragmentModuleKoin
    )

    class ApplicationLifecycleHandler : Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {
        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityDestroyed(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        }

        override fun onActivityResumed(activity: Activity) {
            if (isInBackground) {
                isInBackground = false
            }
        }

        override fun onLowMemory() {
        }

        override fun onConfigurationChanged(newConfig: Configuration) {
        }

        override fun onTrimMemory(p0: Int) {
            if (p0 == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
                isInBackground = true
            }
        }
    }

    companion object {
        lateinit var appContext: GridImageViewApplication
        var isInBackground = true
        private const val TAG = "GridImageViewApplication"
    }
}
