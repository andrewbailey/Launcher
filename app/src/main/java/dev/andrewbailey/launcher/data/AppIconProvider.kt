package dev.andrewbailey.launcher.data

import android.content.Context
import dev.andrewbailey.launcher.model.ApplicationIcon
import dev.andrewbailey.launcher.model.ApplicationListing
import dev.andrewbailey.launcher.model.toComponentName

class AppIconProvider(
    context: Context
) {

    private val packageManager = context.packageManager

    fun getAppIcon(listing: ApplicationListing): ApplicationIcon {
        return ApplicationIcon(packageManager.getActivityIcon(listing.toComponentName()))
    }
}
