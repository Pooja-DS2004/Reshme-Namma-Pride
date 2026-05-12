package com.reshmenamma.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.reshmenamma.app.ui.screens.*
import com.reshmenamma.app.ui.theme.ReshmeNammaTheme
import com.reshmenamma.app.viewmodel.BatchViewModel
import com.reshmenamma.app.viewmodel.CommunityViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReshmeNammaTheme {
                ReshmeNammaApp()
            }
        }
    }
}

@Composable
fun ReshmeNammaApp() {
    val navController = rememberNavController()
    val batchViewModel: BatchViewModel = viewModel()
    val communityViewModel: CommunityViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") {
            DashboardScreen(
                onNavigateToBatch = { batchId ->
                    navController.navigate("batch/$batchId")
                },
                onNavigateToNewBatch = {
                    navController.navigate("new_batch")
                },
                onNavigateToCommunity = {
                    navController.navigate("community")
                },
                viewModel = batchViewModel
            )
        }

        composable("new_batch") {
            NewBatchScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = batchViewModel
            )
        }

        composable(
            "batch/{batchId}",
            arguments = listOf(
                navArgument("batchId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val batchId = backStackEntry.arguments?.getInt("batchId") ?: return@composable
            val allBatches by batchViewModel.allBatches.collectAsState(initial = emptyList())
            val batch = allBatches.find { it.id == batchId }
            val currentInstar = batch?.currentInstar ?: 1

            BatchTrackerScreen(
                batchId = batchId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToClimateEntry = {
                    navController.navigate("climate_entry/$batchId/$currentInstar")
                },
                onNavigateToAdvice = {
                    navController.navigate("advice/$batchId/$currentInstar")
                },
                viewModel = batchViewModel
            )
        }

        composable(
            "climate_entry/{batchId}/{instar}",
            arguments = listOf(
                navArgument("batchId") { type = NavType.IntType },
                navArgument("instar") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val batchId = backStackEntry.arguments?.getInt("batchId") ?: return@composable
            val instar = backStackEntry.arguments?.getInt("instar") ?: 1

            ClimateEntryScreen(
                batchId = batchId,
                currentInstar = instar,
                onNavigateBack = { navController.popBackStack() },
                onAdviceGenerated = { advice ->
                    navController.navigate("advice_result/$batchId/$instar")
                },
                viewModel = batchViewModel
            )
        }

        composable(
            "advice_result/{batchId}/{instar}",
            arguments = listOf(
                navArgument("batchId") { type = NavType.IntType },
                navArgument("instar") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val batchId = backStackEntry.arguments?.getInt("batchId") ?: return@composable
            val instar = backStackEntry.arguments?.getInt("instar") ?: 1
            val advice = batchViewModel.currentAdvice
            val lastEntry = batchViewModel.currentEntries.firstOrNull()

            if (advice != null) {
                AdviceScreen(
                    advice = advice,
                    currentInstar = instar,
                    temperature = lastEntry?.temperature ?: 0.0,
                    humidity = lastEntry?.humidity ?: 0.0,
                    onNavigateBack = {
                        navController.popBackStack("batch/$batchId", inclusive = false)
                    },
                    onTakeAnotherReading = {
                        navController.navigate("climate_entry/$batchId/$instar") {
                            popUpTo("advice_result/$batchId/$instar") { inclusive = true }
                        }
                    },
                    onViewBatch = {
                        navController.navigate("batch/$batchId") {
                            popUpTo("dashboard") { inclusive = false }
                        }
                    }
                )
            } else {
                navController.popBackStack()
            }
        }

        composable(
            "advice/{batchId}/{instar}",
            arguments = listOf(
                navArgument("batchId") { type = NavType.IntType },
                navArgument("instar") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val batchId = backStackEntry.arguments?.getInt("batchId") ?: return@composable
            val instar = backStackEntry.arguments?.getInt("instar") ?: 1
            val advice = batchViewModel.currentAdvice
            val lastEntry = batchViewModel.currentEntries.firstOrNull()

            if (advice != null) {
                AdviceScreen(
                    advice = advice,
                    currentInstar = instar,
                    temperature = lastEntry?.temperature ?: 0.0,
                    humidity = lastEntry?.humidity ?: 0.0,
                    onNavigateBack = { navController.popBackStack() },
                    onTakeAnotherReading = {
                        navController.navigate("climate_entry/$batchId/$instar")
                    },
                    onViewBatch = { navController.popBackStack() }
                )
            }
        }

        // ============ COMMUNITY SCREEN - UPDATED ============
        composable("community") {
            CommunityScreen(
                onNavigateBack = { navController.popBackStack() },
                onPostClick = { postId ->
                    navController.navigate("post/$postId")
                },
                viewModel = communityViewModel
            )
        }

        composable(
            "post/{postId}",
            arguments = listOf(
                navArgument("postId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: return@composable
            PostDetailScreen(
                postId = postId,
                onNavigateBack = { navController.popBackStack() },
                viewModel = communityViewModel
            )
        }
    }
}
