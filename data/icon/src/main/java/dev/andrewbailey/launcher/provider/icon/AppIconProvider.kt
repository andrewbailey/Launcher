package dev.andrewbailey.launcher.provider.icon

import android.content.Context
import android.content.pm.PackageManager
import dev.andrewbailey.launcher.model.ApplicationIcon
import dev.andrewbailey.launcher.model.ApplicationListing
import dev.andrewbailey.launcher.model.toComponentName
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

interface AppIconProvider {
    fun getAppIcon(listing: ApplicationListing): Flow<ApplicationIcon?>
}
