package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.LeaderboardEntryEntity
import com.example.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(viewModel: GameViewModel, modifier: Modifier = Modifier) {
    val leaderboardList by viewModel.leaderboardEntries.collectAsState()
    val profileState by viewModel.userProfile.collectAsState()

    var showAddFriendDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(16.dp)
    ) {
        // --- HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "FAMILY & FRIENDS",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    color = ElectricAccent,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Compete with peers in total points",
                    fontSize = 12.sp,
                    color = VibrantMutedText
                )
            }

            // Quick Actions Add Buddy Button
            Button(
                onClick = { showAddFriendDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "Add Friend",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Add Friend",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- GENERAL STANDINGS BOX ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(1.dp, BorderSlate, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            color = Color.White
        ) {
            if (leaderboardList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ElectricAccent)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(
                        items = leaderboardList,
                        key = { _, entry -> entry.id }
                    ) { index, entry ->
                        val rank = index + 1
                        LeaderboardRowItem(
                            rank = rank,
                            entry = entry,
                            onDelete = {
                                if (entry.isFriend) {
                                    viewModel.removeFriend(entry.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // --- ADD FRIEND CUSTOM MODAL DIALOG ---
    if (showAddFriendDialog) {
        var friendName by remember { mutableStateOf("") }
        var friendScore by remember { mutableStateOf("") }
        var selectedAvatarIndex by remember { mutableStateOf(0) }

        val avatars = listOf("🦊", "🦁", "🐼", "🦉", "🦄", "🐱", "🐶", "🐵", "🦖", "🐸")
        val context = LocalContext.current

        AlertDialog(
            onDismissRequest = { showAddFriendDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "Add Buddy to Leaderboard",
                    color = ElectricAccent,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Enter details below to challenge friends locally:",
                        color = VibrantMutedText,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Nickname Input
                    OutlinedTextField(
                        value = friendName,
                        onValueChange = { friendName = it },
                        label = { Text("Friend's Nickname", color = VibrantMutedText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricAccent,
                            unfocusedBorderColor = BorderSlate,
                            focusedTextColor = VibrantTextDark,
                            unfocusedTextColor = VibrantTextDark,
                            focusedLabelColor = ElectricAccent,
                            unfocusedLabelColor = VibrantMutedText
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Initial High Score Input
                    OutlinedTextField(
                        value = friendScore,
                        onValueChange = { friendScore = it },
                        label = { Text("Total High Score (PTS)", color = VibrantMutedText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricAccent,
                            unfocusedBorderColor = BorderSlate,
                            focusedTextColor = VibrantTextDark,
                            unfocusedTextColor = VibrantTextDark,
                            focusedLabelColor = ElectricAccent,
                            unfocusedLabelColor = VibrantMutedText
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Avatar Picker
                    Text(
                        text = "CHOOSE AVATAR EMOJI",
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        avatars.take(5).forEachIndexed { index, emoji ->
                            val isSelected = selectedAvatarIndex == index
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) ElectricAccent else DarkCardBg)
                                    .border(
                                        1.dp,
                                        if (isSelected) ElectricAccent else BorderSlate,
                                        CircleShape
                                    )
                                    .clickable { selectedAvatarIndex = index },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 20.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        avatars.drop(5).forEachIndexed { subIndex, emoji ->
                            val index = subIndex + 5
                            val isSelected = selectedAvatarIndex == index
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) ElectricAccent else DarkCardBg)
                                    .border(
                                        1.dp,
                                        if (isSelected) ElectricAccent else BorderSlate,
                                        CircleShape
                                    )
                                    .clickable { selectedAvatarIndex = index },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 20.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (friendName.isBlank()) {
                            Toast.makeText(context, "Please enter a name", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.addFriend(
                            friendName,
                            avatars.getOrElse(selectedAvatarIndex) { "🦊" },
                            friendScore
                        )
                        showAddFriendDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricAccent)
                ) {
                    Text("Add Buddy", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFriendDialog = false }) {
                    Text("Cancel", color = VibrantMutedText)
                }
            }
        )
    }
}

@Composable
fun LeaderboardRowItem(
    rank: Int,
    entry: LeaderboardEntryEntity,
    onDelete: () -> Unit
) {
    val highlightColor = if (entry.isSelf) VibrantLavender.copy(alpha = 0.3f) else Color.Transparent
    val borderStroke = if (entry.isSelf) BorderStroke(1.5.dp, ElectricAccent) else BorderStroke(1.dp, BorderSlate)

    Surface(
        color = if (entry.isSelf) VibrantLavender else DarkCardBg,
        border = borderStroke,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(highlightColor)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Rank and Avatar
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Rank Badges
                Box(
                    modifier = Modifier.width(32.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    when (rank) {
                        1 -> Text("🥇", fontSize = 20.sp)
                        2 -> Text("🥈", fontSize = 20.sp)
                        3 -> Text("🥉", fontSize = 20.sp)
                        else -> Text(
                            text = "#$rank",
                            color = VibrantMutedText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // Avatar
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, BorderSlate, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(entry.avatarEmoji, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name and details
                Column {
                    Text(
                        text = entry.name + (if (entry.isSelf) " (You)" else ""),
                        color = if (entry.isSelf) VibrantDeepPurple else VibrantTextDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = if (entry.isFriend) "Friend" else "Global Challenger",
                        color = VibrantMutedText,
                        fontSize = 10.sp
                    )
                }
            }

            // Score and actions
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${entry.score} pts",
                    color = if (entry.isSelf) ElectricAccent else VibrantTextDark,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(end = 6.dp)
                )

                // Delete trash icon if it's a friend
                if (entry.isFriend) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove friend",
                            tint = HotPinkAccent,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(24.dp))
                }
            }
        }
    }
}
