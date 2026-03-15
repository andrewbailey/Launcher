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
        var bestHotspot: Hotspot<T>? = null
        var bestHotspotDistanceSquared = Float.MAX_VALUE

        val dragCenter = absoluteDragPosition +
            Offset(activeDragContentSize.width / 2f, activeDragContentSize.height / 2f)

        for (i in 0 until hotspots.size) {
            val hotspot = hotspots[i]
            if (!hotspot.enabled(item)) continue

            val (hotspotLeft, hotspotTop, hotspotRight, hotspotBottom) = hotspot.globalPosition
            val padding = hotspot.interactionPadding

            val isDragInsideHotspotBounds =
                dragCenter.x in (hotspotLeft - padding)..<(hotspotRight + padding) &&
                    dragCenter.y in (hotspotTop - padding)..<(hotspotBottom + padding)

            if (isDragInsideHotspotBounds) {
                val hotspotCenter = hotspot.globalPosition.center
                val dx = hotspotCenter.x - dragCenter.x
                val dy = hotspotCenter.y - dragCenter.y
                var distanceSquared = dx * dx + dy * dy

                // Give a slight preference (15% bonus) to the currently active hotspot to prevent
                // flickering when the drag point is near a boundary between two hotspots.
                if (hotspot == lastActiveHotspot) {
                    distanceSquared *= 0.85f
                }

                if (bestHotspot == null || distanceSquared < bestHotspotDistanceSquared) {
                    bestHotspot = hotspot
                    bestHotspotDistanceSquared = distanceSquared
                }
            }
        }

        return bestHotspot
    }

    internal interface Hotspot<T : Any> {
        val globalPosition: Rect
        val interactionPadding: Int

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
