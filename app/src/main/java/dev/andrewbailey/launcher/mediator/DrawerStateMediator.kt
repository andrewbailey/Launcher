package dev.andrewbailey.launcher.mediator

import dev.andrewbailey.launcher.provider.apps.AppListProvider
import dev.andrewbailey.launcher.provider.icon.AppIconProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject

@ContributesIntoMap(AppScope::class)
@ClassKey(DrawerStateMediator::class)
class DrawerStateMediator @Inject constructor(
    val iconProvider: AppIconProvider,
    val appListProvider: AppListProvider,
) : UiMediator()
