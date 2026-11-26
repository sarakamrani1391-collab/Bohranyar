package com.example.ui.navigation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.material.icons.outlined.Sos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.data.model.CrisisType
import com.example.ui.screens.CrisisDetailScreen
import com.example.ui.screens.CrisisListScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EmergencyKitScreen
import com.example.ui.screens.FamilyScreen
import com.example.ui.screens.MapScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SosScreen
import com.example.ui.screens.TrainingScreen
import com.example.ui.screens.UrbanReportScreen
import com.example.ui.theme.EmergencyRed
import com.example.ui.viewmodel.AppViewModelProvider
import com.example.ui.viewmodel.CrisisGuideViewModel
import com.example.ui.viewmodel.DashboardViewModel
import com.example.ui.viewmodel.FamilyViewModel
import com.example.ui.viewmodel.KitViewModel
import com.example.ui.viewmodel.MainAppViewModel
import com.example.ui.viewmodel.MapViewModel
import com.example.ui.viewmodel.ReportViewModel
import com.example.ui.viewmodel.SettingsViewModel
import com.example.ui.viewmodel.SosViewModel
import com.example.ui.viewmodel.TrainingViewModel

private data class TopNavItem(
    val route: AppRoute,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String,
    val isEmergency: Boolean = false
)

@Composable
fun HamyarBohranApp(
    mainViewModel: MainAppViewModel
) {
    val settings by mainViewModel.userSettings.collectAsStateWithLifecycle()
    val isOnline by mainViewModel.isOnline.collectAsStateWithLifecycle()

    if (!settings.onboardingCompleted) {
        OnboardingScreen(
            onCompleteOnboarding = { name, city, consent ->
                mainViewModel.completeOnboarding(name, city, consent)
            }
        )
        return
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val topLevelItems = listOf(
        TopNavItem(AppRoute.Dashboard, "داشبورد", Icons.Filled.Home, Icons.Outlined.Home, "nav_dashboard"),
        TopNavItem(AppRoute.CrisisSelection, "بحران‌ها", Icons.Filled.MenuBook, Icons.Outlined.MenuBook, "nav_crises"),
        TopNavItem(AppRoute.Sos, "کمک فوری", Icons.Filled.Sos, Icons.Outlined.Sos, "nav_sos", isEmergency = true),
        TopNavItem(AppRoute.Map, "نقشه", Icons.Filled.Map, Icons.Outlined.Map, "nav_map"),
        TopNavItem(AppRoute.UrbanReports, "گزارش", Icons.Filled.ReportProblem, Icons.Outlined.ReportProblem, "nav_reports")
    )

    val showTopLevelChrome = currentDestination?.let { dest ->
        topLevelItems.any { item -> dest.hasRoute(item.route::class) }
    } ?: true

    fun navigateTopLevel(route: AppRoute) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavRail = maxWidth >= 600.dp

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (showTopLevelChrome && !useNavRail) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 4.dp
                    ) {
                        topLevelItems.forEach { item ->
                            val selected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigateTopLevel(item.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label
                                    )
                                },
                                label = { Text(item.label, maxLines = 1) },
                                colors = if (item.isEmergency) {
                                    NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.onError,
                                        selectedTextColor = EmergencyRed,
                                        indicatorColor = EmergencyRed,
                                        unselectedIconColor = EmergencyRed,
                                        unselectedTextColor = EmergencyRed
                                    )
                                } else {
                                    NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                },
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (showTopLevelChrome && useNavRail) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        topLevelItems.forEach { item ->
                            val selected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateTopLevel(item.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label
                                    )
                                },
                                label = { Text(item.label) },
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = AppRoute.Dashboard,
                    modifier = Modifier.weight(1f)
                ) {
                    composable<AppRoute.Dashboard> {
                        val vm: DashboardViewModel = viewModel(factory = AppViewModelProvider.Factory)
                        val uiState by vm.uiState.collectAsStateWithLifecycle()
                        DashboardScreen(
                            uiState = uiState,
                            onNavigateToSos = { navigateTopLevel(AppRoute.Sos) },
                            onNavigateToCrises = { navigateTopLevel(AppRoute.CrisisSelection) },
                            onNavigateToMap = { navigateTopLevel(AppRoute.Map) },
                            onNavigateToFamily = { navController.navigate(AppRoute.Family) },
                            onNavigateToKit = { navController.navigate(AppRoute.EmergencyKit) },
                            onNavigateToTraining = { navController.navigate(AppRoute.Training) },
                            onNavigateToReports = { navigateTopLevel(AppRoute.UrbanReports) },
                            onNavigateToSettings = { navController.navigate(AppRoute.Settings) }
                        )
                    }

                    composable<AppRoute.CrisisSelection> {
                        val vm: CrisisGuideViewModel = viewModel(factory = AppViewModelProvider.Factory)
                        val checklists by vm.allChecklists.collectAsStateWithLifecycle()
                        CrisisListScreen(
                            crisisTypes = vm.crisisTypes,
                            allChecklists = checklists,
                            isOnline = isOnline,
                            onSelectCrisis = { crisis ->
                                navController.navigate(AppRoute.CrisisDetail(crisis.id))
                            }
                        )
                    }

                    composable<AppRoute.CrisisDetail> { entry ->
                        val route = entry.toRoute<AppRoute.CrisisDetail>()
                        val crisisType = CrisisType.fromId(route.crisisId)
                        val vm: CrisisGuideViewModel = viewModel(factory = AppViewModelProvider.Factory)
                        val guide = vm.getGuide(crisisType) ?: return@composable
                        val checklist by vm.observeChecklist(crisisType).collectAsStateWithLifecycle(emptyList())
                        CrisisDetailScreen(
                            crisisType = crisisType,
                            guide = guide,
                            checklist = checklist,
                            isOnline = isOnline,
                            onToggleChecklist = vm::toggleChecklist,
                            onNavigateToSos = { navigateTopLevel(AppRoute.Sos) },
                            onNavigateToTraining = { navController.navigate(AppRoute.Training) },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable<AppRoute.Sos> {
                        val vm: SosViewModel = viewModel(factory = AppViewModelProvider.Factory)
                        val uiState by vm.uiState.collectAsStateWithLifecycle()
                        SosScreen(
                            uiState = uiState,
                            onSelectType = vm::selectEmergencyType,
                            onUpdateNote = vm::updateCustomNote,
                            onSetIncludeLocation = vm::setIncludeLocation,
                            onFetchLocation = vm::fetchLocation,
                            onSubmitSos = vm::submitSosRequest,
                            onMarkShared = vm::markShared,
                            onSyncQueued = vm::syncQueuedNow,
                            onDeleteRequest = vm::deleteRequest
                        )
                    }

                    composable<AppRoute.Map> {
                        val vm: MapViewModel = viewModel(factory = AppViewModelProvider.Factory)
                        val uiState by vm.uiState.collectAsStateWithLifecycle()
                        MapScreen(
                            uiState = uiState,
                            onSelectFilter = vm::selectFilter,
                            onSelectShelter = vm::selectShelter,
                            onFetchLocation = vm::fetchUserLocation,
                            onAddSafePoint = vm::addPersonalSafePoint,
                            onDeleteShelter = vm::deleteShelter,
                            onRefreshOfflineSnapshot = vm::refreshOfflineSnapshot
                        )
                    }

                    composable<AppRoute.Family> {
                        val vm: FamilyViewModel = viewModel(factory = AppViewModelProvider.Factory)
                        val uiState by vm.uiState.collectAsStateWithLifecycle()
                        FamilyScreen(
                            uiState = uiState,
                            isOnline = isOnline,
                            onAddMember = vm::addMember,
                            onUpdateSafetyStatus = vm::updateSafetyStatus,
                            onDeleteMember = vm::deleteMember,
                            onSavePlan = vm::saveFamilyPlan,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable<AppRoute.EmergencyKit> {
                        val vm: KitViewModel = viewModel(factory = AppViewModelProvider.Factory)
                        val uiState by vm.uiState.collectAsStateWithLifecycle()
                        EmergencyKitScreen(
                            uiState = uiState,
                            isOnline = isOnline,
                            onSelectCategory = vm::selectCategory,
                            onToggleOnlyUnprepared = vm::toggleOnlyUnprepared,
                            onToggleItem = vm::toggleItemPrepared,
                            onAddCustomItem = vm::addCustomItem,
                            onDeleteItem = vm::deleteItem,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable<AppRoute.Training> {
                        val vm: TrainingViewModel = viewModel(factory = AppViewModelProvider.Factory)
                        val uiState by vm.uiState.collectAsStateWithLifecycle()
                        TrainingScreen(
                            uiState = uiState,
                            isOnline = isOnline,
                            onStartQuiz = vm::startQuiz,
                            onSelectAnswer = vm::selectAnswer,
                            onNextQuestion = vm::nextQuestion,
                            onCloseQuiz = vm::closeQuiz,
                            onOpenCrisisGuide = { crisis ->
                                navController.navigate(AppRoute.CrisisDetail(crisis.id))
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable<AppRoute.UrbanReports> {
                        val vm: ReportViewModel = viewModel(factory = AppViewModelProvider.Factory)
                        val uiState by vm.uiState.collectAsStateWithLifecycle()
                        UrbanReportScreen(
                            uiState = uiState,
                            onSelectFilter = vm::selectFilter,
                            onFetchLocation = vm::fetchLocationForReport,
                            onSubmitReport = vm::submitReport,
                            onSyncQueued = vm::syncQueuedReports,
                            onDeleteReport = vm::deleteReport,
                            onDismissBanner = vm::dismissBanner,
                            onBack = { navigateTopLevel(AppRoute.Dashboard) }
                        )
                    }

                    composable<AppRoute.Settings> {
                        val vm: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
                        val uiState by vm.uiState.collectAsStateWithLifecycle()
                        SettingsScreen(
                            uiState = uiState,
                            onSaveProfile = vm::saveProfile,
                            onUpdateAccessibility = vm::updateAccessibility,
                            onRefreshOfflinePackage = vm::refreshOfflinePackage,
                            onReplayOnboarding = vm::replayOnboarding,
                            onDismissMessage = vm::dismissMessage,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
