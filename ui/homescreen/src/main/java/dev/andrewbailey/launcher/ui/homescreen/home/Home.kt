package dev.andrewbailey.launcher.ui.homescreen.home

import android.app.WallpaperManager
import android.content.Intent
import android.graphics.Paint
import android.graphics.RectF
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import dev.andrewbailey.launcher.model.HomeConfiguration
import dev.andrewbailey.launcher.provider.icon.AppIconProvider
import dev.andrewbailey.launcher.ui.dragdrop.DragDropScope
import dev.andrewbailey.launcher.ui.dragdrop.DragHotspot
import dev.andrewbailey.launcher.ui.dragdrop.DropTarget
import dev.andrewbailey.launcher.ui.homescreen.DraggableHomescreenItem
import dev.andrewbailey.launcher.ui.homescreen.R
import dev.andrewbailey.launcher.ui.homescreen.layout.PopulatedHomeGrid
import dev.andrewbailey.launcher.ui.mediator.retainUiMediator
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val PageMarginDuringDrag = 48.dp

@Composable
context(dragDropScope: DragDropScope<DraggableHomescreenItem>)
internal fun Home(modifier: Modifier = Modifier) {
    val homeStateMediator = retainUiMediator<HomeStateMediator>()
    val layout = homeStateMediator.homescreenLayout.value

    if (layout != null) {
        Box(
            modifier = modifier,
        ) {
            DragActions()

            Column {
                HomePages(
                    layout = layout,
                    iconProvider = homeStateMediator.iconProvider,
                    modifier = Modifier.weight(1f),
                )

                Dock(
                    iconProvider = homeStateMediator.iconProvider,
                    gridSize = layout.dockGridSize,
                    contents = layout.dock,
                )
            }
        }
    }
}

@Composable
context(dragDropScope: DragDropScope<DraggableHomescreenItem>)
private fun HomePages(
    layout: HomeConfiguration,
    iconProvider: AppIconProvider,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState { layout.pages.size }
    WallpaperParallax {
        with(pagerState) { (currentPage + currentPageOffsetFraction) / pageCount }
    }

    val pageEdgeMargin by animateDpAsState(
        targetValue = if (dragDropScope.isDragInProgress) PageMarginDuringDrag else 0.dp,
    )

    val coroutineScope = rememberCoroutineScope()

    HorizontalPager(
        state = pagerState,
        snapPosition = SnapPosition.Center,
        pageSpacing = animateDpAsState(
            targetValue = if (dragDropScope.isDragInProgress) 8.dp else 0.dp,
        ).value,
        contentPadding = PaddingValues(horizontal = pageEdgeMargin),
        modifier = modifier,
    ) { pageIndex ->
        DragHotspot(
            hoverAction = {
                if (pagerState.targetPage != pageIndex) {
                    delay(DragHotspot.DefaultHoverActionDelay)

                    // Perform the animation in a separate coroutineScope so that if the hover is
                    // cancelled in the middle of the scroll animation, we will still settle on the
                    // target page
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pageIndex)
                    }
                }
            },
        ) {
            HomePage(
                layout = layout,
                pageIndex = pageIndex,
                iconProvider = iconProvider,
                containerWidth = { (it + 2 * pageEdgeMargin.toPx()).roundToInt() },
                modifier = Modifier
                    .border(
                        shape = RoundedCornerShape(16.dp),
                        width = animateDpAsState(
                            if (dragDropScope.isDragInProgress) {
                                2.dp
                            } else {
                                0.dp
                            },
                        ).value,
                        color = animateColorAsState(
                            when {
                                isHovered -> Color.White
                                dragDropScope.isDragInProgress -> Color.White.copy(alpha = 0.5f)
                                else -> Color.Transparent
                            },
                        ).value,
                    )
                    .background(
                        shape = RoundedCornerShape(16.dp),
                        color = when {
                            dragDropScope.isDragInProgress -> Color.White.copy(alpha = 0.25f)
                            else -> Color.Transparent
                        },
                    ),
            )
        }
    }
}

@Composable
context(dragDropScope: DragDropScope<DraggableHomescreenItem>)
private fun HomePage(
    layout: HomeConfiguration,
    pageIndex: Int,
    iconProvider: AppIconProvider,
    containerWidth: Density.(incomingWidthConstraintPx: Int) -> Int,
    modifier: Modifier = Modifier,
) {
    var scale by remember { mutableFloatStateOf(1f) }
    PopulatedHomeGrid(
        iconProvider = iconProvider,
        gridSize = layout.pageGridSize,
        contents = layout.pages[pageIndex],
        itemScale = { scale },
        modifier = modifier
            .layout { measurable, constraints ->
                val widthOfPager = containerWidth(constraints.maxWidth)
                val aspectRatio = constraints.maxHeight.toFloat() / widthOfPager
                val height = aspectRatio * constraints.maxWidth

                scale = constraints.maxWidth.toFloat() / widthOfPager
                val placeable = measurable.measure(
                    Constraints.fixed(constraints.maxWidth, height.roundToInt()),
                )

                layout(placeable.width, placeable.height) {
                    placeable.place(0, 0)
                }
            },
    )
}

@Composable
context(dragDropScope: DragDropScope<DraggableHomescreenItem>)
private fun DragActions(modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = dragDropScope.isDragInProgress,
        modifier = modifier
            .fillMaxWidth()
            .height(PageMarginDuringDrag),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            RemoveButton()
            CancelButton()
            UninstallButton()
        }
    }
}

@Composable
context(dragDropScope: DragDropScope<DraggableHomescreenItem>)
private fun RemoveButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    HoverButton(
        icon = Icons.Default.Close,
        text = stringResource(R.string.remove_item),
    ) {
        Toast.makeText(context, "Remove", Toast.LENGTH_SHORT).show()
    }
}

@Composable
context(dragDropScope: DragDropScope<DraggableHomescreenItem>)
private fun CancelButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    HoverButton(
        icon = Icons.Default.Close,
        text = stringResource(R.string.cancel_drag),
    ) {
        Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show()
    }
}

@Composable
context(dragDropScope: DragDropScope<DraggableHomescreenItem>)
private fun UninstallButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    HoverButton(
        icon = Icons.Default.Delete,
        text = stringResource(R.string.uninstall_app),
    ) {
        val app = (it as? DraggableHomescreenItem.Icon)?.listing ?: return@HoverButton
        context.startActivity(Intent(Intent.ACTION_DELETE, "package:${app.packageName}".toUri()))
    }
}

@Composable
context(dragDropScope: DragDropScope<DraggableHomescreenItem>)
private fun HoverButton(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    action: (DraggableHomescreenItem) -> Unit,
) {
    DropTarget(
        modifier = modifier,
        dropAction = action,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val contentColor by animateColorAsState(
                targetValue = when {
                    isHovered -> Color.Red
                    else -> Color.White
                },
            )

            val shadow = with(LocalDensity.current) {
                Shadow(
                    color = Color.Black,
                    offset = Offset(0f, 0.5.dp.toPx()),
                    blurRadius = 0.5.dp.toPx(),
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.drawWithContent {
                    // Cheap visual hack to draw an unblurred shadow by rendering the icon twice
                    // with the "shadow" being a tinted translated copy of the icon.
                    translate(shadow.offset.x, shadow.offset.y) {
                        drawIntoCanvas { canvas ->
                            val paint = Paint().apply {
                                colorFilter = android.graphics.PorterDuffColorFilter(
                                    shadow.color.copy(alpha = 0.75f * shadow.color.alpha).toArgb(),
                                    android.graphics.PorterDuff.Mode.SRC_IN,
                                )
                            }

                            val bounds = RectF(0f, 0f, size.width, size.height)
                            canvas.nativeCanvas.saveLayer(bounds, paint)
                            this@drawWithContent.drawContent()
                            canvas.nativeCanvas.restore()
                        }
                    }

                    drawContent()
                },
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = text,
                color = contentColor,
                style = MaterialTheme.typography.labelLarge.copy(
                    shadow = shadow,
                ),

            )
        }
    }
}

@Composable
private fun WallpaperParallax(fraction: () -> Float) {
    val context = LocalContext.current
    val windowToken = LocalView.current.windowToken
    val wallpaperManager = remember { WallpaperManager.getInstance(context) }
    LaunchedEffect(Unit) {
        snapshotFlow { fraction() }
            .collect { fraction ->
                wallpaperManager.setWallpaperOffsets(windowToken, fraction, 0f)
            }
    }
}
