package dev.andrewbailey.launcher.ui.dragdrop

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
context(dragDropScope: DragDropScope<T>)
public fun <T : Any> DragHotspot(
    modifier: Modifier = Modifier,
    enabled: (T) -> Boolean = { true },
    hoverAction: suspend (T) -> Unit = {},
    content: @Composable DragHotspotScope<T>.() -> Unit,
) {
    DragHotspot(
        modifier = modifier,
        enabled = enabled,
        dropEnabled = { false },
        hoverAction = hoverAction,
        dropAction = {},
        content = content,
    )
}

@Composable
context(dragDropScope: DragDropScope<T>)
public fun <T : Any> DragHotspot(
    enabled: (T) -> Boolean,
    dropEnabled: (T) -> Boolean,
    hoverAction: suspend (T) -> Unit,
    dropAction: (T) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable DragHotspotScope<T>.() -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val scope = remember { DragHotspotScopeImpl<T>(coroutineScope) }.apply {
        // These are snapshot writes, no SideEffect needed.
        this.enabled = enabled
        this.dropEnabled = dropEnabled
        this.hoverAction = hoverAction
        this.dropAction = dropAction
    }

    DisposableEffect(scope, dragDropScope) {
        dragDropScope.registerHotspot(scope)
        onDispose { dragDropScope.unregisterHotspot(scope) }
    }

    Box(
        modifier = modifier
            .onGloballyPositioned { layoutCoordinates ->
                scope.globalPosition = layoutCoordinates.boundsInRoot()
            },
    ) {
        scope.content()
    }
}

public object DragHotspot {
    public val DefaultHoverActionDelay: Duration = 400.milliseconds
}

public sealed interface DragHotspotScope<T : Any> {
    public val hoverItem: T?
    public val isHovered: Boolean
}

private class DragHotspotScopeImpl<T : Any>(private val coroutineScope: CoroutineScope) :
    DragHotspotScope<T>,
    DragDropScope.Hotspot<T> {

    override var hoverItem: T? by mutableStateOf(null)

    override val isHovered: Boolean
        get() = hoverItem != null

    override var globalPosition by mutableStateOf(Rect.Zero)

    var enabled: (T) -> Boolean by mutableStateOf(EnabledUninitialized)
    var dropEnabled: (T) -> Boolean by mutableStateOf(DropEnabledUninitialized)
    var hoverAction: suspend (T) -> Unit by mutableStateOf(HoverActionUninitialized)
    var dropAction: (T) -> Unit by mutableStateOf(DropActionUninitialized)

    private var hoverJob: Job? = null
        set(value) {
            field?.cancel()
            field = value
        }

    override fun enabled(draggingItem: T) = enabled.invoke(draggingItem)

    override fun dropEnabled(draggingItem: T) = dropEnabled.invoke(draggingItem)

    override fun onHoverEnter(hoveredItem: T) {
        hoverItem = hoveredItem
        hoverJob = coroutineScope.launch { hoverAction(hoveredItem) }
    }

    override fun onHoverExit(unhoveredItem: T) {
        hoverItem = null
        hoverJob = null
    }

    override fun onDrop(droppedItem: T) {
        dropAction(droppedItem)
    }

    companion object {
        private val EnabledUninitialized: (Any) -> Boolean = { false }
        private val DropEnabledUninitialized: (Any) -> Boolean = { false }
        private val HoverActionUninitialized: suspend (Any) -> Unit = { }
        private val DropActionUninitialized: (Any) -> Unit = { }
    }
}
