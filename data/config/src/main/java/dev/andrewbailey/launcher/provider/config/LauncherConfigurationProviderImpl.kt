package dev.andrewbailey.launcher.provider.config

import dev.andrewbailey.launcher.inject.GlobalBackgroundScope
import dev.andrewbailey.launcher.model.ApplicationListing
import dev.andrewbailey.launcher.model.GridPosition
import dev.andrewbailey.launcher.model.HomeConfiguration
import dev.andrewbailey.launcher.model.HomeConfiguration.PlacedPageElement
import dev.andrewbailey.launcher.model.HomeConfiguration.PlacedPageElement.PlacedIcon
import dev.andrewbailey.launcher.model.by
import dev.andrewbailey.launcher.model.gd
import dev.andrewbailey.launcher.model.x
import dev.andrewbailey.launcher.provider.apps.AppListProvider
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LauncherConfigurationProviderImpl @Inject constructor(
    private val appListProvider: AppListProvider,
    @GlobalBackgroundScope scope: CoroutineScope,
) : LauncherConfigurationProvider {

    private val configuration = MutableStateFlow<HomeConfiguration?>(null)

    init {
        scope.launch {
            val apps = appListProvider.getAllLauncherActivities().first()

            configuration.value = HomeConfiguration(
                pageGridSize = 4.gd by 6.gd,
                pages = buildList {
                    this += apps.buildAppsList(
                        "com.google.android.googlequicksearchbox" to (0.5.gd x 2.5.gd),
                        "com.google.android.apps.weather" to (1.5.gd x 2.5.gd),
                        "au.com.shiftyjelly.pocketcasts" to (2.5.gd x 2.5.gd),

                        "com.Slack" to (0.5.gd x 3.5.gd),
                        "com.spotify.music" to (1.5.gd x 3.5.gd),
                        "com.google.android.apps.photos" to (2.5.gd x 3.5.gd),

                        "com.microsoft.todos" to (0.5.gd x 4.5.gd),
                        "reddit.news" to (1.5.gd x 4.5.gd),
                        "com.google.android.apps.magazines" to (2.5.gd x 4.5.gd),
                    )

                    this += apps.buildAppsList(
                        "com.android.vending" to (0.5.gd x 2.5.gd),
                        "com.zhiliaoapp.musically" to (1.5.gd x 2.5.gd),
                        "com.google.android.youtube" to (2.5.gd x 2.5.gd),

                        "com.google.android.calculator" to (0.5.gd x 3.5.gd),
                        "com.google.android.apps.maps" to (1.5.gd x 3.5.gd),
                        "com.truebill" to (2.5.gd x 3.5.gd),

                        "com.google.android.apps.nbu.files" to (0.5.gd x 4.5.gd),
                        "com.devhd.feedly" to (1.5.gd x 4.5.gd),
                        "com.google.android.apps.fitness" to (2.5.gd x 4.5.gd),
                    )
                },
                dockGridSize = 5.gd by 1.gd,
                dock = apps.buildAppsList(
                    "com.google.android.dialer" to (0.gd x 0.gd),
                    "com.android.chrome" to (1.gd x 0.gd),
                    "com.google.android.gm" to (2.gd x 0.gd),
                    "com.google.android.apps.messaging" to (3.gd x 0.gd),
                    "dev.andrewbailey.music" to (4.gd x 0.gd),
                ),
            )
        }
    }

    override fun getHomeConfiguration(): Flow<HomeConfiguration> = configuration.filterNotNull()

    private fun List<ApplicationListing>.buildAppsList(
        vararg positions: Pair<String, GridPosition>,
    ) = buildList<PlacedPageElement> {
        positions.forEach { (packageName, position) ->
            addIfNotNull(placedIconForPackage(packageName, position))
        }
    }

    private fun List<ApplicationListing>.placedIconForPackage(
        packageName: String,
        position: GridPosition,
    ): PlacedIcon? = find { it.packageName == packageName }?.let {
        PlacedIcon(
            app = it,
            position = position,
        )
    }

    private fun <T : Any> MutableList<T>.addIfNotNull(value: T?) = value?.let { add(it) } ?: false
}
