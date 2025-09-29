package dev.andrewbailey.launcher.ui.common

import android.graphics.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import dev.andrewbailey.launcher.model.ApplicationIcon

class ApplicationIconPainter(val icon: ApplicationIcon) : Painter() {
    override val intrinsicSize: Size
        get() = Size(
            width = icon.drawable.intrinsicWidth.toFloat(),
            height = icon.drawable.intrinsicHeight.toFloat(),
        )

    override fun DrawScope.onDraw() {
        drawIntoCanvas {
            val drawable = icon.drawable
            drawable.bounds = Rect(
                (center.x - size.width / 2).toInt(),
                (center.y - size.height / 2).toInt(),
                (center.x + size.width / 2).toInt(),
                (center.y + size.width / 2).toInt(),
            )
            drawable.draw(it.nativeCanvas)
        }
    }
}
