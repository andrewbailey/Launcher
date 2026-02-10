package dev.andrewbailey.launcher.ui.dragdrop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

@Composable
public fun <T : Any> DragDropSurface(
    modifier: Modifier = Modifier,
    content: @Composable DragDropScope<T>.() -> Unit,
) {
    var positionInRoot by remember { mutableStateOf(Offset.Zero) }
    val scope = remember { DragDropScope<T>() }
    Box(
        modifier = modifier,
    ) {
        scope.content()
        Box(
            modifier = Modifier
                .onPlaced { positionInRoot = it.positionInRoot() }
                .offset {
                    val absoluteLayoutPosition = scope.absoluteDragPosition
                    IntOffset(
                        x = (absoluteLayoutPosition.x - positionInRoot.x).roundToInt(),
                        y = (absoluteLayoutPosition.y - positionInRoot.y).roundToInt(),
                    )
                }
                .onSizeChanged { scope.onDragContentSizeChange(it) },
        ) {
            scope.activeDragContent()
        }
    }
}
