package dev.andrewbailey.launcher.provider.apps

import dev.andrewbailey.launcher.model.ApplicationListing
import kotlinx.coroutines.flow.Flow

interface AppListProvider {
    fun getAllLauncherActivities(): Flow<List<ApplicationListing>>
}

