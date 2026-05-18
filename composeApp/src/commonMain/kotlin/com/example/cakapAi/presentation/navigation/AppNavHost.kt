package com.example.cakapAi.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
                    onFinishQuiz = {
                        navigationActions.navigateToResult(
                            score = 80,
                            totalQuestion = 10,
                            accuracy = 80,
                            isPassed = true
                        )
                    }
                )
            }

            composable<Route.Result> { backStackEntry ->
                val route: Route.Result = backStackEntry.toRoute()

                ResultScreen(
                    score = route.score,
                    totalQuestion = route.totalQuestion,
                    accuracy = route.accuracy,
                    isPassed = route.isPassed,
                    onBackToMap = {
                        navigationActions.navigateToMap()
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

    NavigationBar {
        NavigationBarItem(
            selected = currentDestination.isMapSelected(),
            onClick = {
                navigationActions.navigateToMap()
            },
            icon = {
                Text("🗺️")
            },
            label = {
                Text("Map")
            }
        )

        NavigationBarItem(
            selected = currentDestination.isQuizSelected(),
            onClick = {
                navigationActions.navigateToQuiz(levelId = 1)
            },
            icon = {
                Text("🎯")
            },
            label = {
                Text("Quiz")
            }
        )

        NavigationBarItem(
            selected = currentDestination.isDictionarySelected(),
            onClick = {
                navigationActions.navigateToDictionary()
            },
            icon = {
                Text("📖")
            },
            label = {
                Text("Dictionary")
            }
        )

        NavigationBarItem(
            selected = currentDestination.isAITutorSelected(),
            onClick = {
                navigationActions.navigateToAITutor()
            },
            icon = {
                Text("🤖")
            },
            label = {
                Text("AI Tutor")
            }
        )
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
            navController.navigate(Route.Map) {
                popUpTo(Route.Map) {
                    inclusive = false
                }
                launchSingleTop = true
            }
        }

        override fun navigateToQuiz(levelId: Int) {
            navController.navigate(Route.Quiz(levelId)) {
                launchSingleTop = true
            }
        }

        override fun navigateToResult(
            score: Int,
            totalQuestion: Int,
            accuracy: Int,
            isPassed: Boolean
        ) {
            navController.navigate(
                Route.Result(
                    score = score,
                    totalQuestion = totalQuestion,
                    accuracy = accuracy,
                    isPassed = isPassed
                )
            )
        }

        override fun navigateToDictionary() {
            navController.navigate(Route.Dictionary) {
                launchSingleTop = true
            }
        }

        override fun navigateToAITutor() {
            navController.navigate(Route.AITutor) {
                launchSingleTop = true
            }
        }

        override fun navigateBack() {
            navController.popBackStack()
        }
    }
}