package dev.andrewbailey.launcher.ui.common

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ParentDataModifierNode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import dev.andrewbailey.launcher.model.GridDimension
import dev.andrewbailey.launcher.model.GridPosition
import dev.andrewbailey.launcher.model.GridSize
import dev.andrewbailey.launcher.model.by
import dev.andrewbailey.launcher.model.gd
import dev.andrewbailey.launcher.model.x
import dev.andrewbailey.launcher.ui.common.GridPositionModifier.GridPositionModifierElement

@Composable
fun Grid(
    gridSize: GridSize,
    modifier: Modifier = Modifier,
    cellAlignment: Alignment = Alignment.Center,
    content: @Composable GridScope.() -> Unit,
) {
    Layout(content = { GridScopeInstance.content() }, modifier) { measurables, constraints ->
        val cellWidth = constraints.maxWidth / gridSize.width.halfSteps * 2
        val cellHeight = constraints.maxHeight / gridSize.height.halfSteps * 2

        val oneByOneCellConstraints = Constraints(maxWidth = cellWidth, maxHeight = cellHeight)
        val placeables = measurables.map { measurable ->
            val size = (measurable.parentData as? GridParentData ?: GridParentData.Default).size
            val constraints = when {
                size.width == 1.gd && size.height == 1.gd -> oneByOneCellConstraints
                else -> Constraints(
                    maxWidth = cellWidth * size.width.halfSteps / 2,
                    maxHeight = cellHeight * size.height.halfSteps / 2,
                )
            }
            measurable.measure(constraints)
        }

        layout(width = constraints.maxWidth, height = constraints.maxHeight) {
            placeables.fastForEach { placeable ->
                val gridData = (placeable.parentData as? GridParentData) ?: GridParentData.Default

                val alignmentOffset = cellAlignment.align(
                    size = IntSize(
                        width = placeable.measuredWidth,
                        height = placeable.measuredHeight,
                    ),
                    space = IntSize(
                        width = cellWidth * gridData.size.width.halfSteps / 2,
                        height = cellHeight * gridData.size.height.halfSteps / 2,
                    ),
                    layoutDirection = layoutDirection,
                )

                placeable.place(
                    x = alignmentOffset.x + (gridData.position.x.halfSteps * cellWidth / 2),
                    y = alignmentOffset.y + (gridData.position.y.halfSteps * cellHeight / 2),
                )
            }
        }
    }
}

@Composable
fun UnboundedVerticalGrid(
    gridWidth: GridDimension,
    cellHeight: Dp,
    modifier: Modifier = Modifier,
    cellAlignment: Alignment = Alignment.Center,
    content: @Composable GridScope.() -> Unit,
) {
    Layout(
        content = { GridScopeInstance.content() },
        modifier = modifier,
    ) { measurables, constraints ->
        val cellWidth = constraints.maxWidth / gridWidth.halfSteps * 2
        val cellHeight = cellHeight.roundToPx()

        val oneByOneCellConstraints = Constraints(maxWidth = cellWidth, maxHeight = cellHeight)
        var heightInCells = 0.gd
        val placeables = measurables.map { measurable ->
            val gridData = (measurable.parentData as? GridParentData ?: GridParentData.Default)
            heightInCells = maxOf(heightInCells, gridData.size.height + gridData.position.y)

            val size = gridData.size
            val constraints = when {
                size.width == 1.gd && size.height == 1.gd -> oneByOneCellConstraints
                else -> Constraints(
                    maxWidth = cellWidth * size.width.halfSteps / 2,
                    maxHeight = cellHeight * size.height.halfSteps / 2,
                )
            }
            measurable.measure(constraints)
        }

        layout(width = constraints.maxWidth, height = cellHeight * heightInCells.halfSteps / 2) {
            placeables.fastForEach { placeable ->
                val gridData = (placeable.parentData as? GridParentData) ?: GridParentData.Default

                val alignmentOffset = cellAlignment.align(
                    size = IntSize(
                        width = placeable.measuredWidth,
                        height = placeable.measuredHeight,
                    ),
                    space = IntSize(
                        width = cellWidth * gridData.size.width.halfSteps / 2,
                        height = cellHeight * gridData.size.height.halfSteps / 2,
                    ),
                    layoutDirection = layoutDirection,
                )

                placeable.place(
                    x = alignmentOffset.x + (gridData.position.x.halfSteps * cellWidth / 2),
                    y = alignmentOffset.y + (gridData.position.y.halfSteps * cellHeight / 2),
                )
            }
        }
    }
}

@Preview
@Composable
internal fun GridPreview() {
    Grid(
        gridSize = 5.gd by 9.gd,
        modifier = Modifier.background(Color.White),
    ) {
        for (x in 0 until 9) {
            for (y in 0 until 17) {
                val isHalfGrid = x % 2 != 0 || y % 2 != 0
                Box(
                    Modifier
                        .gridPosition(position = (x / 2.0).gd x (y / 2.0).gd, size = 1.gd by 1.gd)
                        .size(if (isHalfGrid) 3.dp else 6.dp)
                        .clip(CircleShape)
                        .background(if (isHalfGrid) Color.Blue else Color.Red),
                )
            }
        }

        Box(
            Modifier
                .gridPosition(position = 0.5.gd x 0.5.gd, size = 4.gd by 1.5.gd)
                .padding(16.dp)
                .fillMaxSize()
                .clip(CircleShape)
                .background(Color.Black),
        )

        for (x in 0 until 5) {
            for (y in 2 until 6) {
                Box(
                    Modifier
                        .gridPosition(position = x.gd x y.gd)
                        .padding(16.dp)
                        .aspectRatio(1f)
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.Black),
                )
            }
        }
    }
}

@LayoutScopeMarker
interface GridScope {
    fun Modifier.gridPosition(position: GridPosition, size: GridSize = 1.gd by 1.gd): Modifier
}

private object GridScopeInstance : GridScope {
    override fun Modifier.gridPosition(position: GridPosition, size: GridSize): Modifier =
        this then GridPositionModifier(position, size)
}

private data class GridPositionModifier(val position: GridPosition, val size: GridSize) :
    ModifierNodeElement<GridPositionModifierElement>() {

    override fun create(): GridPositionModifierElement = GridPositionModifierElement(position, size)

    override fun update(node: GridPositionModifierElement) {
        node.position = position
        node.size = size
    }

    class GridPositionModifierElement(var position: GridPosition, var size: GridSize) :
        Modifier.Node(),
        ParentDataModifierNode {
        override fun Density.modifyParentData(parentData: Any?): Any? =
            (parentData as? GridParentData ?: GridParentData()).apply {
                position = this@GridPositionModifierElement.position
                size = this@GridPositionModifierElement.size
            }
    }
}

private class GridParentData(
    var position: GridPosition = GridPosition(0.gd, 0.gd),
    var size: GridSize = GridSize(1.gd, 1.gd),
) {
    companion object {
        val Default = GridParentData()
    }
}
