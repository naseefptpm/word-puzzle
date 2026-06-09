package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun MainContainer(viewModel: GameViewModel) {
    val currentScreenState by viewModel.currentScreen.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .navigationBarsPadding()
            .statusBarsPadding(),
        bottomBar = {
            NavigationBar(
                containerColor = DarkCardBg,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars
            ) {
                NavigationBarItem(
                    selected = currentScreenState == Screen.PLAY,
                    onClick = { viewModel.currentScreen.value = Screen.PLAY },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.GridOn,
                            contentDescription = "Play Game",
                            tint = if (currentScreenState == Screen.PLAY) ElectricAccent else VibrantMutedText
                        )
                    },
                    label = {
                        Text(
                            text = "Play",
                            fontSize = 11.sp,
                            color = if (currentScreenState == Screen.PLAY) ElectricAccent else VibrantMutedText
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = VibrantLavender,
                        selectedIconColor = ElectricAccent,
                        unselectedIconColor = VibrantMutedText
                    ),
                    modifier = Modifier.testTag("nav_play_tab")
                )

                NavigationBarItem(
                    selected = currentScreenState == Screen.LEADERBOARD,
                    onClick = { viewModel.currentScreen.value = Screen.LEADERBOARD },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Leaderboard",
                            tint = if (currentScreenState == Screen.LEADERBOARD) ElectricAccent else VibrantMutedText
                        )
                    },
                    label = {
                        Text(
                            text = "Leaderboard",
                            fontSize = 11.sp,
                            color = if (currentScreenState == Screen.LEADERBOARD) ElectricAccent else VibrantMutedText
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = VibrantLavender,
                        selectedIconColor = ElectricAccent,
                        unselectedIconColor = VibrantMutedText
                    ),
                    modifier = Modifier.testTag("nav_leaderboard_tab")
                )

                NavigationBarItem(
                    selected = currentScreenState == Screen.PROFILE,
                    onClick = { viewModel.currentScreen.value = Screen.PROFILE },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = if (currentScreenState == Screen.PROFILE) ElectricAccent else VibrantMutedText
                        )
                    },
                    label = {
                        Text(
                            text = "Profile",
                            fontSize = 11.sp,
                            color = if (currentScreenState == Screen.PROFILE) ElectricAccent else VibrantMutedText
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = VibrantLavender,
                        selectedIconColor = ElectricAccent,
                        unselectedIconColor = VibrantMutedText
                    ),
                    modifier = Modifier.testTag("nav_profile_tab")
                )
            }
        },
        containerColor = SlateBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreenState) {
                Screen.PLAY -> PlayScreen(viewModel = viewModel)
                Screen.LEADERBOARD -> LeaderboardScreen(viewModel = viewModel)
                Screen.PROFILE -> ProfileScreen(viewModel = viewModel)
            }
        }
    }
}
