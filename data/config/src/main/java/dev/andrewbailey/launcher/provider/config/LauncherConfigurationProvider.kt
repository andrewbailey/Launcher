package dev.andrewbailey.launcher.provider.config

import dev.andrewbailey.launcher.model.HomeConfiguration
import kotlinx.coroutines.flow.Flow

public interface LauncherConfigurationProvider {
    public fun getHomeConfiguration(): Flow<HomeConfiguration>
}
