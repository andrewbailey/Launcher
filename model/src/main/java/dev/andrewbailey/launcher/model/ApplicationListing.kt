package dev.andrewbailey.launcher.model

import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable

data class ApplicationListing(val name: String, val packageName: String, val activityClass: String)

fun ApplicationListing.toComponentName() = ComponentName(packageName, activityClass)

fun ApplicationListing.toIntent() = Intent().apply {
    component = toComponentName()
    action = Intent.ACTION_MAIN
    addCategory(Intent.CATEGORY_LAUNCHER)
    setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
}
