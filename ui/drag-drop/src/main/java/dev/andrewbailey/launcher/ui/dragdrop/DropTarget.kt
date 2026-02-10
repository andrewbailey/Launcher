package dev.andrewbailey.launcher.ui.dragdrop

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
context(dragDropScope: DragDropScope<T>)
public fun <T : Any> DropTarget(
    modifier: Modifier = Modifier,
    enabled: (T) -> Boolean = { true },
    dropEnabled: (T) -> Boolean = { true },
    hoverAction: suspend (T) -> Unit = {},
    dropAction: (T) -> Unit = {},
    content: @Composable DragHotspotScope<T>.() -> Unit,
) {
    DragHotspot(
        modifier = modifier,
        enabled = enabled,
        dropEnabled = dropEnabled,
        hoverAction = hoverAction,
        dropAction = dropAction,
        content = content,
    )
}
