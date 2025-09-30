package dev.andrewbailey.launcher.ui.launcher.drawer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import dev.andrewbailey.launcher.mediator.retainHomeStateMediator
import dev.andrewbailey.launcher.model.gd

@Composable
fun Drawer(modifier: Modifier = Modifier) {
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
