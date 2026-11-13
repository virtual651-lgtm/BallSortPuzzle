/*
 * com.example.ballsort.ui.navigation
 * Uygulama içi Navigation Compose rota ve geçiş grafiği.
 */
package com.example.ballsort.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ballsort.ui.game.GameScreen
import com.example.ballsort.ui.howto.HowToPlayScreen
import com.example.ballsort.ui.levels.LevelSelectScreen
import com.example.ballsort.ui.menu.MenuScreen
import com.example.ballsort.ui.settings.SettingsScreen
import com.example.ballsort.ui.splash.SplashScreen

object Routes {
    const val SPLASH = "splash"
    const val MENU = "menu"
    const val LEVELS = "levels"
    const val GAME = "game/{levelId}"
    const val SETTINGS = "settings"
    const val HOW_TO = "howto"

    fun game(levelId: Int): String = "game/$levelId"
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Routes.MENU) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.MENU) {
            MenuScreen(
                onPlayLevel = { levelId -> navController.navigate(Routes.game(levelId)) },
                onOpenLevels = { navController.navigate(Routes.LEVELS) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                onOpenHowTo = { navController.navigate(Routes.HOW_TO) },
            )
        }

        composable(Routes.LEVELS) {
            LevelSelectScreen(
                onBack = { navController.popBackStack() },
                onSelectLevel = { levelId -> navController.navigate(Routes.game(levelId)) },
            )
        }

        composable(
            route = Routes.GAME,
            arguments = listOf(navArgument("levelId") { type = NavType.IntType }),
        ) {
            GameScreen(
                onNavigateMenu = {
                    navController.popBackStack(Routes.MENU, inclusive = false)
                },
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.HOW_TO) {
            HowToPlayScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}

