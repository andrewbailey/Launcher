package dev.andrewbailey.launcher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import dev.andrewbailey.launcher.inject.LocalDependencyGraph
import dev.andrewbailey.launcher.inject.dependencyGraph
import dev.andrewbailey.launcher.ui.launcher.Launcher
import dev.andrewbailey.launcher.ui.launcher.home.Home
import dev.andrewbailey.launcher.ui.theme.LauncherTheme

@Composable
fun Root() {
    CompositionLocalProvider(
        LocalDependencyGraph provides LocalContext.current.dependencyGraph,
    ) {
        LauncherTheme {
            Launcher()
        }
    }
}
