package dev.andrewbailey.launcher.ui.homescreen

import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import dev.andrewbailey.launcher.ui.homescreen.drawer.Drawer
import dev.andrewbailey.launcher.ui.homescreen.home.Home
import dev.andrewbailey.launcher.ui.homescreen.layout.FlingLayout
import dev.andrewbailey.launcher.ui.homescreen.layout.FlingLayoutExpansionState.Collapsed
import dev.andrewbailey.launcher.ui.homescreen.layout.FlingLayoutExpansionState.Expanded
import dev.andrewbailey.launcher.ui.homescreen.layout.rememberFlingLayoutAnchoredDraggableState
import dev.andrewbailey.launcher.ui.homescreen.util.expandNotificationShade
import dev.andrewbailey.launcher.ui.homescreen.util.expandSettingsShade

@Composable
public fun HomescreenRoot(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val draggableState = rememberFlingLayoutAnchoredDraggableState()

    FlingLayout(
        draggableState = draggableState,
        modifier = modifier,
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
                .offset {
                    val inverseOffset = draggableState.offset -
                        draggableState.anchors.positionOf(Collapsed)

                    IntOffset(x = 0, y = inverseOffset.toInt() / 4)
                }
                .graphicsLayer {
                    val visibility = draggableState.progress(Expanded, Collapsed)
                    alpha = visibility * 2.25f - 1
                },
        )

        Drawer(
            modifier = Modifier
                .offset { IntOffset(x = 0, y = draggableState.offset.toInt()) }
                .graphicsLayer {
                    val visibility = draggableState.progress(Collapsed, Expanded)
                    alpha = visibility * 2.25f - 1
                },
        )
    }
}
