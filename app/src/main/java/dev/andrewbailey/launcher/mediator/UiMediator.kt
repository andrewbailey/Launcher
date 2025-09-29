package dev.andrewbailey.launcher.mediator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.RetainObserver
import androidx.compose.runtime.retain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

@Composable
fun <T : UiMediator> retainUiMediator(factory: () -> T): T =
    retain { UiMediatorWrapper(factory) }.uiMediator

private class UiMediatorWrapper<T : UiMediator>(factory: () -> T) : RetainObserver {
    val uiMediator: T = factory()

    override fun onRetained() {}
    override fun onEnteredComposition() {}
    override fun onExitedComposition() {}

    override fun onRetired() {
        uiMediator.destroy()
    }
}

abstract class UiMediator {

    protected val coroutineScope by lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    }

    internal fun destroy() {
        coroutineScope.cancel()
        onDestroy()
    }

    protected open fun onDestroy() {
    }
}
