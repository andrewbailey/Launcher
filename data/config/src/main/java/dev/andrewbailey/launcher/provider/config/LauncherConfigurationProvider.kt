package dev.andrewbailey.launcher.provider.config

import dev.andrewbailey.launcher.model.HomeConfiguration
import kotlinx.coroutines.flow.Flow

interface LauncherConfigurationProvider {
    fun getHomeConfiguration(): Flow<HomeConfiguration>
}
