package dev.andrewbailey.launcher.ui.homescreen.drawer

import dev.andrewbailey.launcher.provider.apps.AppListProvider
import dev.andrewbailey.launcher.provider.icon.AppIconProvider
import dev.andrewbailey.launcher.ui.UiMediator
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
