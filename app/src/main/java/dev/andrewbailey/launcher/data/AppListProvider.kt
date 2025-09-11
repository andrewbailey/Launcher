package dev.andrewbailey.launcher.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import dev.andrewbailey.launcher.model.ApplicationListing

class AppListProvider(
    private val context: Context
) {

    private val packageManager = context.packageManager

    fun getAllLauncherActivities(): List<ApplicationListing> {
        return packageManager.queryIntentActivities(
            Intent().apply {
                action = Intent.ACTION_MAIN
                addCategory(Intent.CATEGORY_LAUNCHER)
            }, 0
        ).map {
            ApplicationListing(
                name = it.activityInfo.loadLabel(packageManager).toString(),
                packageName = it.activityInfo.packageName,
                activityClass = it.activityInfo.name
            )
        }
    }

}