package dev.andrewbailey.launcher.model

import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable

data class ApplicationListing(val name: String, val packageName: String, val activityClass: String)

fun ApplicationListing.toComponentName() = ComponentName(packageName, activityClass)

fun ApplicationListing.toIntent() = Intent().apply { component = toComponentName() }
