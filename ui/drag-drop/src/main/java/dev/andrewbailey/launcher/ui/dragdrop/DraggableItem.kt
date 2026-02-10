package dev.andrewbailey.launcher.ui.dragdrop

import androidx.compose.foundation.gestures.awaitDragOrCancellation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitLongPressOrCancellation
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import kotlinx.coroutines.CancellationException

@Composable
context(dragDropScope: DragDropScope<T>)
public fun <T : Any> DraggableItem(
    item: T,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null,
    onClick: (() -> Unit)? = null,
    onLongPress: (() -> Unit)? = null,
    onDragStart: (() -> Unit)? = null,
    content: @Composable DraggableScope.() -> Unit,
) {
    val scope = remember { DraggableScopeImpl() }
    val draggableContent = remember(content) {
        movableContentOf {
            var lastHostConstraints by remember { mutableStateOf<Constraints?>(null) }
            Layout(content = { scope.content() }) { measurables, constraints ->
                if (!scope.isBeingDragged) lastHostConstraints = constraints
                val hostConstraints = lastHostConstraints ?: constraints

                val placeables = measurables.map { it.measure(hostConstraints) }
                layout(
                    width = placeables.maxOfOrNull { it.width } ?: 0,
                    height = placeables.maxOfOrNull { it.height } ?: 0,
                ) {
                    placeables.forEach { it.place(0, 0) }
                }
            }
        }
    }

    val hapticFeedback = LocalHapticFeedback.current
    var restingContentSize by remember { mutableStateOf(IntSize.Zero) }
    var restingPositionInRoot by remember { mutableStateOf(Offset.Zero) }
    Layout(
        content = {
            if (!scope.isBeingDragged) draggableContent()
        },
        modifier = modifier
            .onGloballyPositioned {
                restingPositionInRoot = it.positionInRoot()
            }
            .pointerInput(item) {
                awaitEachGesture {
                    val down = awaitFirstDown()

                    val pressInteraction = PressInteraction.Press(down.position)
                    interactionSource?.tryEmit(pressInteraction)

                    if (awaitLongPressOrCancellation(down.id) == null) {
                        val lastEvent = currentEvent.changes.firstOrNull { it.id == down.id }
                        if (lastEvent != null && lastEvent.changedToUp()) {
                            onClick?.invoke()
                        }
                        interactionSource?.tryEmit(PressInteraction.Release(pressInteraction))
                        return@awaitEachGesture
                    }

                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongPress?.invoke()

                    if (awaitDragOrCancellation(down.id) == null) {
                        interactionSource?.tryEmit(PressInteraction.Release(pressInteraction))
                        return@awaitEachGesture
                    }

                    dragDropScope.startDrag(
                        item = item,
                        dragOrigin = restingPositionInRoot,
                        dragContentLayoutSize = restingContentSize,
                        content = draggableContent,
                    )
                    scope.isBeingDragged = true
                    val dragStartInteraction = DragInteraction.Start()
                    interactionSource?.tryEmit(dragStartInteraction)
                    try {
                        while (currentEvent.changes.fastAny { it.id == down.id && it.pressed }) {
                            var offset = Offset.Zero
                            awaitPointerEvent().changes.fastForEach { change ->
                                if (change.id == down.id && change.pressed && !change.isConsumed) {
                                    offset += change.positionChange()
                                    change.consume()
                                }
                            }
                            dragDropScope.dragBy(offset)
                        }
                    } catch (_: CancellationException) { }

                    // TODO: Animate to the destination point (or the origin, if cancelled)

                    scope.isBeingDragged = false
                    dragDropScope.endDrag()
                    interactionSource?.tryEmit(DragInteraction.Stop(dragStartInteraction))
                    interactionSource?.tryEmit(PressInteraction.Release(pressInteraction))
                }
            },
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        if (!scope.isBeingDragged) {
            restingContentSize = IntSize(
                width = placeables.maxOfOrNull { it.width } ?: 0,
                height = placeables.maxOfOrNull { it.height } ?: 0,
            )
        }
        layout(restingContentSize.width, restingContentSize.height) {
            placeables.forEach { it.place(0, 0) }
        }
    }
}

public interface DraggableScope {
    public val isBeingDragged: Boolean
    public val hoverTarget: Any?
}

private class DraggableScopeImpl : DraggableScope {
    override var isBeingDragged: Boolean by mutableStateOf(false)
    override var hoverTarget: Any? by mutableStateOf(null)
}
