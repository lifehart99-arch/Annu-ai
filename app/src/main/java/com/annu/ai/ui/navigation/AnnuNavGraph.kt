package com.annu.ai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.annu.ai.ui.screens.BrainScreen
import com.annu.ai.ui.screens.ChatScreen
import com.annu.ai.ui.screens.DeviceActionsScreen
import com.annu.ai.ui.screens.HealthScreen
import com.annu.ai.ui.screens.HomeScreen
import com.annu.ai.viewmodel.BrainViewModel
import com.annu.ai.viewmodel.ChatViewModel

object AnnuDestinations {
    const val HOME = "home"
    const val CHAT = "chat"
    const val BRAIN = "brain"
    const val HEALTH = "health"
    const val DEVICE = "device"
}

@Composable
fun AnnuNavGraph(
    navController: NavHostController = rememberNavController(),
    chatViewModel: ChatViewModel = viewModel(),
    brainViewModel: BrainViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = AnnuDestinations.HOME
    ) {
        composable(AnnuDestinations.HOME) {
            HomeScreen(
                chatViewModel = chatViewModel,
                brainViewModel = brainViewModel,
                onNavigateToChat = { navController.navigate(AnnuDestinations.CHAT) },
                onNavigateToBrain = { navController.navigate(AnnuDestinations.BRAIN) },
                onNavigateToHealth = { navController.navigate(AnnuDestinations.HEALTH) },
                onNavigateToDevice = { navController.navigate(AnnuDestinations.DEVICE) }
            )
        }

        composable(AnnuDestinations.CHAT) {
            ChatScreen(
                chatViewModel = chatViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBrain = { navController.navigate(AnnuDestinations.BRAIN) }
            )
        }

        composable(AnnuDestinations.BRAIN) {
            BrainScreen(
                brainViewModel = brainViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AnnuDestinations.HEALTH) {
            HealthScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AnnuDestinations.DEVICE) {
            DeviceActionsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
