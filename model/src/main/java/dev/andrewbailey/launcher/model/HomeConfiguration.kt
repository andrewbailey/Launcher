package dev.andrewbailey.launcher.model

public data class HomeConfiguration(
    val pageGridSize: GridSize,
    val pages: List<List<PlacedPageElement>>,
    val dockGridSize: GridSize,
    val dock: List<PlacedPageElement>,
) {

    public sealed class PlacedPageElement {
        public abstract val position: GridPosition
        public abstract val size: GridSize

        public data class PlacedIcon(
            val app: ApplicationListing,
            override val position: GridPosition,
        ) : PlacedPageElement() {
            override val size: GridSize = GridSize(1.gd, 1.gd)
        }
    }
}
