package com.example.cakapAi.presentation.navigation

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.cakapAi.presentation.screens.dictionary.DictionaryScreen
import com.example.cakapAi.presentation.screens.map.MapScreen
import com.example.cakapAi.presentation.screens.quiz.QuizScreen
import com.example.cakapAi.presentation.screens.result.ResultScreen
import com.example.cakapAi.presentation.screens.tutor.AITutorScreen
import com.example.cakapAi.presentation.screens.profile.ProfileScreen
import com.example.cakapAi.presentation.screens.settings.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val navigationActions = createNavigationActions(navController)

    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                navigationActions = navigationActions
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Map,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Route.Map> {
                MapScreen(
                    onNavigateToQuiz = { levelId ->
                        navigationActions.navigateToQuiz(levelId)
                    },
                    onNavigateToDictionary = {
                        navigationActions.navigateToDictionary()
                    },
                    onNavigateToAITutor = {
                        navigationActions.navigateToAITutor()
                    },
                    onNavigateToProfile = {
                        navigationActions.navigateToProfile()
                    }
                )
            }

            composable<Route.Quiz> { backStackEntry ->
                val route: Route.Quiz = backStackEntry.toRoute()

                QuizScreen(
                    levelId = route.levelId,
                    onNavigateBack = {
                        navigationActions.navigateToMap()
                    },
                    onFinishQuiz = { score, totalQuestions, accuracy, isPassed ->
                        navigationActions.navigateToResult(
                            levelId = route.levelId,
                            score = score,
                            totalQuestion = totalQuestions,
                            accuracy = accuracy,
                            isPassed = isPassed
                        )
                    }
                )
            }

            composable<Route.Result> { backStackEntry ->
                val route: Route.Result = backStackEntry.toRoute()

                ResultScreen(
                    levelId = route.levelId,
                    score = route.score,
                    totalQuestion = route.totalQuestion,
                    accuracy = route.accuracy,
                    isPassed = route.isPassed,
                    onBackToMap = {
                        navigationActions.navigateToMap()
                    },
                    onRetryQuiz = {
                        navigationActions.navigateToQuiz(route.levelId)
                    }
                )
            }

            composable<Route.Dictionary> {
                DictionaryScreen(
                    onNavigateBack = {
                        navigationActions.navigateToMap()
                    }
                )
            }

            composable<Route.AITutor> {
                AITutorScreen(
                    onNavigateBack = {
                        navigationActions.navigateToMap()
                    }
                )
            }

            composable<Route.Profile> {
                ProfileScreen(
                    onNavigateBack = {
                        navigationActions.navigateBack()
                    },
                    onNavigateToSettings = {
                        navigationActions.navigateToSettings()
                    }
                )
            }

            composable<Route.Settings> {
                SettingsScreen(
                    onNavigateBack = {
                        navigationActions.navigateBack()
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    navController: NavHostController,
    navigationActions: NavigationActions
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Dynamic active accent glowing color based on the selected screen
    val activeColor = when {
        currentDestination.isMapSelected() -> Color(0xFF0EA5E9)       // Sky Blue Map
        currentDestination.isQuizSelected() -> Color(0xFFFBBF24)      // Gold Quiz
        currentDestination.isDictionarySelected() -> Color(0xFF10B981)  // Emerald Dictionary
        currentDestination.isAITutorSelected() -> Color(0xFF8B5CF6)     // Purple AI Tutor
        else -> Color.White
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F1A30))
    ) {
        HorizontalDivider(
            color = Color.White.copy(alpha = 0.08f),
            thickness = 1.dp
        )
        NavigationBar(
            containerColor = Color(0xFF0F1A30),
            tonalElevation = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(72.dp)
        ) {
            // 1. MAP ITEM (Explore / Peta)
            val isMapActive = currentDestination.isMapSelected()
            val mapScale by animateFloatAsState(
                targetValue = if (isMapActive) 1.2f else 1.0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
            NavigationBarItem(
                selected = isMapActive,
                onClick = { navigationActions.navigateToMap() },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = "Map",
                        modifier = Modifier
                            .size(22.dp)
                            .graphicsLayer {
                                scaleX = mapScale
                                scaleY = mapScale
                            },
                        tint = if (isMapActive) Color(0xFF0EA5E9) else Color(0xFF64748B)
                    )
                },
                label = {
                    Text(
                        text = "Peta",
                        fontWeight = if (isMapActive) FontWeight.ExtraBold else FontWeight.Medium,
                        fontSize = 11.sp,
                        color = if (isMapActive) Color(0xFF0EA5E9) else Color(0xFF64748B)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFF0EA5E9).copy(alpha = 0.12f)
                )
            )

            // 2. QUIZ ITEM (EmojiEvents / Kuis)
            val isQuizActive = currentDestination.isQuizSelected()
            val quizScale by animateFloatAsState(
                targetValue = if (isQuizActive) 1.2f else 1.0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
            NavigationBarItem(
                selected = isQuizActive,
                onClick = { navigationActions.navigateToQuiz(levelId = 1) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Quiz",
                        modifier = Modifier
                            .size(22.dp)
                            .graphicsLayer {
                                scaleX = quizScale
                                scaleY = quizScale
                            },
                        tint = if (isQuizActive) Color(0xFFFBBF24) else Color(0xFF64748B)
                    )
                },
                label = {
                    Text(
                        text = "Kuis",
                        fontWeight = if (isQuizActive) FontWeight.ExtraBold else FontWeight.Medium,
                        fontSize = 11.sp,
                        color = if (isQuizActive) Color(0xFFFBBF24) else Color(0xFF64748B)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFFFBBF24).copy(alpha = 0.12f)
                )
            )

            // 3. DICTIONARY ITEM (Translate / Kamus)
            val isDictActive = currentDestination.isDictionarySelected()
            val dictScale by animateFloatAsState(
                targetValue = if (isDictActive) 1.2f else 1.0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
            NavigationBarItem(
                selected = isDictActive,
                onClick = { navigationActions.navigateToDictionary() },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "Dictionary",
                        modifier = Modifier
                            .size(22.dp)
                            .graphicsLayer {
                                scaleX = dictScale
                                scaleY = dictScale
                            },
                        tint = if (isDictActive) Color(0xFF10B981) else Color(0xFF64748B)
                    )
                },
                label = {
                    Text(
                        text = "Kamus",
                        fontWeight = if (isDictActive) FontWeight.ExtraBold else FontWeight.Medium,
                        fontSize = 11.sp,
                        color = if (isDictActive) Color(0xFF10B981) else Color(0xFF64748B)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFF10B981).copy(alpha = 0.12f)
                )
            )

            // 4. AI TUTOR ITEM (Face / AI Tutor)
            val isTutorActive = currentDestination.isAITutorSelected()
            val tutorScale by animateFloatAsState(
                targetValue = if (isTutorActive) 1.2f else 1.0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
            NavigationBarItem(
                selected = isTutorActive,
                onClick = { navigationActions.navigateToAITutor() },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "AI Tutor",
                        modifier = Modifier
                            .size(22.dp)
                            .graphicsLayer {
                                scaleX = tutorScale
                                scaleY = tutorScale
                            },
                        tint = if (isTutorActive) Color(0xFF8B5CF6) else Color(0xFF64748B)
                    )
                },
                label = {
                    Text(
                        text = "AI Tutor",
                        fontWeight = if (isTutorActive) FontWeight.ExtraBold else FontWeight.Medium,
                        fontSize = 11.sp,
                        color = if (isTutorActive) Color(0xFF8B5CF6) else Color(0xFF64748B)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFF8B5CF6).copy(alpha = 0.12f)
                )
            )
        }
    }
}

private fun NavDestination?.isMapSelected(): Boolean {
    return this?.hasRoute<Route.Map>() == true
}

private fun NavDestination?.isQuizSelected(): Boolean {
    return this?.hasRoute<Route.Quiz>() == true ||
            this?.hasRoute<Route.Result>() == true
}

private fun NavDestination?.isDictionarySelected(): Boolean {
    return this?.hasRoute<Route.Dictionary>() == true
}

private fun NavDestination?.isAITutorSelected(): Boolean {
    return this?.hasRoute<Route.AITutor>() == true
}

private fun createNavigationActions(
    navController: NavHostController
): NavigationActions {
    return object : NavigationActions {

        override fun navigateToMap() {
            // Bulletproof popping back to Map screen if already in stack, else navigate cleanly
            val popped = navController.popBackStack(Route.Map, inclusive = false)
            if (!popped) {
                navController.navigate(Route.Map) {
                    popUpTo(Route.Map) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }

        override fun navigateToQuiz(levelId: Int) {
            navController.navigate(Route.Quiz(levelId)) {
                popUpTo(Route.Map) {
                    inclusive = false
                }
                launchSingleTop = true
            }
        }

        override fun navigateToResult(
            levelId: Int,
            score: Int,
            totalQuestion: Int,
            accuracy: Int,
            isPassed: Boolean
        ) {
            navController.navigate(
                Route.Result(
                    levelId = levelId,
                    score = score,
                    totalQuestion = totalQuestion,
                    accuracy = accuracy,
                    isPassed = isPassed
                )
            )
        }

        override fun navigateToDictionary() {
            navController.navigate(Route.Dictionary) {
                popUpTo(Route.Map) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }

        override fun navigateToAITutor() {
            navController.navigate(Route.AITutor) {
                popUpTo(Route.Map) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }

        override fun navigateToProfile() {
            navController.navigate(Route.Profile) {
                launchSingleTop = true
            }
        }

        override fun navigateToSettings() {
            navController.navigate(Route.Settings) {
                launchSingleTop = true
            }
        }

        override fun navigateBack() {
            navController.popBackStack()
        }
    }
}