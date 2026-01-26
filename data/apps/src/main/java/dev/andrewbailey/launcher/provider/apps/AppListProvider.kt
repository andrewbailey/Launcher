package dev.andrewbailey.launcher.provider.apps

import dev.andrewbailey.launcher.model.ApplicationListing
import kotlinx.coroutines.flow.Flow

public interface AppListProvider {
    public fun getAllLauncherActivities(): Flow<List<ApplicationListing>>
}
