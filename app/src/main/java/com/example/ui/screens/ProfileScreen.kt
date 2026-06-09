package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.UserProfileEntity
import com.example.ui.viewmodel.GameViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(viewModel: GameViewModel, modifier: Modifier = Modifier) {
    val profileState by viewModel.userProfile.collectAsState()
    val totalSolvedCountState by viewModel.totalSolvedCount.collectAsState()

    val context = LocalContext.current

    if (profileState == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ElectricAccent)
        }
    } else {
        val profile = profileState!!

        // Calculate Rank Title based on points
        val rankTitle = when {
            profile.totalPoints < 100 -> "Word Novice 🎯"
            profile.totalPoints < 300 -> "Spell Apprentice 📜"
            profile.totalPoints < 600 -> "Lexicon Knight 🛡️"
            profile.totalPoints < 1000 -> "Grammar Titan ⚡"
            else -> "Vocabulary Overlord 🧠"
        }

        // Available avatars for the player to select
        val avatarOptions = listOf("🧭", "🧙‍♂️", "🚀", "👑", "🦄", "🐼", "🦊", "🦁", "🦖", "🛸")

        var editName by remember { mutableStateOf(profile.name) }
        var selectedAvatar by remember { mutableStateOf(profile.avatarEmoji) }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(SlateBackground)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Column {
                    Text(
                        text = "PLAYER PROFILE",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = ElectricAccent,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Tailor your identity and track achievements",
                        fontSize = 12.sp,
                        color = VibrantMutedText
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- VISUAL USER CARD ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, VibrantBorderLight, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = VibrantLavender)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Avatar Circle
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(2.dp, ElectricAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(selectedAvatar, fontSize = 38.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = editName.ifEmpty { "Guest Gamer" },
                        color = VibrantDeepPurple,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )

                    Text(
                        text = rankTitle,
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- STATS GRID ---
            Text(
                text = "YOUR MILESTONES",
                color = VibrantMutedText,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "SOLVED",
                    value = "$totalSolvedCountState",
                    subtitle = "Puzzles",
                    accentColor = CyanAccent,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "SCORE",
                    value = "${profile.totalPoints}",
                    subtitle = "Points",
                    accentColor = ElectricAccent,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "STREAK",
                    value = "${profile.dailyStreak}",
                    subtitle = "Days 🔥",
                    accentColor = HotPinkAccent,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- CUSTOMIZATION FORM ---
            Text(
                text = "EDIT CHARACTER DETAILS",
                color = VibrantMutedText,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSlate, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Enter Player Nickname", color = VibrantMutedText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = VibrantTextDark,
                            unfocusedTextColor = VibrantTextDark,
                            focusedBorderColor = ElectricAccent,
                            unfocusedBorderColor = BorderSlate,
                            focusedLabelColor = ElectricAccent,
                            unfocusedLabelColor = VibrantMutedText
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "CHOOSE AVATAR EMOJI",
                        color = VibrantMutedText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    // Avatar Picker FlowRow on White background
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        avatarOptions.forEach { emoji ->
                            val isSelected = selectedAvatar == emoji
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) ElectricAccent else DarkCardBg)
                                    .border(
                                        2.dp,
                                        if (isSelected) ElectricAccent else BorderSlate,
                                        CircleShape
                                    )
                                    .clickable { selectedAvatar = emoji },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 22.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (editName.isBlank()) {
                                Toast.makeText(context, "Nickname cannot be empty!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            viewModel.saveProfile(editName, selectedAvatar, profile.selectedThemeIndex)
                            Toast.makeText(context, "Profile Saved Successfully!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricAccent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Save Profile",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.border(1.dp, BorderSlate, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = VibrantMutedText,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = accentColor,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = VibrantTextDark,
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp
            )
        }
    }
}
