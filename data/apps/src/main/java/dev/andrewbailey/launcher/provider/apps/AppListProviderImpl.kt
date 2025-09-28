package dev.andrewbailey.launcher.provider.apps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import dev.andrewbailey.launcher.inject.GlobalBackgroundScope
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlin.collections.map

class AppListProviderImpl @Inject constructor(
    context: Context,
    private val packageManager: PackageManager,
    @GlobalBackgroundScope scope: CoroutineScope
) : AppListProvider {

    private val allActivities = packagesChangedPing(context)
        .map { packageManager.queryMainActivities().map { ApplicationListing(it) } }
        .stateIn(scope, SharingStarted.Companion.Lazily, null)

    override fun getAllLauncherActivities() = allActivities.filterNotNull()

    private fun PackageManager.queryMainActivities() = queryIntentActivities(
        Intent().apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
        }, 0
    )

    private fun ApplicationListing(info: ResolveInfo) =
        dev.andrewbailey.launcher.model.ApplicationListing(
            name = info.activityInfo.loadLabel(packageManager).toString(),
            packageName = info.activityInfo.packageName,
            activityClass = info.activityInfo.name
        )

    private fun packagesChangedPing(context: Context): Flow<Unit> {
        return callbackFlow {
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_PACKAGE_ADDED)
                addAction(Intent.ACTION_PACKAGE_REMOVED)
                addAction(Intent.ACTION_PACKAGE_REPLACED)
                addAction(Intent.ACTION_PACKAGE_CHANGED)
            }

            val broadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    channel.trySend(Unit)
                }
            }

            channel.trySend(Unit)
            context.registerReceiver(broadcastReceiver, filter)
            awaitClose {
                context.unregisterReceiver(broadcastReceiver)
            }
        }
    }

}