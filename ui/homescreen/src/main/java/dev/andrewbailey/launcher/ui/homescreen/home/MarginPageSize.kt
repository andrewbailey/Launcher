package dev.andrewbailey.launcher.ui.homescreen.home

import androidx.compose.foundation.pager.PageSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

@Composable
internal fun rememberMarginPageSize(margin: () -> Dp): MarginPageSize = remember(margin) {
    MarginPageSize(margin)
}

internal class MarginPageSize(private val margin: () -> Dp) : PageSize {
    override fun Density.calculateMainAxisPageSize(availableSpace: Int, pageSpacing: Int): Int =
        availableSpace - margin().roundToPx() * 2
}
