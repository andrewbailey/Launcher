package dev.andrewbailey.launcher.ui.homescreen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.andrewbailey.launcher.ui.common.PopulatedHomeGrid
import dev.andrewbailey.launcher.ui.retainUiMediator

@Composable
fun Home(modifier: Modifier = Modifier) {
    val homeStateMediator = retainUiMediator<HomeStateMediator>()
    val layout = homeStateMediator.homescreenLayout.value

    if (layout != null) {
        Column(
            modifier = modifier.windowInsetsPadding(WindowInsets.systemBars),
        ) {
            HorizontalPager(
                state = rememberPagerState { layout.pages.size },
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
