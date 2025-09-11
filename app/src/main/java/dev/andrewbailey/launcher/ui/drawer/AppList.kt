package dev.andrewbailey.launcher.ui.drawer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.andrewbailey.launcher.model.ApplicationListing
import dev.andrewbailey.launcher.model.GridDimension
import dev.andrewbailey.launcher.model.GridPosition
import dev.andrewbailey.launcher.model.gd
import dev.andrewbailey.launcher.ui.common.LauncherIcon
import dev.andrewbailey.launcher.ui.common.ScrollingVerticalGrid

@Composable
fun AppList(
    gridWidth: GridDimension,
    apps: List<ApplicationListing>,
    modifier: Modifier = Modifier
) {
    ScrollingVerticalGrid(
        gridWidth = gridWidth,
        cellHeight = 112.dp,
        modifier = modifier
    ) {
        apps.forEachIndexed { index, listing ->
            val y = index / (gridWidth.halfSteps / 2)
            val x = index % (gridWidth.halfSteps / 2)
            LauncherIcon(
                listing = listing,
                modifier = Modifier
                    .fillMaxSize()
                    .gridPosition(position = GridPosition(x.gd, y.gd))
            )
        }
    }
}