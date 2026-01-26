package dev.andrewbailey.launcher.ui.homescreen.drawer

import dev.andrewbailey.launcher.provider.apps.AppListProvider
import dev.andrewbailey.launcher.provider.icon.AppIconProvider
import dev.andrewbailey.launcher.ui.mediator.MediatorClass
import dev.andrewbailey.launcher.ui.mediator.UiMediator
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoMap
import dev.zacsweers.metro.Provides

internal class DrawerStateMediator(
    val iconProvider: AppIconProvider,
    val appListProvider: AppListProvider,
) : UiMediator()

@ContributesTo(AppScope::class)
@BindingContainer
public object DrawerStateMediatorBinding {
    @Provides
    @IntoMap
    @MediatorClass(DrawerStateMediator::class)
    public fun drawerStateMediator(
        iconProvider: AppIconProvider,
        appListProvider: AppListProvider,
    ): UiMediator = DrawerStateMediator(iconProvider, appListProvider)
}
