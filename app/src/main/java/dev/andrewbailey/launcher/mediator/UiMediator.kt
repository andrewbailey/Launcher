package dev.andrewbailey.launcher.mediator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.RetainObserver
import androidx.compose.runtime.retain.retain
import dev.andrewbailey.launcher.inject.LocalDependencyGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
inline fun <reified T : UiMediator> retainUiMediator(): T {
    val dependencyGraph = LocalDependencyGraph.current
    return retainUiMediator {
        dependencyGraph.mediatorProviders[T::class]?.invoke() as T
    }
}

@Composable
fun <T : UiMediator> retainUiMediator(factory: () -> T): T =
    retain { UiMediatorWrapper(factory) }.uiMediator

private class UiMediatorWrapper<T : UiMediator>(factory: () -> T) : RetainObserver {
    val uiMediator: T = factory()

    override fun onRetained() {}
    override fun onEnteredComposition() {}
    override fun onExitedComposition() {}

    override fun onUnused() {
        uiMediator.destroy()
    }

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

    protected fun <T : R, R> Flow<T>.collectAsState(initial: R): State<R> {
        val state = mutableStateOf(initial)
        coroutineScope.launch(Dispatchers.Main) {
            collect { state.value = it }
        }
        return state
    }
}
