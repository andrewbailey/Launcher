package dev.andrewbailey.launcher.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import dev.andrewbailey.launcher.mediator.retainHomeStateMediator
import dev.andrewbailey.launcher.model.gd
import dev.andrewbailey.launcher.provider.apps.AppListProvider
import dev.andrewbailey.launcher.ui.drawer.AppList

@Composable
fun Home(modifier: Modifier = Modifier) {
    val mediator = retainHomeStateMediator()
    AppList(
        gridWidth = 5.gd,
        apps = remember { mediator.appListProvider.getAllLauncherActivities() }
            .collectAsState(emptyList())
            .value,
        iconProvider = mediator.iconProvider,
        modifier = modifier.fillMaxSize(),
    )
}
