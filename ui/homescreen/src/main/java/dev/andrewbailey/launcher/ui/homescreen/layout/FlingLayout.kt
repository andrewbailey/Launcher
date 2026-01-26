package dev.andrewbailey.launcher.ui.homescreen.layout

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.TargetedFlingBehavior
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitVerticalTouchSlopOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.NestedScrollSource.Companion.UserInput
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Velocity
import dev.andrewbailey.launcher.ui.homescreen.layout.FlingLayoutExpansionState.Collapsed
import dev.andrewbailey.launcher.ui.homescreen.layout.FlingLayoutExpansionState.Expanded
import kotlinx.coroutines.CancellationException

@Composable
internal fun FlingLayout(
    draggableState: AnchoredDraggableState<FlingLayoutExpansionState>,
    modifier: Modifier = Modifier,
    onVerticalDownOverswipe: (fingers: Int) -> Unit = {},
    overscrollEffect: OverscrollEffect? = rememberOverscrollEffect(),
    flingBehavior: TargetedFlingBehavior = AnchoredDraggableDefaults.flingBehavior(
        state = draggableState,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
    ),
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .nestedScroll(rememberAnchoredDraggableScrollConnection(draggableState, flingBehavior))
            .anchoredDraggable(
                state = draggableState,
                reverseDirection = false,
                orientation = Orientation.Vertical,
                flingBehavior = flingBehavior,
                overscrollEffect = overscrollEffect,
            )
            .onVerticalDownSwipe(
                enabled = { draggableState.currentValue == Collapsed },
                action = onVerticalDownOverswipe,
            )
            .verticalSwipeHapticFeedback(LocalHapticFeedback.current) {
                draggableState.currentValue == Collapsed
            }
            .overscroll(overscrollEffect)
            .onSizeChanged {
                draggableState.updateAnchors(
                    DraggableAnchors {
                        Collapsed at it.height.toFloat()
                        Expanded at 0f
                    },
                )
            },
    ) {
        content()
    }
}

@Composable
internal fun rememberFlingLayoutAnchoredDraggableState(
    initialValue: FlingLayoutExpansionState = Collapsed,
) = rememberSaveable(saver = AnchoredDraggableState.Saver()) {
    AnchoredDraggableState(initialValue)
}

internal enum class FlingLayoutExpansionState {
    Collapsed,
    Expanded,
}

private val AnchoredDraggableState<FlingLayoutExpansionState>.isBetweenStates: Boolean
    get() {
        val progress = progress(Collapsed, Expanded)
        return progress != 0f && progress != 1f
    }

@Composable
private fun rememberAnchoredDraggableScrollConnection(
    state: AnchoredDraggableState<FlingLayoutExpansionState>,
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

private fun Modifier.onVerticalDownSwipe(
    enabled: () -> Boolean,
    action: (fingerCount: Int) -> Unit,
): Modifier = this.pointerInput(enabled, action) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        val drag = awaitVerticalTouchSlopOrCancellation(down.id) { change, over ->
            if (enabled() && over > 0) change.consume()
        }

        if (drag != null) {
            val fingerCount = currentEvent.changes.distinctBy { it.id }.count { it.pressed }
            action(fingerCount)

            do {
                val event = awaitPointerEvent()
                event.changes.forEach { it.consume() }
            } while (event.changes.any { it.pressed })
        }
    }
}

private fun Modifier.verticalSwipeHapticFeedback(
    hapticFeedback: HapticFeedback,
    hapticFeedbackType: HapticFeedbackType = HapticFeedbackType.GestureThresholdActivate,
    enabled: () -> Boolean,
): Modifier = this.pointerInput(enabled) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        awaitVerticalTouchSlopOrCancellation(down.id) { _, _ ->
            if (enabled()) {
                hapticFeedback.performHapticFeedback(hapticFeedbackType)
                throw CancellationException()
            }
        }
    }
}
