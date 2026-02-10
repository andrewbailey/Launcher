package dev.andrewbailey.launcher.ui.dragdrop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize

public class DragDropScope<T : Any> {
    public var draggingItem: T? by mutableStateOf(null)
        private set

    public val isDragInProgress: Boolean
        get() = draggingItem != null

    internal var activeDragContent: @Composable () -> Unit by mutableStateOf(EmptyContent)
        private set

    internal var absoluteDragPosition: Offset by mutableStateOf(Offset.Zero)
        private set

    internal var activeDragContentSize: IntSize by mutableStateOf(IntSize.Zero)
        private set

    private var hotspots = mutableStateListOf<Hotspot<T>>()
    private var lastActiveHotspot: Hotspot<T>? = null

    internal fun startDrag(
        item: T,
        dragOrigin: Offset,
        dragContentLayoutSize: IntSize,
        content: @Composable () -> Unit,
    ) {
        require(!isDragInProgress) {
            "Cannot drag two items at the same time."
        }
        draggingItem = item
        absoluteDragPosition = dragOrigin
        activeDragContentSize = dragContentLayoutSize
        activeDragContent = content
    }

    internal fun dragBy(offset: Offset) {
        absoluteDragPosition += offset
        invalidateActiveHotspot()
    }

    internal fun endDrag() {
        lastActiveHotspot?.let { hotspot ->
            val draggingItem = draggingItem!!
            hotspot.onHoverExit(draggingItem)
            hotspot.onDrop(draggingItem)
        }

        activeDragContent = EmptyContent
        absoluteDragPosition = Offset.Zero
        activeDragContentSize = IntSize.Zero
        draggingItem = null
        lastActiveHotspot = null
    }

    internal fun onDragContentSizeChange(newSize: IntSize) {
        if (isDragInProgress) {
            activeDragContentSize = newSize
        }
    }

    internal fun registerHotspot(hotspot: Hotspot<T>) {
        hotspots += hotspot
        if (isDragInProgress) invalidateActiveHotspot()
    }

    internal fun unregisterHotspot(hotspot: Hotspot<T>) {
        hotspots -= hotspot
        if (lastActiveHotspot == hotspot) {
            invalidateActiveHotspot()
        }
    }

    private fun invalidateActiveHotspot() {
        val draggingItem = requireNotNull(draggingItem) {
            "The active hotspot can only be invalidated during a drag operation"
        }
        val activeHotspot = findInteractingHotspot(draggingItem)
        val lastActiveHotspot = lastActiveHotspot
        if (activeHotspot != lastActiveHotspot) {
            lastActiveHotspot?.onHoverExit(draggingItem)
            activeHotspot?.onHoverEnter(draggingItem)
            this.lastActiveHotspot = activeHotspot
        }
    }

    private fun findInteractingHotspot(item: T): Hotspot<T>? {
        val dragContentCenter = absoluteDragPosition + Offset(
            x = activeDragContentSize.width / 2f,
            y = activeDragContentSize.height / 2f,
        )

        return hotspots
            .filter {
                it.enabled(item) && dragContentCenter in it.globalPosition
            }
            .minByOrNull { hotspot ->
                val center = hotspot.globalPosition.center
                val offsetFromCenter = center - dragContentCenter
                offsetFromCenter.getDistanceSquared()
            }
    }

    internal interface Hotspot<T : Any> {
        val globalPosition: Rect

        fun enabled(draggingItem: T): Boolean
        fun dropEnabled(draggingItem: T): Boolean
        fun onHoverEnter(hoveredItem: T)
        fun onHoverExit(unhoveredItem: T)
        fun onDrop(droppedItem: T)
    }

    private companion object {
        private val EmptyContent = @Composable {}
    }
}
