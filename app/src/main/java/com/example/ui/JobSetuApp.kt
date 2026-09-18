package com.example.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.data.ai.HybridAIService
import com.example.data.local.AppDatabase
import com.example.data.local.AppPreferences
import com.example.data.repository.*
import com.example.ui.navigation.Screen
import com.example.ui.navigation.bottomNavItems
import com.example.ui.screens.alerts.AlertsScreen
import com.example.ui.screens.applications.ApplicationsScreen
import com.example.ui.screens.applications.ApplicationsViewModel
import com.example.ui.screens.coach.AiCoachScreen
import com.example.ui.screens.coach.AiCoachViewModel
import com.example.ui.screens.detail.JobDetailScreen
import com.example.ui.screens.detail.JobDetailViewModel
import com.example.ui.screens.govt.GovtJobsScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.home.HomeViewModel
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.profile.ProfileViewModel
import com.example.ui.screens.search.SearchScreen
import com.example.ui.screens.search.SearchViewModel
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.theme.JobSetuTheme
import com.example.ui.util.JobSetuStrings

@Composable
fun JobSetuApp() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val appPreferences = remember { AppPreferences(context) }
    val aiProvider = remember { HybridAIService() }

    val jobRepository = remember { JobRepository(database.jobDao(), aiProvider) }
    val candidateRepository = remember { CandidateRepository(database.candidateProfileDao(), database.resumeDao()) }
    val applicationRepository = remember { ApplicationRepository(database.applicationDao()) }
    val govtJobRepository = remember { GovtJobRepository(database.govtJobDao()) }
    val alertRepository = remember { AlertRepository(database.jobAlertDao()) }

    // ViewModels
    val homeViewModel = remember { HomeViewModel(jobRepository, candidateRepository, aiProvider, appPreferences) }
    val searchViewModel = remember { SearchViewModel(jobRepository, candidateRepository, aiProvider) }
    val coachViewModel = remember { AiCoachViewModel(candidateRepository, jobRepository, aiProvider) }
    val applicationsViewModel = remember { ApplicationsViewModel(applicationRepository) }
    val profileViewModel = remember { ProfileViewModel(candidateRepository, jobRepository, aiProvider) }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentLang by appPreferences.language.collectAsState()

    // Determine if bottom bar should be visible (only on the 5 primary destinations)
    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    JobSetuTheme {
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        tonalElevation = 8.dp,
                        modifier = Modifier.testTag("main_bottom_nav")
                    ) {
                        bottomNavItems.forEach { item ->
                            val isSelected = currentRoute == item.route
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.defaultTitle
                                    )
                                },
                                label = {
                                    Text(
                                        text = JobSetuStrings.get(item.titleKey, currentLang),
                                        fontSize = 11.sp
                                    )
                                },
                                selected = isSelected,
                                onClick = {
                                    if (currentRoute != item.route) {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                modifier = Modifier.testTag("nav_item_${item.route}")
                            )
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = homeViewModel,
                        onJobClick = { job ->
                            navController.navigate(Screen.JobDetail.createRoute(job.id))
                        },
                        onApplyClick = { job ->
                            navController.navigate(Screen.JobDetail.createRoute(job.id))
                        },
                        onSearchTriggered = { query ->
                            searchViewModel.onSearchQueryChange(query)
                            searchViewModel.executeSearch(query)
                            navController.navigate(Screen.Search.route)
                        },
                        onNavigateToGovt = {
                            navController.navigate(Screen.GovtJobs.route)
                        },
                        onNavigateToAlerts = {
                            navController.navigate(Screen.Alerts.route)
                        },
                        onNavigateToSettings = {
                            navController.navigate(Screen.Settings.route)
                        }
                    )
                }

                composable(Screen.Search.route) {
                    SearchScreen(
                        viewModel = searchViewModel,
                        onJobClick = { job ->
                            navController.navigate(Screen.JobDetail.createRoute(job.id))
                        },
                        onApplyClick = { job ->
                            navController.navigate(Screen.JobDetail.createRoute(job.id))
                        }
                    )
                }

                composable(Screen.AiCoach.route) {
                    AiCoachScreen(viewModel = coachViewModel)
                }

                composable(Screen.Applications.route) {
                    ApplicationsScreen(
                        viewModel = applicationsViewModel,
                        onExploreJobs = {
                            navController.navigate(Screen.Home.route)
                        }
                    )
                }

                composable(Screen.Profile.route) {
                    ProfileScreen(viewModel = profileViewModel)
                }

                composable(
                    route = Screen.JobDetail.route,
                    arguments = listOf(navArgument("jobId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
                    val detailViewModel = remember(jobId) {
                        JobDetailViewModel(jobId, jobRepository, candidateRepository, applicationRepository, aiProvider)
                    }
                    JobDetailScreen(
                        viewModel = detailViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.GovtJobs.route) {
                    GovtJobsScreen(
                        repository = govtJobRepository,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Alerts.route) {
                    AlertsScreen(
                        alertRepository = alertRepository,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(
                        appPreferences = appPreferences,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
