package dev.andrewbailey.launcher.ui.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.andrewbailey.launcher.model.GridSize
import dev.andrewbailey.launcher.model.HomeConfiguration.PlacedPageElement
import dev.andrewbailey.launcher.model.HomeConfiguration.PlacedPageElement.PlacedIcon
import dev.andrewbailey.launcher.provider.icon.AppIconProvider

@Composable
fun PopulatedHomeGrid(
    iconProvider: AppIconProvider,
    gridSize: GridSize,
    contents: List<PlacedPageElement>,
    modifier: Modifier = Modifier,
) {
    Grid(
        gridSize = gridSize,
        modifier = modifier,
    ) {
        contents.forEach { element ->
            val itemModifier = Modifier.fillMaxSize().gridPosition(element.position, element.size)
            when (element) {
                is PlacedIcon -> LauncherIcon(
                    appName = element.app.name,
                    icon = LauncherIconDefaults.icon(element.app, iconProvider),
                    label = LauncherIconDefaults.label(element.app.name),
                    onClick = LauncherIconDefaults.launchActivityAction(element.app),
                    modifier = itemModifier,
                )
            }
        }
    }
}
