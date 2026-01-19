package dev.andrewbailey.launcher.ui.launcher.drawer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.andrewbailey.launcher.mediator.DrawerStateMediator
import dev.andrewbailey.launcher.mediator.retainUiMediator
import dev.andrewbailey.launcher.model.gd

@Composable
fun Drawer(modifier: Modifier = Modifier) {
    val mediator = retainUiMediator<DrawerStateMediator>()
    AppList(
        gridWidth = 5.gd,
        apps = remember { mediator.appListProvider.getAllLauncherActivities() }
            .collectAsState(emptyList())
            .value,
        iconProvider = mediator.iconProvider,
        modifier = modifier.fillMaxSize(),
    )
}
