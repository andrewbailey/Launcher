package dev.andrewbailey.launcher.mediator

import androidx.compose.runtime.Composable
import dev.andrewbailey.launcher.LauncherApplication
import dev.andrewbailey.launcher.provider.apps.AppListProvider
import dev.andrewbailey.launcher.provider.icon.AppIconProvider
import dev.zacsweers.metro.Inject

@Composable
fun retainHomeStateMediator() = retainUiMediator { LauncherApplication.graph.homeStateMediator }

class HomeStateMediator @Inject constructor(
    val iconProvider: AppIconProvider,
    val appListProvider: AppListProvider,
) : UiMediator()
