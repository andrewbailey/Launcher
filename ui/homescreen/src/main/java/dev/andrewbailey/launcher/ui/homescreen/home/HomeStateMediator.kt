package dev.andrewbailey.launcher.ui.homescreen.home

import dev.andrewbailey.launcher.provider.config.LauncherConfigurationProvider
import dev.andrewbailey.launcher.provider.icon.AppIconProvider
import dev.andrewbailey.launcher.ui.mediator.MediatorClass
import dev.andrewbailey.launcher.ui.mediator.UiMediator
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoMap
import dev.zacsweers.metro.Provides

internal class HomeStateMediator(
    val iconProvider: AppIconProvider,
    launcherConfigurationProvider: LauncherConfigurationProvider,
) : UiMediator() {

    val homescreenLayout = launcherConfigurationProvider.getHomeConfiguration().collectAsState(null)
}

@ContributesTo(AppScope::class)
@BindingContainer
public object HomeStateMediatorBinding {
    @Provides
    @IntoMap
    @MediatorClass(HomeStateMediator::class)
    public fun homeStateMediator(
        iconProvider: AppIconProvider,
        launcherConfigurationProvider: LauncherConfigurationProvider,
    ): UiMediator = HomeStateMediator(iconProvider, launcherConfigurationProvider)
}
