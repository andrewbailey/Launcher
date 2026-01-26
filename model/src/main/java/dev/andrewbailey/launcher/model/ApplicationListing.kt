package dev.andrewbailey.launcher.model

import android.content.ComponentName
import android.content.Intent

public data class ApplicationListing(
    val name: String,
    val packageName: String,
    val activityClass: String,
)

public fun ApplicationListing.toComponentName(): ComponentName =
    ComponentName(packageName, activityClass)

public fun ApplicationListing.toIntent(): Intent = Intent().apply {
    component = toComponentName()
    action = Intent.ACTION_MAIN
    addCategory(Intent.CATEGORY_LAUNCHER)
    setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
}
