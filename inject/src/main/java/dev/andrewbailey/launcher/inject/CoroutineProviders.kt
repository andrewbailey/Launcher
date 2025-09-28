package dev.andrewbailey.launcher.inject

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

@ContributesTo(AppScope::class)
interface CoroutineProviders {
    @SingleIn(AppScope::class)
    @GlobalBackgroundScope
    @Provides
    fun globalBackgroundJob(): Job = Job()

    @SingleIn(AppScope::class)
    @GlobalBackgroundScope
    @Provides
    fun globalBackgroundCoroutineScope(@GlobalBackgroundScope job: Job): CoroutineScope {
        return CoroutineScope(Dispatchers.Default + job)
    }
}