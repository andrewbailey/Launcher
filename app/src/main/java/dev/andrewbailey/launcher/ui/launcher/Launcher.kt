package dev.andrewbailey.launcher.ui.launcher

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.TargetedFlingBehavior
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.NestedScrollSource.Companion.UserInput
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import dev.andrewbailey.launcher.ui.launcher.drawer.Drawer
import dev.andrewbailey.launcher.ui.launcher.home.Home

@Composable
fun Launcher(modifier: Modifier = Modifier) {
    val dragState = rememberSaveable(saver = AnchoredDraggableState.Saver()) {
        AnchoredDraggableState(
            initialValue = DrawerState.Expanded,
        )
    }
    val flingBehavior = AnchoredDraggableDefaults.flingBehavior(
        state = dragState,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
    )

    val overscrollEffect = rememberOverscrollEffect()

    Box(
        modifier = modifier
            .nestedScroll(rememberAnchoredDraggableScrollConnection(dragState, flingBehavior))
            .anchoredDraggable(
                state = dragState,
                reverseDirection = false,
                orientation = Orientation.Vertical,
                flingBehavior = flingBehavior,
                overscrollEffect = overscrollEffect,
            )
            .overscroll(overscrollEffect)
            .onSizeChanged {
                dragState.updateAnchors(
                    DraggableAnchors {
                        DrawerState.Expanded at it.height.toFloat()
                        DrawerState.Collapsed at 0f
                    },
                )
            },
    ) {
        Home(
            modifier = Modifier
                .offset {
                    val inverseOffset = dragState.offset -
                        dragState.anchors.positionOf(DrawerState.Expanded)

                    IntOffset(x = 0, y = inverseOffset.toInt() / 4)
                }
                .graphicsLayer {
                    val visibility = dragState.progress(DrawerState.Collapsed, DrawerState.Expanded)
                    alpha = visibility * 2.25f - 1
                },
        )

        Drawer(
            modifier = Modifier
                .offset { IntOffset(x = 0, y = dragState.offset.toInt()) }
                .graphicsLayer {
                    val visibility = dragState.progress(DrawerState.Expanded, DrawerState.Collapsed)
                    alpha = visibility * 2.25f - 1
                },
        )
    }
}

@Composable
private fun rememberAnchoredDraggableScrollConnection(
    state: AnchoredDraggableState<DrawerState>,
    flingBehavior: TargetedFlingBehavior,
) = remember(state, flingBehavior) {
    object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset =
            if (source == UserInput && state.isBetweenStates) {
                Offset(0f, state.dispatchRawDelta(available.y))
            } else {
                Offset.Zero
            }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset = if (source == UserInput && available.y > 0) {
            Offset(0f, state.dispatchRawDelta(available.y))
        } else {
            Offset.Zero
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            if (state.isBetweenStates) {
                var consumedVelocity = 0f
                state.anchoredDrag {
                    val scrollFlingScope =
                        object : ScrollScope {
                            override fun scrollBy(pixels: Float): Float {
                                dragTo(state.offset + pixels)
                                return pixels
                            }
                        }

                    consumedVelocity = with(flingBehavior) {
                        scrollFlingScope.performFling(available.y)
                    }
                }
                return Velocity(0f, consumedVelocity)
            }
            return Velocity.Zero
        }
    }
}

private val AnchoredDraggableState<DrawerState>.isBetweenStates: Boolean
    get() {
        val progress = progress(DrawerState.Expanded, DrawerState.Collapsed)
        return progress != 0f && progress != 1f
    }

private enum class DrawerState {
    Expanded,
    Collapsed,
}
