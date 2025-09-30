package dev.andrewbailey.launcher.ui.launcher.drawer

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.andrewbailey.launcher.model.ApplicationListing
import dev.andrewbailey.launcher.model.GridDimension
import dev.andrewbailey.launcher.model.GridPosition
import dev.andrewbailey.launcher.model.gd
import dev.andrewbailey.launcher.provider.icon.AppIconProvider
import dev.andrewbailey.launcher.ui.common.LauncherIcon
import dev.andrewbailey.launcher.ui.common.LauncherIconDefaults
import dev.andrewbailey.launcher.ui.common.UnboundedVerticalGrid

@Composable
fun AppList(
    gridWidth: GridDimension,
    apps: List<ApplicationListing>,
    iconProvider: AppIconProvider,
    modifier: Modifier = Modifier,
) {
    UnboundedVerticalGrid(
        gridWidth = gridWidth,
        cellHeight = 112.dp,
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        apps.forEachIndexed { index, listing ->
            val y = index / (gridWidth.halfSteps / 2)
            val x = index % (gridWidth.halfSteps / 2)
            LauncherIcon(
                appName = listing.name,
                icon = LauncherIconDefaults.icon(listing, iconProvider),
                label = LauncherIconDefaults.label(listing.name),
                onClick = LauncherIconDefaults.launchActivityAction(listing),
                modifier = Modifier
                    .fillMaxSize()
                    .gridPosition(position = GridPosition(x.gd, y.gd)),
            )
        }
    }
}
