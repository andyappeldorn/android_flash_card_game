package com.elementary.flashcards.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.elementary.flashcards.flashCardsApp
import com.elementary.flashcards.ui.home.HomeScreen
import com.elementary.flashcards.ui.play.PlayScreen
import com.elementary.flashcards.ui.progress.ProgressScreen
import com.elementary.flashcards.ui.summary.SummaryScreen

private object Routes {
    const val Home = "home"
    const val Progress = "progress"
    const val Play = "play/{deckId}"
    const val Summary = "summary/{deckId}"
    fun play(deckId: String) = "play/$deckId"
    fun summary(deckId: String) = "summary/$deckId"
}

@Composable
fun FlashNav() {
    val container = LocalContext.current.flashCardsApp().container
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.Home) {
        composable(Routes.Home) {
            HomeScreen(
                stats = container.stats,
                settings = container.settings,
                onPlay = { deckId -> nav.navigate(Routes.play(deckId)) },
                onProgress = { nav.navigate(Routes.Progress) },
            )
        }
        composable(
            route = Routes.Play,
            arguments = listOf(navArgument("deckId") { type = NavType.StringType }),
        ) { entry ->
            val deckId = entry.arguments?.getString("deckId").orEmpty()
            PlayScreen(
                deckId = deckId,
                stats = container.stats,
                settings = container.settings,
                onExit = { nav.popBackStack() },
                onFinished = {
                    nav.navigate(Routes.summary(deckId)) {
                        popUpTo(Routes.Home)
                    }
                },
            )
        }
        composable(
            route = Routes.Summary,
            arguments = listOf(navArgument("deckId") { type = NavType.StringType }),
        ) { entry ->
            val deckId = entry.arguments?.getString("deckId").orEmpty()
            SummaryScreen(
                deckId = deckId,
                stats = container.stats,
                onDone = { nav.popBackStack(Routes.Home, inclusive = false) },
            )
        }
        composable(Routes.Progress) {
            ProgressScreen(
                stats = container.stats,
                onBack = { nav.popBackStack() },
            )
        }
    }
}
