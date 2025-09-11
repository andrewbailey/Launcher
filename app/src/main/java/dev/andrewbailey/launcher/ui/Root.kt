package dev.andrewbailey.launcher.ui

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.andrewbailey.launcher.ui.home.Home

@Composable
fun Root() {
    Home(
        modifier = Modifier.background(Color.Gray)
    )
}