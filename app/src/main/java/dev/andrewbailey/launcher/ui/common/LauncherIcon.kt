package dev.andrewbailey.launcher.ui.common

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.andrewbailey.launcher.data.AppIconProvider
import dev.andrewbailey.launcher.model.ApplicationListing
import dev.andrewbailey.launcher.model.toIntent

@Composable
fun LauncherIcon(
    listing: ApplicationListing,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = launchActivityAction(listing)
) {
    val context = LocalContext.current
    val iconPainter = remember(listing) {
        ApplicationIconPainter(AppIconProvider(context).getAppIcon(listing))
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
    ) {
        Image(
            painter = iconPainter,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.size(48.dp)
                .clickable(enabled = onClick != null, onClick = onClick ?: {})
        )
        Spacer(Modifier.padding(4.dp))
        Text(
            text = listing.name,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier.weight(1f).fillMaxWidth()
        )
    }
}

@Composable
private fun launchActivityAction(listing: ApplicationListing): () -> Unit {
    val context = LocalContext.current
    return { context.startActivity(listing.toIntent()) }
}