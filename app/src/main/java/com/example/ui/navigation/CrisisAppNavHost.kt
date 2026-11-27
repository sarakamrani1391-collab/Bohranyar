package com.example.ui.navigation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Science
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
import com.example.ui.screens.EncyclopediaDetailScreen
import com.example.ui.screens.EncyclopediaScreen
import com.example.ui.screens.FamilyScreen
import com.example.ui.screens.FirstAidDetailScreen
import com.example.ui.screens.MavaRootScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ScenarioLabScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SosScreen
import com.example.ui.screens.TrainingScreen
import com.example.ui.screens.VirtualLabScreen
import com.example.ui.screens.WhyItHappensScreen
import com.example.ui.theme.EmergencyRed
import com.example.ui.viewmodel.AppViewModelProvider
import com.example.ui.viewmodel.CrisisGuideViewModel
import com.example.ui.viewmodel.DashboardViewModel
import com.example.ui.viewmodel.FamilyViewModel
import com.example.ui.viewmodel.KitViewModel
import com.example.ui.viewmodel.MainAppViewModel
import com.example.ui.viewmodel.MavaViewModel
import com.example.ui.viewmodel.ScenarioLabViewModel
import com.example.ui.viewmodel.ScienceViewModel
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
            onCompleteOnboarding = { name, city ->
                mainViewModel.completeOnboarding(name, city)
            }
        )
        return
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val topLevelItems = listOf(
        TopNavItem(AppRoute.Dashboard, "خانه", Icons.Filled.Home, Icons.Outlined.Home, "nav_dashboard"),
        TopNavItem(AppRoute.CrisisSelection, "بحران‌ها", Icons.Filled.MenuBook, Icons.Outlined.MenuBook, "nav_crises"),
        TopNavItem(AppRoute.Sos, "کمک فوری", Icons.Filled.Sos, Icons.Outlined.Sos, "nav_sos", isEmergency = true),
        TopNavItem(AppRoute.Encyclopedia, "دانشنامه", Icons.Filled.Science, Icons.Outlined.Science, "nav_encyclopedia"),
        TopNavItem(AppRoute.Mava, "مأوا", Icons.Filled.ChildCare, Icons.Outlined.ChildCare, "nav_mava")
    )

    val isInMava = currentDestination?.hasRoute(AppRoute.Mava::class) == true
    val showTopLevelChrome = !isInMava && (currentDestination?.let { dest ->
        topLevelItems.any { item -> item.route != AppRoute.Mava && dest.hasRoute(item.route::class) }
    } ?: true)

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
                            onNavigateToTraining = { navController.navigate(AppRoute.Training) },
                            onNavigateToEncyclopedia = { navigateTopLevel(AppRoute.Encyclopedia) },
                            onNavigateToWhyItHappens = { navController.navigate(AppRoute.WhyItHappens) },
                            onNavigateToVirtualLab = { navController.navigate(AppRoute.VirtualLab) },
                            onNavigateToScenarioLab = { navController.navigate(AppRoute.ScenarioLab) },
                            onNavigateToFamily = { navController.navigate(AppRoute.Family) },
                            onNavigateToKit = { navController.navigate(AppRoute.EmergencyKit) },
                            onNavigateToMava = { navController.navigate(AppRoute.Mava) },
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
                            onSelectCrisisType = vm::selectCrisisType,
                            onSelectType = vm::selectEmergencyType,
                            onUpdateAddressNote = vm::updateAddressNote,
                            onUpdateNote = vm::updateCustomNote,
                            onToggleContact = vm::toggleContactSelection,
                            onSetLocationConsent = vm::setLocationConsent,
                            onLocationPermissionResult = vm::onLocationPermissionResult,
                            onSubmitSos = vm::submitSosRequest,
                            onStopLocationSharing = vm::stopLocationSharing,
                            onResolveSos = vm::resolveActiveSos,
                            onCancelSos = vm::cancelActiveSos,
                            onMarkShared = vm::markShared,
                            onSyncQueued = vm::syncQueuedNow,
                            onDeleteRequest = vm::deleteRequest
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
                            onOpenLessonDetail = { lessonId ->
                                navController.navigate(AppRoute.FirstAidDetail(lessonId))
                            },
                            onStartQuiz = vm::startQuiz,
                            onSelectAnswer = vm::selectAnswer,
                            onNextQuestion = vm::nextQuestion,
                            onCloseQuiz = vm::closeQuiz,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable<AppRoute.FirstAidDetail> { entry ->
                        val route = entry.toRoute<AppRoute.FirstAidDetail>()
                        val vm: TrainingViewModel = viewModel(factory = AppViewModelProvider.Factory)
                        val uiState by vm.uiState.collectAsStateWithLifecycle()
                        val lesson = vm.getLessonById(route.lessonId) ?: return@composable
                        FirstAidDetailScreen(
                            lesson = lesson,
                            uiState = uiState,
                            onDownloadVideo = vm::downloadLessonVideo,
                            onImportLocalVideo = vm::importLocalLessonVideo,
                            onDeleteOfflineVideo = vm::removeOfflineVideo,
                            onDismissBanner = vm::dismissMediaBanner,
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

                    composable<AppRoute.Encyclopedia> {
                        val vm: ScienceViewModel = viewModel(factory = AppViewModelProvider.Factory)
                        val uiState by vm.uiState.collectAsStateWithLifecycle()
                        EncyclopediaScreen(
                            uiState = uiState,
                            isOnline = isOnline,
                            onSearchChange = vm::updateSearch,
                            onSelectCategory = vm::selectCategory,
                            onOpenTopic = { topicId ->
                                navController.navigate(AppRoute.EncyclopediaDetail(topicId))
                            },
                            onOpenWhyItHappens = {
                                navController.navigate(AppRoute.WhyItHappens)
                            }
                        )
                    }

                    composable<AppRoute.EncyclopediaDetail> { entry ->
                        val route = entry.toRoute<AppRoute.EncyclopediaDetail>()
                        val vm: ScienceViewModel = viewModel(factory = AppViewModelProvider.Factory)
                        val uiState by vm.uiState.collectAsStateWithLifecycle()
                        val topic = vm.getTopicById(route.topicId) ?: return@composable
                        val questions = vm.getQuestionsForTopic(topic.quizTopicId)
                        EncyclopediaDetailScreen(
                            topic = topic,
                            questions = questions,
                            selectedAnswers = uiState.selectedQuizAnswers,
                            isOnline = isOnline,
                            onAnswerQuiz = vm::answerInlineQuiz,
                            onResetQuiz = vm::resetInlineQuiz,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable<AppRoute.WhyItHappens> {
                        val vm: ScienceViewModel = viewModel(factory = AppViewModelProvider.Factory)
                        val uiState by vm.uiState.collectAsStateWithLifecycle()
                        WhyItHappensScreen(
                            uiState = uiState,
                            isOnline = isOnline,
                            onAnswerQuiz = vm::answerInlineQuiz,
                            onResetQuiz = vm::resetInlineQuiz,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable<AppRoute.VirtualLab> {
                        VirtualLabScreen(
                            isOnline = isOnline,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable<AppRoute.ScenarioLab> {
                        val vm: ScenarioLabViewModel = viewModel(factory = AppViewModelProvider.Factory)
                        val uiState by vm.uiState.collectAsStateWithLifecycle()
                        ScenarioLabScreen(
                            uiState = uiState,
                            isOnline = isOnline,
                            onSelectScenario = vm::selectScenario,
                            onChooseOption = vm::chooseOption,
                            onRetry = vm::retryCurrent,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable<AppRoute.Mava> {
                        val vm: MavaViewModel = viewModel(factory = AppViewModelProvider.Factory)
                        val uiState by vm.uiState.collectAsStateWithLifecycle()
                        MavaRootScreen(
                            uiState = uiState,
                            onUpdateSettings = vm::updateSettings,
                            onPlayTone = vm::playTone,
                            onPlayHappyTone = vm::playHappyTone,
                            onPlayLullabyStep = vm::playLullabyStep,
                            onImportLocalCartoon = vm::importLocalCartoonVideo,
                            onRemoveOfflineCartoon = vm::removeOfflineCartoon,
                            onDismissNote = vm::dismissNote,
                            onExitMava = {
                                navController.popBackStack(AppRoute.Dashboard, inclusive = false)
                            }
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
