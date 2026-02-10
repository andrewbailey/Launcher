package dev.andrewbailey.launcher.ui.homescreen

import dev.andrewbailey.launcher.model.ApplicationListing

internal sealed class DraggableHomescreenItem {
    class Icon(val listing: ApplicationListing) : DraggableHomescreenItem()
}
