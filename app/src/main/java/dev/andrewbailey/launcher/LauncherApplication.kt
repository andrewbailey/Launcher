package dev.andrewbailey.launcher

import android.app.Application
import androidx.compose.runtime.ComposeRuntimeFlags
import androidx.compose.runtime.ExperimentalComposeApi
import dev.andrewbailey.launcher.inject.LauncherGraph
import dev.zacsweers.metro.createGraphFactory

class LauncherApplication : Application() {

    lateinit var graph: LauncherGraph
        private set

    override fun onCreate() {
        super.onCreate()

        @OptIn(ExperimentalComposeApi::class)
        ComposeRuntimeFlags.isLinkBufferComposerEnabled = true

        graph = createGraphFactory<LauncherGraph.Factory>().create(
            applicationContext = this,
        )
    }
}
