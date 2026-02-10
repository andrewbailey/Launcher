package dev.andrewbailey.launcher.ui.homescreen.layout

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import dev.andrewbailey.launcher.model.GridSize
import dev.andrewbailey.launcher.model.HomeConfiguration.PlacedPageElement
import dev.andrewbailey.launcher.model.HomeConfiguration.PlacedPageElement.PlacedIcon
import dev.andrewbailey.launcher.provider.icon.AppIconProvider
import dev.andrewbailey.launcher.ui.dragdrop.DragDropScope
import dev.andrewbailey.launcher.ui.dragdrop.DraggableItem
import dev.andrewbailey.launcher.ui.homescreen.DraggableHomescreenItem
import dev.andrewbailey.launcher.ui.layout.Grid

@Composable
context(dragDropScope: DragDropScope<DraggableHomescreenItem>)
internal fun PopulatedHomeGrid(
    iconProvider: AppIconProvider,
    gridSize: GridSize,
    contents: List<PlacedPageElement>,
    modifier: Modifier = Modifier,
    itemScale: () -> Float = { 1f },
) {
    Grid(
        gridSize = gridSize,
        modifier = modifier,
    ) {
        contents.forEach { element ->
            val itemModifier = Modifier.gridPosition(element.position, element.size)
            when (element) {
                is PlacedIcon -> PopulatedIcon(element, iconProvider, itemModifier, itemScale)
            }
        }
    }
}

@Composable
context(dragDropScope: DragDropScope<DraggableHomescreenItem>)
private fun PopulatedIcon(
    element: PlacedIcon,
    iconProvider: AppIconProvider,
    modifier: Modifier = Modifier,
    itemScale: () -> Float = { 1f },
) {
    val interactionSource = remember { MutableInteractionSource() }
    DraggableItem(
        item = DraggableHomescreenItem.Icon(element.app),
        modifier = modifier,
        onClick = LauncherIconDefaults.launchActivityAction(element.app),
        interactionSource = interactionSource,
    ) {
        LauncherIcon(
            appName = element.app.name,
            icon = LauncherIconDefaults.icon(element.app, iconProvider),
            label = LauncherIconDefaults.label(element.app.name),
            interactionSource = interactionSource,
            modifier = Modifier.graphicsLayer {
                if (!isBeingDragged) {
                    val scale = itemScale()
                    scaleX = scale
                    scaleY = scale
                }
            },
        )
    }
}
