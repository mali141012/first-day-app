package com.firstday.habits.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

object Routes {
    const val HOME = "home"
    const val STATS = "stats"
    const val SETTINGS = "settings"
    const val DETAIL = "detail/{habitId}"
    const val CREATE = "create?habitId={habitId}"

    fun detail(habitId: Long) = "detail/$habitId"
    fun create() = "create?habitId=0"
    fun edit(habitId: Long) = "create?habitId=$habitId"
}

val bottomNavItems = listOf(
    BottomNavItem(
        route = Routes.HOME,
        labelRes = com.firstday.habits.R.string.nav_today,
        selectedIcon = Icons.Rounded.CalendarToday,
        unselectedIcon = Icons.Outlined.CalendarToday,
    ),
    BottomNavItem(
        route = Routes.STATS,
        labelRes = com.firstday.habits.R.string.nav_stats,
        selectedIcon = Icons.Rounded.BarChart,
        unselectedIcon = Icons.Outlined.BarChart,
    ),
    BottomNavItem(
        route = Routes.SETTINGS,
        labelRes = com.firstday.habits.R.string.nav_settings,
        selectedIcon = Icons.Rounded.Settings,
        unselectedIcon = Icons.Outlined.Settings,
    ),
)
