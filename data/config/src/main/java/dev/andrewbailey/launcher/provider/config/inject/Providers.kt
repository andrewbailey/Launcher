package dev.andrewbailey.launcher.provider.config.inject

import dev.andrewbailey.launcher.provider.config.LauncherConfigurationProvider
import dev.andrewbailey.launcher.provider.config.LauncherConfigurationProviderImpl
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo

@ContributesTo(AppScope::class)
interface Providers {
    @Binds val LauncherConfigurationProviderImpl.binds: LauncherConfigurationProvider
}
