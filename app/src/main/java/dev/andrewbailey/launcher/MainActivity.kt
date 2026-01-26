package dev.andrewbailey.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import dev.andrewbailey.launcher.inject.dependencyGraph
import dev.andrewbailey.launcher.ui.homescreen.HomescreenRoot
import dev.andrewbailey.launcher.ui.inject.LocalUiGraph
import dev.andrewbailey.launcher.ui.theme.LauncherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(
                LocalUiGraph provides dependencyGraph,
            ) {
                LauncherTheme {
                    HomescreenRoot()
                }
            }
        }
    }
}
