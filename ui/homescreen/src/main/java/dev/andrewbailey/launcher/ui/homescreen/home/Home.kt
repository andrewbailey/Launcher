package dev.andrewbailey.launcher.ui.homescreen.home

import android.app.WallpaperManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import dev.andrewbailey.launcher.ui.homescreen.layout.PopulatedHomeGrid
import dev.andrewbailey.launcher.ui.mediator.retainUiMediator

@Composable
internal fun Home(modifier: Modifier = Modifier) {
    val homeStateMediator = retainUiMediator<HomeStateMediator>()
    val layout = homeStateMediator.homescreenLayout.value

    if (layout != null) {
        Column(
            modifier = modifier.windowInsetsPadding(WindowInsets.systemBars),
        ) {
            val pagerState = rememberPagerState { layout.pages.size }
            WallpaperParallax {
                with(pagerState) { (currentPage + currentPageOffsetFraction) / pageCount }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                PopulatedHomeGrid(
                    iconProvider = homeStateMediator.iconProvider,
                    gridSize = layout.pageGridSize,
                    contents = layout.pages[page],
                )
            }

            Dock(
                iconProvider = homeStateMediator.iconProvider,
                gridSize = layout.dockGridSize,
                contents = layout.dock,
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
