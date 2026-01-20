package dev.andrewbailey.launcher.ui.homescreen.home

import dev.andrewbailey.launcher.provider.config.LauncherConfigurationProvider
import dev.andrewbailey.launcher.provider.icon.AppIconProvider
import dev.andrewbailey.launcher.ui.UiMediator
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@ContributesIntoMap(AppScope::class, binding<UiMediator>())
@ClassKey(HomeStateMediator::class)
class HomeStateMediator @Inject constructor(
    val iconProvider: AppIconProvider,
    launcherConfigurationProvider: LauncherConfigurationProvider,
) : UiMediator() {

    val homescreenLayout = launcherConfigurationProvider.getHomeConfiguration().collectAsState(null)
}
