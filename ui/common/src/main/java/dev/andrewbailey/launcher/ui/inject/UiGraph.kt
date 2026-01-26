package dev.andrewbailey.launcher.ui.inject

import dev.andrewbailey.launcher.ui.mediator.UiMediator
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provider
import kotlin.reflect.KClass

public interface UiGraph {

    @Multibinds
    public val mediatorProviders: Map<KClass<out UiMediator>, Provider<UiMediator>>
}
