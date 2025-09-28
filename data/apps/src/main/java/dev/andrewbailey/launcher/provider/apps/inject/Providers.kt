package dev.andrewbailey.launcher.provider.apps.inject

import dev.andrewbailey.launcher.provider.apps.AppListProvider
import dev.andrewbailey.launcher.provider.apps.AppListProviderImpl
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo

@ContributesTo(AppScope::class)
interface Providers {
    @Binds val AppListProviderImpl.binds: AppListProvider
}