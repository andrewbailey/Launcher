package dev.andrewbailey.launcher.util

import android.app.StatusBarManager
import android.content.Context

private val expandNotificationShadeMethod by lazy(LazyThreadSafetyMode.NONE) {
    StatusBarManager::class.java.getMethod("expandNotificationsPanel")
}

fun Context.expandNotificationShade() {
    val statusBarManager = getSystemService(StatusBarManager::class.java)
    expandNotificationShadeMethod.invoke(statusBarManager)
}

private val expandSettingsShadeMethod by lazy(LazyThreadSafetyMode.NONE) {
    StatusBarManager::class.java.getMethod("expandSettingsPanel")
}

fun Context.expandSettingsShade() {
    val statusBarManager = getSystemService(StatusBarManager::class.java)
    expandSettingsShadeMethod.invoke(statusBarManager)
}
