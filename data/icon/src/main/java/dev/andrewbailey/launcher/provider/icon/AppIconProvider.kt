package dev.andrewbailey.launcher.provider.icon

import dev.andrewbailey.launcher.model.ApplicationIcon
import dev.andrewbailey.launcher.model.ApplicationListing
import kotlinx.coroutines.flow.Flow

public interface AppIconProvider {
    public fun getAppIcon(listing: ApplicationListing): Flow<ApplicationIcon?>
}
