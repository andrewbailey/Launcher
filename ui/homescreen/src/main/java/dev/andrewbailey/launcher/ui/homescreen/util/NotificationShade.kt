package dev.andrewbailey.launcher.ui.homescreen.util

import android.app.StatusBarManager
import android.content.Context

private val expandNotificationShadeMethod by lazy(LazyThreadSafetyMode.NONE) {
    StatusBarManager::class.java.getMethod("expandNotificationsPanel")
}

internal fun Context.expandNotificationShade() {
    val statusBarManager = getSystemService(StatusBarManager::class.java)
    expandNotificationShadeMethod.invoke(statusBarManager)
}

private val expandSettingsShadeMethod by lazy(LazyThreadSafetyMode.NONE) {
    StatusBarManager::class.java.getMethod("expandSettingsPanel")
}

internal fun Context.expandSettingsShade() {
    val statusBarManager = getSystemService(StatusBarManager::class.java)
    expandSettingsShadeMethod.invoke(statusBarManager)
}
