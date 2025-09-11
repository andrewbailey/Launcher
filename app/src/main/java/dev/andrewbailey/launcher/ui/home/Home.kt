package dev.andrewbailey.launcher.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import dev.andrewbailey.launcher.data.AppListProvider
import dev.andrewbailey.launcher.model.gd
import dev.andrewbailey.launcher.ui.drawer.AppList

@Composable
fun Home(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    AppList(
        gridWidth = 5.gd,
        apps = remember {
            AppListProvider(context).getAllLauncherActivities()
        },
        modifier = modifier.fillMaxSize()
    )
}