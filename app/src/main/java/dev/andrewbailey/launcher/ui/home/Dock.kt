package dev.andrewbailey.launcher.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.andrewbailey.launcher.mediator.retainDockStateMediator
import dev.andrewbailey.launcher.model.GridSize
import dev.andrewbailey.launcher.model.HomeConfiguration
import dev.andrewbailey.launcher.ui.common.Grid
import dev.andrewbailey.launcher.ui.common.PopulatedHomeGrid

@Composable
fun Dock(
    gridSize: GridSize,
    contents: List<HomeConfiguration.PlacedPageElement>,
    modifier: Modifier = Modifier,
) {
    val mediator = retainDockStateMediator()
    PopulatedHomeGrid(
        iconProvider = mediator.iconProvider,
        gridSize = gridSize,
        contents = contents,
        modifier = modifier,
    )
}
