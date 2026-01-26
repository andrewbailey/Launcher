package dev.andrewbailey.launcher.model

import kotlin.math.roundToInt

public data class GridPosition(val x: GridDimension, val y: GridDimension) {
    public operator fun plus(other: GridPosition): GridPosition = GridPosition(
        this.y + other.y,
        this.x + other.x,
    )

    public operator fun plus(other: GridSize): GridPosition = GridPosition(
        this.y + other.width,
        this.x + other.height,
    )
}

public data class GridSize(val width: GridDimension, val height: GridDimension) {
    init {
        require(width >= 0.gd && height >= 0.gd) { "Width and height must be non-negative." }
    }

    public operator fun contains(point: GridPosition): Boolean =
        point.x >= 0.gd && point.x <= width &&
            point.y >= 0.gd && point.y <= height
}

@JvmInline
public value class GridDimension(public val halfSteps: Int) : Comparable<GridDimension> {

    public operator fun plus(other: GridDimension): GridDimension =
        GridDimension(this.halfSteps + other.halfSteps)

    public operator fun minus(other: GridDimension): GridDimension =
        GridDimension(this.halfSteps - other.halfSteps)

    public operator fun times(scalar: Int): GridDimension = GridDimension(scalar * this.halfSteps)

    public operator fun div(denominator: Int): GridDimension =
        GridDimension(this.halfSteps / denominator)

    public operator fun unaryMinus(): GridDimension = GridDimension(-this.halfSteps)

    override operator fun compareTo(other: GridDimension): Int =
        this.halfSteps.compareTo(other.halfSteps)

    override fun toString(): String = "${halfSteps / 2.0}.gd"

    public companion object {
        @Suppress("NOTHING_TO_INLINE")
        public inline operator fun invoke(fullSteps: Double): GridDimension =
            GridDimension((fullSteps * 2).roundToInt())
    }
}

public infix fun GridDimension.by(height: GridDimension): GridSize =
    GridSize(width = this, height = height)

public infix fun GridDimension.x(y: GridDimension): GridPosition = GridPosition(x = this, y = y)

public inline val Int.gd: GridDimension
    get() = GridDimension(this * 2)

public inline val Double.gd: GridDimension
    get() = GridDimension(this)
