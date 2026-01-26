package dev.andrewbailey.launcher.inject

import android.content.Context
import android.content.pm.PackageManager
import dev.andrewbailey.launcher.LauncherApplication
import dev.andrewbailey.launcher.ui.inject.UiGraph
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@DependencyGraph(scope = AppScope::class)
interface LauncherGraph : UiGraph {

    @SingleIn(AppScope::class)
    @Provides
    fun packageManager(context: Context): PackageManager = context.packageManager

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides applicationContext: Context): LauncherGraph
    }
}

val Context.dependencyGraph: LauncherGraph
    get() = (applicationContext as LauncherApplication).graph
