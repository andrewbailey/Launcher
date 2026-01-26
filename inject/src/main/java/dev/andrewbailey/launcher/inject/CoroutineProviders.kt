package dev.andrewbailey.launcher.inject

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

@ContributesTo(AppScope::class)
public interface CoroutineProviders {
    @SingleIn(AppScope::class)
    @GlobalBackgroundScope
    @Provides
    public fun globalBackgroundJob(): Job = Job()

    @SingleIn(AppScope::class)
    @GlobalBackgroundScope
    @Provides
    public fun globalBackgroundCoroutineScope(@GlobalBackgroundScope job: Job): CoroutineScope =
        CoroutineScope(Dispatchers.Default + job)
}
