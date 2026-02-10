package dev.andrewbailey.launcher.ui.homescreen.layout

import android.graphics.Rect
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction.Cancel
import androidx.compose.foundation.interaction.PressInteraction.Press
import androidx.compose.foundation.interaction.PressInteraction.Release
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ObserverModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.observeReads
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.andrewbailey.launcher.model.ApplicationIcon
import dev.andrewbailey.launcher.model.ApplicationListing
import dev.andrewbailey.launcher.model.toIntent
import dev.andrewbailey.launcher.provider.icon.AppIconProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
internal fun LauncherIcon(
    appName: String,
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .semantics(mergeDescendants = true) {
                role = Role.Button
                contentDescription = appName
            },
    ) {
        Box(
            modifier = Modifier.size(48.dp)
                .then(
                    if (interactionSource == null) {
                        Modifier
                    } else {
                        Modifier.indication(interactionSource, LauncherIconIndication)
                    },
                ),
        ) {
            icon()
        }
        Spacer(Modifier.padding(4.dp))
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            label()
        }
    }
}

internal object LauncherIconDefaults {
    @Composable
    fun icon(
        listing: ApplicationListing,
        iconProvider: AppIconProvider,
        modifier: Modifier = Modifier,
    ) = @Composable {
        val icon = remember(iconProvider, listing) {
            iconProvider.getAppIcon(listing)
        }.collectAsState(null).value

        if (icon != null) {
            val painter = remember(icon) {
                ApplicationIconPainter(icon)
            }

            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = modifier.fillMaxSize(),
            )
        }
    }

    @Composable
    fun label(appName: String, modifier: Modifier = Modifier) = @Composable {
        Text(
            text = appName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = modifier,
            color = Color.White,
            style = TextStyle(
                shadow = with(LocalDensity.current) {
                    Shadow(
                        color = Color.Black,
                        offset = Offset(0f, 0.5.dp.toPx()),
                        blurRadius = 0.5.dp.toPx(),
                    )
                },
            ),
        )
    }

    @Composable
    fun launchActivityAction(listing: ApplicationListing): () -> Unit {
        val context = LocalContext.current
        return { context.startActivity(listing.toIntent()) }
    }
}

internal class ApplicationIconPainter(val icon: ApplicationIcon) : Painter() {
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

private object LauncherIconIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode =
        ScalingIndicationNode(interactionSource)

    override fun equals(other: Any?) = other === LauncherIconIndication
    override fun hashCode() = System.identityHashCode(this)
}

private class ScalingIndicationNode(private val interactionSource: InteractionSource) :
    Modifier.Node(),
    DrawModifierNode,
    CompositionLocalConsumerModifierNode,
    ObserverModifierNode {

    private var scale = Animatable(SCALE_INACTIVE)
    private var isTouchActive = false

    private var longPressDurationMs = 0

    private var animationJob: Job? = null
        set(value) {
            field?.cancel()
            field = value
        }

    private fun animateTo(isTouched: Boolean) {
        this.isTouchActive = isTouched
        val targetScale = if (isTouched) SCALE_ACTIVE else SCALE_INACTIVE
        val animationDuration = if (isTouched) longPressDurationMs else IDLE_ANIMATION_DURATION_MS

        if (scale.value != targetScale) {
            animationJob = coroutineScope.launch {
                scale.animateTo(
                    targetValue = targetScale,
                    animationSpec = tween(animationDuration),
                )
            }
        }
    }

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is Press -> animateTo(true)
                    is Release, is Cancel -> animateTo(false)
                }
            }
        }

        // This is a hack to support being used in movable content. When dragging start, the
        // indication gets detached and reattached in the same frame, which briefly cancels the
        // animation coroutine. Restart an animation to the current state if necessary to restart
        // the animation that was cancelled on the MovableContent state transition.
        animateTo(isTouchActive)

        observeReads {
            onObservedReadsChanged()
        }
    }

    override fun onDetach() {
        animationJob?.cancel()
        animationJob = null
    }

    override fun onReset() {
        super.onReset()
        isTouchActive = false
        scale = Animatable(SCALE_INACTIVE)
    }

    override fun onObservedReadsChanged() {
        longPressDurationMs = currentValueOf(LocalViewConfiguration).longPressTimeoutMillis.toInt()
    }

    override fun ContentDrawScope.draw() {
        val centeringFactor = (1 - scale.value) / 2f
        drawContext.canvas.translate(size.width * centeringFactor, size.height * centeringFactor)
        drawContext.canvas.scale(scale.value)
        drawContent()
    }

    private companion object {
        const val SCALE_INACTIVE = 1f
        const val SCALE_ACTIVE = 1.10f

        const val IDLE_ANIMATION_DURATION_MS = 300
    }
}
