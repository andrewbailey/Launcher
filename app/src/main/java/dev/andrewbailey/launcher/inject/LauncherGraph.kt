package dev.andrewbailey.launcher.inject

import android.content.Context
import android.content.pm.PackageManager
import dev.andrewbailey.launcher.mediator.DockStateMediator
import dev.andrewbailey.launcher.mediator.HomeStateMediator
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@DependencyGraph(scope = AppScope::class)
interface LauncherGraph {

    @SingleIn(AppScope::class)
    @Provides fun packageManager(context: Context): PackageManager = context.packageManager

    val homeStateMediator: HomeStateMediator
    val dockStateMediator: DockStateMediator

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Provides applicationContext: Context
        ): LauncherGraph
    }
}