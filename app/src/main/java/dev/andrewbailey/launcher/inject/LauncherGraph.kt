package dev.andrewbailey.launcher.inject

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.staticCompositionLocalOf
import dev.andrewbailey.launcher.LauncherApplication
import dev.andrewbailey.launcher.mediator.DrawerStateMediator
import dev.andrewbailey.launcher.mediator.HomeStateMediator
import dev.andrewbailey.launcher.mediator.UiMediator
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlin.reflect.KClass

@DependencyGraph(scope = AppScope::class)
interface LauncherGraph {

    @SingleIn(AppScope::class)
    @Provides
    fun packageManager(context: Context): PackageManager = context.packageManager

    @Multibinds
    val mediatorProviders: Map<KClass<*>, Provider<UiMediator>>

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides applicationContext: Context): LauncherGraph
    }
}

val Context.dependencyGraph: LauncherGraph
    get() = (applicationContext as LauncherApplication).graph

val LocalDependencyGraph = staticCompositionLocalOf<LauncherGraph> {
    error("LocalDependencyGraph not provided")
}
