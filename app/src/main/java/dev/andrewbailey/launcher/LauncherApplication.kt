package dev.andrewbailey.launcher

import android.app.Application
import dev.andrewbailey.launcher.inject.LauncherGraph
import dev.zacsweers.metro.createGraphFactory

class LauncherApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        graph = createGraphFactory<LauncherGraph.Factory>().create(
            applicationContext = this,
        )
    }

    companion object {
        lateinit var graph: LauncherGraph
            private set
    }
}