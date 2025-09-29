package dev.andrewbailey.launcher.mediator

import androidx.compose.runtime.Composable
import dev.andrewbailey.launcher.LauncherApplication
import dev.andrewbailey.launcher.provider.icon.AppIconProvider
import dev.zacsweers.metro.Inject

@Composable
fun retainDockStateMediator() = retainUiMediator { LauncherApplication.graph.homeStateMediator }

class DockStateMediator @Inject constructor(val iconProvider: AppIconProvider) : UiMediator()
