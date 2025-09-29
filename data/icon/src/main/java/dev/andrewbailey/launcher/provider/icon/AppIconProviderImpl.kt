package dev.andrewbailey.launcher.provider.icon

import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.collection.LruCache
import androidx.collection.MutableScatterMap
import androidx.collection.ScatterMap
import androidx.collection.emptyScatterMap
import androidx.collection.mutableScatterMapOf
import dev.andrewbailey.launcher.inject.GlobalBackgroundScope
import dev.andrewbailey.launcher.model.ApplicationIcon
import dev.andrewbailey.launcher.model.ApplicationListing
import dev.andrewbailey.launcher.model.toComponentName
import dev.andrewbailey.launcher.provider.apps.AppListProvider
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.coroutines.processNextEventInCurrentThread

class AppIconProviderImpl @Inject constructor(
    private val appListProvider: AppListProvider,
    private val packageManager: PackageManager,
    @GlobalBackgroundScope scope: CoroutineScope,
) : AppIconProvider {

    private val cache = appListProvider.getAllLauncherActivities()
        .mapLatestWithPrevious(emptyScatterMap<String, ApplicationIcon>()) { acc, next ->
            MutableScatterMap<String, ApplicationIcon>().apply {
                next.forEach { listing ->
                    val component = listing.componentString
                    put(component, acc[component] ?: loadIcon(listing))
                }
            }
        }
        .stateIn(scope, SharingStarted.Lazily, emptyScatterMap())

    override fun getAppIcon(listing: ApplicationListing): Flow<ApplicationIcon?> = cache.map {
        it[listing.componentString]
    }

    private fun loadIcon(listing: ApplicationListing): ApplicationIcon =
        ApplicationIcon(packageManager.getActivityIcon(listing.toComponentName()))

    private val ApplicationListing.componentString
        get() = "$packageName/$activityClass"
}

private fun <T, R> Flow<T>.mapLatestWithPrevious(
    initial: R,
    map: (prev: R, next: T) -> R,
): Flow<R> {
    var previous = initial
    return transform { next ->
        previous = map(previous, next)
        emit(previous)
    }
}
