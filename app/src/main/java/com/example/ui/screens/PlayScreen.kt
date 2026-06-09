package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WordPuzzle
import com.example.ui.viewmodel.GameViewModel

// Visual Design Theme Accent Colors (Vibrant Palette Theme)
val SlateBackground = Color(0xFFFEF7FF)
val DarkCardBg = Color(0xFFF3EDF7)
val ElectricAccent = Color(0xFF6750A4) // Primary Purple
val CyanAccent = Color(0xFF004A77) // Blue/Cyan Accent
val HotPinkAccent = Color(0xFF8C1D18) // Reddish Accent
val BorderSlate = Color(0xFFCAC4D0) // Grey Outline

// Custom Theme Helper Colors for Vibrant Palette
val VibrantTextDark = Color(0xFF1D1B20)
val VibrantDeepPurple = Color(0xFF21005D)
val VibrantMutedText = Color(0xFF49454F)
val VibrantPinkBg = Color(0xFFFFD8E4)
val VibrantBlueBg = Color(0xFFC2E7FF)
val VibrantLavender = Color(0xFFEADDFF)
val VibrantBorderLight = Color(0xFFD0BCFF)
val VibrantWhite = Color(0xFFFFFFFF)

@Composable
fun PlayScreen(viewModel: GameViewModel, modifier: Modifier = Modifier) {
    val currentPuzzleState by viewModel.currentPuzzle.collectAsState()
    val scrambledLettersState by viewModel.scrambledLetters.collectAsState()
    val spelledLetterIndicesState by viewModel.spelledLetterIndices.collectAsState()
    val isCorrectState by viewModel.isCorrectAnswer.collectAsState()
    val feedbackTextState by viewModel.feedbackText.collectAsState()
    val isDailyState by viewModel.isDailyMode.collectAsState()
    val dailyPuzzlesState by viewModel.dailyPuzzles.collectAsState()
    val activeDailyIdxState by viewModel.activeDailyIndex.collectAsState()
    val dailySolvedStatusesState by viewModel.dailySolvedStatuses.collectAsState()
    val dailyCompletedTodayState by viewModel.isDailyCompletedToday.collectAsState()
    val profileState by viewModel.userProfile.collectAsState()

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- HEADER ROW (Title & Streak) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "WORD QUEST",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    color = ElectricAccent,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 1.sp
                )
                Text(
                    text = if (isDailyState) "Today's Daily Challenge" else "Infinite Practice Mode",
                    fontSize = 12.sp,
                    color = VibrantMutedText,
                    fontWeight = FontWeight.Medium
                )
            }

            // Streak Pill (Vibrant Pink & Red design)
            Surface(
                color = VibrantPinkBg,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, HotPinkAccent),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔥", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${profileState?.dailyStreak ?: 0} DAYS",
                        color = HotPinkAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- GAME MODE SELECTOR TAB ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkCardBg)
                .border(1.dp, BorderSlate, RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ModeTab(
                text = "Daily Challenge",
                isSelected = isDailyState,
                onClick = { viewModel.selectGameMode(true) },
                modifier = Modifier.weight(1f)
            )
            ModeTab(
                text = "Practice",
                isSelected = !isDailyState,
                onClick = { viewModel.selectGameMode(false) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // If in Daily mode, display the progress of 3 daily challenges
        if (isDailyState && dailyPuzzlesState.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                dailyPuzzlesState.forEachIndexed { idx, puzzle ->
                    val isSolved = dailySolvedStatusesState.getOrElse(idx) { false }
                    val isActive = activeDailyIdxState == idx

                    Surface(
                        onClick = { viewModel.changeDailyIndex(idx) },
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .size(width = 90.dp, height = 40.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = if (isActive) ElectricAccent else if (isSolved) VibrantBlueBg else DarkCardBg,
                        border = BorderStroke(
                            1.dp,
                            if (isActive) ElectricAccent else if (isSolved) CyanAccent else BorderSlate
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "Puzzle ${idx + 1} " + (if (isSolved) "✅" else ""),
                                color = if (isActive) Color.White else if (isSolved) CyanAccent else VibrantMutedText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // --- CENTRAL GAME BOARD CARD ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .shadow(
                    4.dp,
                    shape = RoundedCornerShape(28.dp),
                    spotColor = ElectricAccent.copy(alpha = 0.3f)
                ),
            colors = CardDefaults.cardColors(containerColor = VibrantLavender),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, VibrantBorderLight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentPuzzleState != null) {
                    val puzzle = currentPuzzleState!!

                    // Category Banner (Deep Purple) and Difficulty Indicator (Vibrant accents)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = ElectricAccent,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(
                                text = puzzle.category.uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                letterSpacing = 1.sp
                            )
                        }

                        Text(
                            text = "Difficulty: ${puzzle.difficulty}",
                            color = when (puzzle.difficulty) {
                                "Easy" -> CyanAccent
                                "Medium" -> ElectricAccent
                                else -> HotPinkAccent
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    // --- CLUE/HINT BODY ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Clue",
                                tint = ElectricAccent,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = puzzle.hint,
                                color = VibrantDeepPurple,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp),
                                lineHeight = 24.sp
                            )
                        }
                    }

                    // --- SPELLED/WORKING TILES ROW ---
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "SPELLED WORD",
                            fontSize = 10.sp,
                            color = VibrantMutedText,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        // Hollow answer slots with Vibrant palette colors (White/Blue/Pink)
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            for (i in scrambledLettersState.indices) {
                                val currentSelectionIndex = spelledLetterIndicesState.getOrNull(i)
                                val letter = if (currentSelectionIndex != null) {
                                    scrambledLettersState.getOrNull(currentSelectionIndex)
                                } else null

                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isCorrectState == true) VibrantBlueBg
                                            else if (isCorrectState == false) VibrantPinkBg
                                            else if (letter != null) Color.White
                                            else DarkCardBg
                                        )
                                        .border(
                                            2.dp,
                                            if (isCorrectState == true) CyanAccent
                                            else if (isCorrectState == false) HotPinkAccent
                                            else if (letter != null) ElectricAccent
                                            else BorderSlate,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            if (letter != null) {
                                                viewModel.tapSpelledLetter(i)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = letter?.toString() ?: "",
                                        color = if (isCorrectState == true) CyanAccent
                                                else if (isCorrectState == false) HotPinkAccent
                                                else ElectricAccent,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- SCRAMBLED TILES CHOICE BANK ---
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "TAP LETTERS TO SPELL",
                            fontSize = 11.sp,
                            color = VibrantMutedText,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            scrambledLettersState.forEachIndexed { idx, letter ->
                                val isUsed = spelledLetterIndicesState.contains(idx)

                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isUsed) DarkCardBg.copy(alpha = 0.5f) else Color.White
                                        )
                                        .border(
                                            1.dp,
                                            if (isUsed) BorderSlate.copy(alpha = 0.5f) else ElectricAccent,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable(enabled = !isUsed) {
                                            viewModel.tapScrambledLetter(idx)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = letter.toString(),
                                        color = if (isUsed) VibrantMutedText.copy(alpha = 0.3f) else VibrantTextDark,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }

                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ElectricAccent)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- FEEDBACK AND ACTIONS BLOCK ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Toast/Status Banner (Vibrant Pink/Blue design based on state)
            AnimatedVisibility(
                visible = feedbackTextState.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    color = if (isCorrectState == true) VibrantBlueBg else VibrantPinkBg,
                    border = BorderStroke(1.dp, if (isCorrectState == true) CyanAccent else HotPinkAccent),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isCorrectState == true) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = "Status",
                            tint = if (isCorrectState == true) CyanAccent else HotPinkAccent
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = feedbackTextState,
                            color = if (isCorrectState == true) CyanAccent else HotPinkAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Big Decision button
            if (isCorrectState == true) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Outlined Share Result button (White background, gray border, purple text)
                    Button(
                        onClick = {
                            val scoreStr = "🟩 Word Quest Daily solved! \n" +
                                    "✨ Profile: ${profileState?.name ?: "Explorer"}\n" +
                                    "🔥 Streak: ${profileState?.dailyStreak ?: 0} Days!\n" +
                                    "🏆 Total Score: ${profileState?.totalPoints ?: 0} PTS\n" +
                                    "Download Word Quest and play with me!"
                            
                            clipboardManager.setText(AnnotatedString(scoreStr))
                            Toast.makeText(context, "Results copied to clipboard!", Toast.LENGTH_SHORT).show()

                            // Native trigger
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, scoreStr)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Results"))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFF79747E)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = ElectricAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share Results", color = ElectricAccent, fontWeight = FontWeight.Bold)
                    }

                    // Next Puzzle Button (Purple solid, white text)
                    Button(
                        onClick = { viewModel.continueNext() },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricAccent),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Next Level", color = Color.White, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next", tint = Color.White)
                    }
                }
            } else {
                // Secondary Controls Row (Shuffle, Clear, Hint) and Submit Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.shuffleScrambled() },
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, BorderSlate, RoundedCornerShape(12.dp))
                            .size(50.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Shuffle", tint = ElectricAccent)
                    }

                    IconButton(
                        onClick = { viewModel.clearSpelling() },
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, BorderSlate, RoundedCornerShape(12.dp))
                            .size(50.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Backspace, contentDescription = "Clear", tint = ElectricAccent)
                    }

                    IconButton(
                        onClick = { viewModel.revealCheat() },
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, BorderSlate, RoundedCornerShape(12.dp))
                            .size(50.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Lightbulb, contentDescription = "Hint", tint = ElectricAccent)
                    }

                    Button(
                        onClick = { viewModel.submitAnswer() },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricAccent),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Submit Word", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ModeTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) ElectricAccent else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else VibrantMutedText,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}
