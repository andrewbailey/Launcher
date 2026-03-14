package dev.andrewbailey.launcher.ui.homescreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import dev.andrewbailey.launcher.ui.common.thenIf
import dev.andrewbailey.launcher.ui.dragdrop.DragDropSurface
import dev.andrewbailey.launcher.ui.homescreen.drawer.Drawer
import dev.andrewbailey.launcher.ui.homescreen.home.Home
import dev.andrewbailey.launcher.ui.homescreen.layout.FlingLayout
import dev.andrewbailey.launcher.ui.homescreen.layout.FlingLayoutExpansionState.Collapsed
import dev.andrewbailey.launcher.ui.homescreen.layout.FlingLayoutExpansionState.Expanded
import dev.andrewbailey.launcher.ui.homescreen.layout.rememberFlingLayoutAnchoredDraggableState
import dev.andrewbailey.launcher.ui.homescreen.util.expandNotificationShade
import dev.andrewbailey.launcher.ui.homescreen.util.expandSettingsShade
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlinx.coroutines.launch

@Composable
public fun HomescreenRoot(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val draggableState = rememberFlingLayoutAnchoredDraggableState()

    val coroutineScope = rememberCoroutineScope()
    BackHandler(enabled = draggableState.currentValue != Collapsed) {
        coroutineScope.launch {
            draggableState.animateTo(Collapsed)
        }
    }

    @Suppress("RemoveExplicitTypeArguments")
    DragDropSurface<DraggableHomescreenItem>(modifier) {
        FlingLayout(
            draggableState = draggableState,
            onVerticalDownOverswipe = { fingers ->
                if (fingers == 1) {
                    context.expandNotificationShade()
                } else {
                    context.expandSettingsShade()
                }
            },
        ) {
            Home(
                modifier = Modifier
                    .thenIf(draggableState.currentValue == Collapsed) { systemGestureExclusion() }
                    .offset {
                        val targetOffset = draggableState.anchors.positionOf(Collapsed) -
                            draggableState.anchors.positionOf(Expanded)
                        val transitionPercent = draggableState.progress(Collapsed, Expanded)

                        val tween = -sigmoid(transitionPercent, 0.5f, 4f)
                        IntOffset(x = 0, y = (targetOffset * tween).roundToInt())
                    }
                    .graphicsLayer {
                        val transitionPercent = draggableState.progress(Collapsed, Expanded)

                        transformOrigin = TransformOrigin(0.5f, 0.5f)
                        alpha = 1 - sigmoid(transitionPercent, 4f, 0f)
                        scaleX = 1 - sigmoid(transitionPercent, 0.2f, 20f)
                        scaleY = scaleX
                    }
                    .windowInsetsPadding(WindowInsets.systemBars),
            )

            Drawer(
                modifier = Modifier
                    .offset { IntOffset(x = 0, y = draggableState.offset.toInt()) }
                    .graphicsLayer {
                        val transitionPercent = draggableState.progress(Collapsed, Expanded)
                        alpha = sigmoid(transitionPercent, 4f, 0f)
                    },
                contentPaddingValues = WindowInsets.systemBars.asPaddingValues(),
            )

            if (isDragInProgress) {
                LaunchedEffect(Unit) { draggableState.animateTo(Collapsed) }
            }
        }
    }
}

private fun sigmoid(x: Float, a: Float, b: Float) = a * x / sqrt(1 + (a * b * x).pow(2))
