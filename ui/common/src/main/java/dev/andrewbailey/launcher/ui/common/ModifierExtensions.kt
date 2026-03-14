package dev.andrewbailey.launcher.ui.common

import androidx.compose.ui.Modifier

public inline fun Modifier.thenIf(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier =
    if (condition) {
        this.then(modifier())
    } else {
        this
    }
