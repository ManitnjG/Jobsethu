package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object AiCoach : Screen("ai_coach")
    object Applications : Screen("applications")
    object Profile : Screen("profile")
    object GovtJobs : Screen("govt_jobs")
    object Alerts : Screen("alerts")
    object Settings : Screen("settings")
    object JobDetail : Screen("job_detail/{jobId}") {
        fun createRoute(jobId: String) = "job_detail/$jobId"
    }
}

data class NavItem(
    val titleKey: String,
    val defaultTitle: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    NavItem("home", "Home", Screen.Home.route, Icons.Filled.Home, Icons.Outlined.Home),
    NavItem("search", "Search", Screen.Search.route, Icons.Filled.Search, Icons.Outlined.Search),
    NavItem("ai", "AI Coach", Screen.AiCoach.route, Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
    NavItem("applications", "Applications", Screen.Applications.route, Icons.Filled.BusinessCenter, Icons.Outlined.BusinessCenter),
    NavItem("profile", "Profile", Screen.Profile.route, Icons.Filled.Person, Icons.Outlined.Person)
)
