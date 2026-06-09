package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.database.UserProfileEntity
import com.example.data.database.LeaderboardEntryEntity
import com.example.data.model.GameData
import com.example.data.model.WordPuzzle
import com.example.data.repository.GameRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository

    // Flows from database
    val userProfile: StateFlow<UserProfileEntity?>
    val leaderboardEntries: StateFlow<List<LeaderboardEntryEntity>>
    val totalSolvedCount: StateFlow<Int>

    // Navigation and screen management
    var currentScreen = MutableStateFlow(Screen.PLAY)

    // Game Mode selection: TRUE = Daily Challenges, FALSE = Endless Practice Modus
    val isDailyMode = MutableStateFlow(true)

    // Current date formatted "YYYY-MM-DD"
    val currentDateStr: String

    // Daily Challenge specific states
    val dailyPuzzles = MutableStateFlow<List<WordPuzzle>>(emptyList())
    val activeDailyIndex = MutableStateFlow(0)
    val dailySolvedStatuses = MutableStateFlow<List<Boolean>>(listOf(false, false, false))

    // Practice Mode specific states
    val practiceIndex = MutableStateFlow(0)

    // --- Active Game Session State ---
    val currentPuzzle = MutableStateFlow<WordPuzzle?>(null)
    val scrambledLetters = MutableStateFlow<List<Char>>(emptyList())
    // List of indices inside scrambledLetters representing what is currently spelled
    val spelledLetterIndices = MutableStateFlow<List<Int>>(emptyList())
    val isCorrectAnswer = MutableStateFlow<Boolean?>(null) // true = correct, false = shake, null = normal
    val feedbackText = MutableStateFlow("")
    val isDailyCompletedToday = MutableStateFlow(false)

    init {
        val db = AppDatabase.getDatabase(application)
        repository = GameRepository(db.userDao())

        userProfile = repository.userProfile.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        leaderboardEntries = repository.leaderboardEntries.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        totalSolvedCount = repository.totalSolvedCount.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

        // Initialize Today's Date String
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        currentDateStr = sdf.format(Date())

        viewModelScope.launch {
            repository.checkAndInitializeDefaults()
            loadDailyChallenge()
            
            // Check if all daily challenges are completed
            checkDailyCompletedStatus()
            
            // Default load active puzzle
            loadActiveGame()
        }
    }

    private fun checkDailyCompletedStatus() {
        val todayStr = currentDateStr
        val profile = userProfile.value
        if (profile != null) {
            isDailyCompletedToday.value = (profile.lastCompletedDailyDate == todayStr)
        }
    }

    private fun loadDailyChallenge() = viewModelScope.launch {
        val puzzles = GameData.getDailyPuzzles(currentDateStr)
        dailyPuzzles.value = puzzles

        // Check solved statuses
        val statuses = puzzles.map { puzzle ->
            repository.isPuzzleSolved("daily_${currentDateStr}_${puzzle.id}")
        }
        dailySolvedStatuses.value = statuses

        // Set index to the first unsolved daily puzzle, or if all solved, default to 0
        val firstUnsolved = statuses.indexOfFirst { !it }
        activeDailyIndex.value = if (firstUnsolved != -1) firstUnsolved else 0
    }

    fun selectGameMode(isDaily: Boolean) {
        if (isDailyMode.value == isDaily && currentPuzzle.value != null) {
            return // No change
        }
        isDailyMode.value = isDaily
        loadActiveGame()
    }

    fun changeDailyIndex(index: Int) {
        activeDailyIndex.value = index
        loadActiveGame()
    }

    fun loadActiveGame() {
        viewModelScope.launch {
            val isDaily = isDailyMode.value
            val targetPuzzle = if (isDaily) {
                val puzzles = dailyPuzzles.value
                val index = activeDailyIndex.value
                if (puzzles.isNotEmpty() && index in puzzles.indices) puzzles[index] else null
            } else {
                // Determine endless level index
                val index = practiceIndex.value
                val pool = GameData.puzzlePool
                if (pool.isNotEmpty()) pool[index % pool.size] else null
            }

            setPuzzle(targetPuzzle)
        }
    }

    private fun setPuzzle(puzzle: WordPuzzle?) {
        currentPuzzle.value = puzzle
        spelledLetterIndices.value = emptyList()
        isCorrectAnswer.value = null
        feedbackText.value = ""

        if (puzzle != null) {
            val word = puzzle.word.uppercase()
            // Scramble letters
            val letterList = word.toList().shuffled()
            // Ensure length matches, reshuffle if exactly matches original
            if (letterList.joinToString("") == word && word.length > 2) {
                scrambledLetters.value = letterList.shuffled()
            } else {
                scrambledLetters.value = letterList
            }
        } else {
            scrambledLetters.value = emptyList()
        }
    }

    // --- Active Actions ---

    // Tap a tile in the layout's choice bank
    fun tapScrambledLetter(index: Int) {
        if (isCorrectAnswer.value == true) return // already successfully completed

        val currentSpelled = spelledLetterIndices.value
        if (index in currentSpelled) {
            // Un-tap it
            spelledLetterIndices.value = currentSpelled.filter { it != index }
        } else {
            // Tap to append
            spelledLetterIndices.value = currentSpelled + index
        }
        isCorrectAnswer.value = null
        feedbackText.value = ""
    }

    // Tap a tile currently in the spelled/working row (to put it back)
    fun tapSpelledLetter(slotIndex: Int) {
        if (isCorrectAnswer.value == true) return

        val currentSpelled = spelledLetterIndices.value
        if (slotIndex in currentSpelled.indices) {
            spelledLetterIndices.value = currentSpelled.filterIndexed { index, _ -> index != slotIndex }
        }
        isCorrectAnswer.value = null
        feedbackText.value = ""
    }

    fun clearSpelling() {
        if (isCorrectAnswer.value == true) return
        spelledLetterIndices.value = emptyList()
        feedbackText.value = ""
        isCorrectAnswer.value = null
    }

    fun shuffleScrambled() {
        if (isCorrectAnswer.value == true) return
        val currentScrambled = scrambledLetters.value
        if (currentScrambled.isNotEmpty()) {
            // Clear current working selection to prevent indices breaking
            spelledLetterIndices.value = emptyList()
            scrambledLetters.value = currentScrambled.shuffled()
        }
    }

    // Submit Answer
    fun submitAnswer() = viewModelScope.launch {
        val puzzle = currentPuzzle.value ?: return@launch
        val scrambled = scrambledLetters.value
        val spelledIndices = spelledLetterIndices.value

        if (spelledIndices.size != scrambled.size) {
            feedbackText.value = "Need to use all scrambled letters!"
            isCorrectAnswer.value = false
            return@launch
        }

        val spelledWord = spelledIndices.map { scrambled[it] }.joinToString("").uppercase()
        val correctWord = puzzle.word.uppercase()

        if (spelledWord == correctWord) {
            isCorrectAnswer.value = true
            val points = if (puzzle.difficulty == "Easy") 15 else if (puzzle.difficulty == "Medium") 25 else 40
            feedbackText.value = "Magnificent! Correct!"

            val isDaily = isDailyMode.value
            if (isDaily) {
                val levelIdString = "daily_${currentDateStr}_${puzzle.id}"
                repository.recordSolvedPuzzle(levelIdString, points, currentDateStr)

                // Refresh statuses
                val updatedStatuses = dailySolvedStatuses.value.toMutableList()
                updatedStatuses[activeDailyIndex.value] = true
                dailySolvedStatuses.value = updatedStatuses

                // Check if all solved
                if (updatedStatuses.all { it }) {
                    repository.updateDailyStreak(currentDateStr)
                    isDailyCompletedToday.value = true
                    feedbackText.value = "Daily Challenge complete + bonus points!"
                }
            } else {
                val levelIdString = "practice_${practiceIndex.value}_${puzzle.id}"
                repository.recordSolvedPuzzle(levelIdString, points, currentDateStr)
            }
        } else {
            isCorrectAnswer.value = false
            feedbackText.value = "Ah, not quite! Try a different combo."
        }
    }

    fun continueNext() {
        val isDaily = isDailyMode.value
        if (isDaily) {
            val statuses = dailySolvedStatuses.value
            val currentIdx = activeDailyIndex.value
            val nextIdx = (currentIdx + 1) % 3
            activeDailyIndex.value = nextIdx
            loadActiveGame()
        } else {
            practiceIndex.value = practiceIndex.value + 1
            loadActiveGame()
        }
    }

    fun revealCheat() {
        // Simple helper hint: spell first letter or reveal
        val puzzle = currentPuzzle.value ?: return
        val correctWord = puzzle.word.uppercase()
        feedbackText.value = "Hint: Starts with '${correctWord.first()}' and ends with '${correctWord.last()}'!"
    }

    // --- Profile customization actions ---

    fun saveProfile(name: String, avatar: String, themeIndex: Int) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            val updated = current.copy(
                name = name.trim().ifEmpty { current.name },
                avatarEmoji = avatar.trim().ifEmpty { current.avatarEmoji },
                selectedThemeIndex = themeIndex
            )
            repository.updateProfile(updated)
            feedbackText.value = "Profile updated!"
        }
    }

    // --- Leaderboard Competition actions ---

    fun addFriend(name: String, avatar: String, score: String) {
        viewModelScope.launch {
            val points = score.toIntOrNull() ?: 100
            val cleanAvatar = avatar.trim().ifEmpty { "🦊" }
            val cleanName = name.trim().ifEmpty { "Buddy" }
            repository.addFriend(cleanName, cleanAvatar, points)
        }
    }

    fun removeFriend(id: Int) {
        viewModelScope.launch {
            repository.deleteFriend(id)
        }
    }
}

enum class Screen {
    PLAY,
    LEADERBOARD,
    PROFILE
}
