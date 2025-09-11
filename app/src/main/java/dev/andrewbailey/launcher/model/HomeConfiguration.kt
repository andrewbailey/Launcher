package dev.andrewbailey.launcher.model

data class HomeConfiguration(
    val pageGridSize: GridDimension,
    val pages: List<List<PlacedPageElement>>,
    val dockGridSize: GridDimension,
    val dock: List<PlacedPageElement>
) {

    sealed class PlacedPageElement {
        abstract val position: GridPosition
        abstract val size: GridSize

        data class PlacedIcon(
            val app: ApplicationListing,
            override val position: GridPosition
        ) : PlacedPageElement() {
            override val size = GridSize(1.gd, 1.gd)
        }
    }
}