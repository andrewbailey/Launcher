package dev.andrewbailey.launcher.ui.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.andrewbailey.launcher.model.ApplicationListing
import dev.andrewbailey.launcher.model.toIntent
import dev.andrewbailey.launcher.provider.icon.AppIconProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun LauncherIcon(
    appName: String,
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val clickInteractionSource = remember { MutableInteractionSource() }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .semantics { contentDescription = appName }
            .clickable(
                enabled = onClick != null,
                indication = null,
                onClick = onClick ?: {},
                interactionSource = clickInteractionSource
            )
    ) {
        Box(
            modifier = Modifier.size(48.dp)
                .indication(clickInteractionSource, LauncherIconIndication)
        ) {
            icon()
        }
        Spacer(Modifier.padding(4.dp))
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            label()
        }
    }
}

object LauncherIconDefaults {
    @Composable
    fun icon(
        listing: ApplicationListing,
        iconProvider: AppIconProvider
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
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    @Composable
    fun label(
        appName: String
    ) = @Composable {
        Text(
            text = appName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }

    @Composable
    fun launchActivityAction(listing: ApplicationListing): () -> Unit {
        val context = LocalContext.current
        return { context.startActivity(listing.toIntent()) }
    }
}

private object LauncherIconIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return OverlayIndicationNode(interactionSource)
    }

    override fun equals(other: Any?) = other === LauncherIconIndication
    override fun hashCode() = System.identityHashCode(this)
}

private class OverlayIndicationNode(
    private val interactionSource: InteractionSource
) : Modifier.Node(), DrawModifierNode {

    private var currentAlpha = Animatable(0f)
    private var pressJob: Job? = null
    private val layerPaint = Paint() // Reusable Paint for saveLayer

    private fun animateTo(targetAlpha: Float) {
        pressJob?.cancel()
        pressJob = coroutineScope.launch {
            currentAlpha.animateTo(
                targetValue = targetAlpha,
                animationSpec = tween(durationMillis = ANIMATION_DURATION_MS)
            )
        }
    }

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> animateTo(PRESSED_TARGET_ALPHA)
                    is PressInteraction.Release -> animateTo(0f)
                    is PressInteraction.Cancel -> animateTo(0f)
                }
            }
        }
    }

    override fun onDetach() {
        pressJob?.cancel()
        pressJob = null
    }

    override fun ContentDrawScope.draw() {
        val alpha = currentAlpha.value
        if (alpha > 0f) {
            // Save a layer to apply the blend mode correctly
            drawContext.canvas.saveLayer(size.toRect(), layerPaint)

            // Draw the original content (e.g., the icon)
            drawContent()

            // Draw the white overlay with SrcAtop blend mode.
            // This will draw the white color (with its alpha) only where the content
            // (destination) has been drawn, effectively clipping the overlay to the content's shape.
            drawRect(
                color = Color.White,
                alpha = alpha, // The overlay's own alpha
                blendMode = BlendMode.SrcAtop
            )

            // Restore the layer, merging it with the previous content
            drawContext.canvas.restore()
        } else {
            // If no overlay is needed, just draw the content directly
            drawContent()
        }
    }

    companion object {
        private const val ANIMATION_DURATION_MS = 300
        private const val PRESSED_TARGET_ALPHA = 0.45f
    }
}
