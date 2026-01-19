package dev.andrewbailey.launcher.ui.launcher.home

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.andrewbailey.launcher.model.GridSize
import dev.andrewbailey.launcher.model.HomeConfiguration.PlacedPageElement
import dev.andrewbailey.launcher.provider.icon.AppIconProvider
import dev.andrewbailey.launcher.ui.common.PopulatedHomeGrid

@Composable
fun Dock(
    iconProvider: AppIconProvider,
    gridSize: GridSize,
    contents: List<PlacedPageElement>,
    modifier: Modifier = Modifier,
) {
    PopulatedHomeGrid(
        iconProvider = iconProvider,
        gridSize = gridSize,
        contents = contents,
        modifier = modifier.height(86.dp * gridSize.height.halfSteps / 2),
    )
}
