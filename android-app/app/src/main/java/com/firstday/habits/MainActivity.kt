package com.firstday.habits

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.firstday.habits.data.prefs.AppSettings
import com.firstday.habits.data.prefs.DarkModePref
import com.firstday.habits.navigation.Routes
import com.firstday.habits.navigation.bottomNavItems
import com.firstday.habits.ui.screens.create.CreateScreen
import com.firstday.habits.ui.screens.create.CreateViewModel
import com.firstday.habits.ui.screens.detail.DetailScreen
import com.firstday.habits.ui.screens.detail.DetailViewModel
import com.firstday.habits.ui.screens.home.HomeScreen
import com.firstday.habits.ui.screens.home.HomeViewModel
import com.firstday.habits.ui.screens.settings.SettingsScreen
import com.firstday.habits.ui.screens.settings.SettingsViewModel
import com.firstday.habits.ui.screens.stats.StatsScreen
import com.firstday.habits.ui.screens.stats.StatsViewModel
import com.firstday.habits.ui.theme.FirstDayTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val app = LocalContext.current.applicationContext as FirstDayApp
            val settings by app.settingsStore.settings.collectAsState(initial = AppSettings())
            val darkTheme = when (settings.darkMode) {
                DarkModePref.SYSTEM -> isSystemInDarkTheme()
                DarkModePref.ON -> true
                DarkModePref.OFF -> false
            }
            FirstDayTheme(darkTheme = darkTheme) {
                MainScreen()
            }
        }
    }
}

@Composable
private fun MainScreen() {
    val app = LocalContext.current.applicationContext as FirstDayApp
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(Routes.HOME, Routes.STATS, Routes.SETTINGS)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(item.route) {
                                        popUpTo(Routes.HOME) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = null,
                                )
                            },
                            label = { Text(stringResource(item.labelRes)) },
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
        ) {
            composable(Routes.HOME) {
                val vm: HomeViewModel = viewModel(
                    factory = HomeViewModel.factory(app.repository, app.settingsStore),
                )
                HomeScreen(
                    viewModel = vm,
                    onAddHabit = { navController.navigate(Routes.create()) },
                    onHabitClick = { id -> navController.navigate(Routes.detail(id)) },
                    contentPadding = innerPadding,
                )
            }

            composable(Routes.STATS) {
                val vm: StatsViewModel = viewModel(
                    factory = StatsViewModel.factory(app.repository, app.settingsStore),
                )
                StatsScreen(
                    viewModel = vm,
                    contentPadding = innerPadding,
                )
            }

            composable(Routes.SETTINGS) {
                val vm: SettingsViewModel = viewModel(
                    factory = SettingsViewModel.factory(
                        LocalContext.current.applicationContext,
                        app.settingsStore,
                    ),
                )
                SettingsScreen(
                    viewModel = vm,
                    contentPadding = innerPadding,
                    appVersion = "1.0.0",
                )
            }

            composable(
                route = Routes.DETAIL,
                arguments = listOf(navArgument("habitId") { type = NavType.LongType }),
            ) { entry ->
                val vm: DetailViewModel = viewModel(
                    factory = DetailViewModel.factory(
                        entry.savedStateHandle,
                        app.repository,
                        app.settingsStore,
                    ),
                )
                DetailScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() },
                    onEdit = { id -> navController.navigate(Routes.edit(id)) },
                    contentPadding = innerPadding,
                )
            }

            composable(
                route = Routes.CREATE,
                arguments = listOf(navArgument("habitId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }),
            ) { entry ->
                val vm: CreateViewModel = viewModel(
                    factory = CreateViewModel.factory(
                        entry.savedStateHandle,
                        app.repository,
                        LocalContext.current.applicationContext,
                    ),
                )
                CreateScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() },
                    contentPadding = innerPadding,
                )
            }
        }
    }
}
