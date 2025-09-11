package dev.andrewbailey.launcher.ui.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.andrewbailey.launcher.model.GridSize
import dev.andrewbailey.launcher.model.HomeConfiguration.PlacedPageElement
import dev.andrewbailey.launcher.model.HomeConfiguration.PlacedPageElement.PlacedIcon

@Composable
fun PopulatedHomeGrid(
    gridSize: GridSize,
    contents: List<PlacedPageElement>,
    modifier: Modifier = Modifier
) {
    Grid(
        gridSize = gridSize,
        modifier = modifier,
    ) {
        contents.forEach { element ->
            val modifier = Modifier.fillMaxSize().gridPosition(element.position, element.size)
            when (element) {
                is PlacedIcon -> LauncherIcon(listing = element.app, modifier = modifier)
            }
        }
    }
}