package dev.andrewbailey.launcher

import androidx.compose.runtime.Composable
import dev.andrewbailey.launcher.ui.launcher.Launcher
import dev.andrewbailey.launcher.ui.launcher.home.Home
import dev.andrewbailey.launcher.ui.theme.LauncherTheme

@Composable
fun Root() {
    LauncherTheme {
        Launcher()
    }
}
