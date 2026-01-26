package dev.andrewbailey.launcher.ui.inject

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

public val LocalUiGraph: ProvidableCompositionLocal<UiGraph> = staticCompositionLocalOf {
    error("LocalUiGraph not provided")
}
