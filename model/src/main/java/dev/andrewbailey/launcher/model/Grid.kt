package dev.andrewbailey.launcher.model

import kotlin.math.roundToInt

data class GridPosition(val x: GridDimension, val y: GridDimension) {
    operator fun plus(other: GridPosition) = GridPosition(this.y + other.y, this.x + other.x)

    operator fun plus(other: GridSize) = GridPosition(this.y + other.width, this.x + other.height)
}

data class GridSize(val width: GridDimension, val height: GridDimension) {
    init {
        require(width >= 0.gd && height >= 0.gd) { "Width and height must be non-negative." }
    }

    operator fun contains(point: GridPosition) = point.x >= 0.gd && point.x <= width &&
        point.y >= 0.gd && point.y <= height
}

@JvmInline
value class GridDimension(val halfSteps: Int) : Comparable<GridDimension> {
    operator fun plus(other: GridDimension) = GridDimension(this.halfSteps + other.halfSteps)
    operator fun minus(other: GridDimension) = GridDimension(this.halfSteps - other.halfSteps)
    operator fun times(scalar: Int) = GridDimension(scalar * this.halfSteps)
    operator fun div(denominator: Int) = GridDimension(this.halfSteps / denominator)
    operator fun unaryMinus() = GridDimension(-this.halfSteps)

    override operator fun compareTo(other: GridDimension) =
        this.halfSteps.compareTo(other.halfSteps)

    override fun toString(): String = "${halfSteps / 2.0}.gd"

    companion object {
        @Suppress("NOTHING_TO_INLINE")
        inline operator fun invoke(fullSteps: Double) = GridDimension((fullSteps * 2).roundToInt())
    }
}

infix fun GridDimension.by(height: GridDimension) = GridSize(width = this, height = height)
infix fun GridDimension.x(y: GridDimension) = GridPosition(x = this, y = y)

inline val Int.gd get() = GridDimension(this * 2)
inline val Double.gd get() = GridDimension(this)
