package dev.andrewbailey.launcher.provider.icon.inject

import dev.andrewbailey.launcher.provider.icon.AppIconProvider
import dev.andrewbailey.launcher.provider.icon.AppIconProviderImpl
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo

@ContributesTo(AppScope::class)
interface Providers {
    @Binds val AppIconProviderImpl.binds: AppIconProvider
}
