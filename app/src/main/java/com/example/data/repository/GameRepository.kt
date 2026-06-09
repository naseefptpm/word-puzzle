package com.example.data.repository

import com.example.data.database.UserDao
import com.example.data.database.UserProfileEntity
import com.example.data.database.SolvedPuzzleEntity
import com.example.data.database.LeaderboardEntryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameRepository(private val userDao: UserDao) {

    val userProfile: Flow<UserProfileEntity?> = userDao.getUserProfile()
    val leaderboardEntries: Flow<List<LeaderboardEntryEntity>> = userDao.getLeaderboardEntries()
    val totalSolvedCount: Flow<Int> = userDao.getSolvedCountFlow()

    suspend fun checkAndInitializeDefaults() = withContext(Dispatchers.IO) {
        // 1. Ensure User Profile exists
        val currentProfile = userDao.getUserProfile().firstOrNull()
        if (currentProfile == null) {
            val defaultProfile = UserProfileEntity(
                name = "WordExplorer",
                avatarEmoji = "🧭",
                selectedThemeIndex = 0,
                currentLevel = 1,
                totalPoints = 0,
                dailyStreak = 0,
                lastCompletedDailyDate = ""
            )
            userDao.insertOrUpdateProfile(defaultProfile)
            
            // Sync user to leaderboard
            userDao.insertLeaderboardEntry(
                LeaderboardEntryEntity(
                    name = defaultProfile.name,
                    avatarEmoji = defaultProfile.avatarEmoji,
                    score = 0,
                    isFriend = false,
                    isSelf = true
                )
            )
        }

        // 2. Ensure Leaderboard contains some friendly automated competitors
        val currentEntries = userDao.getLeaderboardEntries().firstOrNull() ?: emptyList()
        val hasCompetitors = currentEntries.any { !it.isSelf }
        if (!hasCompetitors) {
            val competitors = listOf(
                LeaderboardEntryEntity(name = "Sophia", avatarEmoji = "🦊", score = 350, isFriend = false, isSelf = false),
                LeaderboardEntryEntity(name = "Ethan", avatarEmoji = "🐯", score = 280, isFriend = false, isSelf = false),
                LeaderboardEntryEntity(name = "Olivia", avatarEmoji = "🐼", score = 190, isFriend = true, isSelf = false),
                LeaderboardEntryEntity(name = "Lucas", avatarEmoji = "🦉", score = 120, isFriend = true, isSelf = false),
                LeaderboardEntryEntity(name = "Emma", avatarEmoji = "🦄", score = 80, isFriend = false, isSelf = false)
            )
            userDao.insertLeaderboardEntries(competitors)
        }
    }

    suspend fun updateProfile(profile: UserProfileEntity) = withContext(Dispatchers.IO) {
        userDao.insertOrUpdateProfile(profile)
        
        // Sync user scores/attributes directly in the leaderboard too
        // Remove old self entry first so we don't duplicate
        userDao.deleteSelfLeaderboardEntries()
        userDao.insertLeaderboardEntry(
            LeaderboardEntryEntity(
                name = profile.name,
                avatarEmoji = profile.avatarEmoji,
                score = profile.totalPoints,
                isFriend = false,
                isSelf = true
            )
        )
    }

    suspend fun addPoints(points: Int) = withContext(Dispatchers.IO) {
        val current = userDao.getUserProfile().firstOrNull()
        if (current != null) {
            val updated = current.copy(
                totalPoints = current.totalPoints + points,
                currentLevel = (current.totalPoints + points) / 100 + 1
            )
            updateProfile(updated)
        }
    }

    suspend fun recordSolvedPuzzle(levelId: String, pointsEarned: Int, dateStr: String) = withContext(Dispatchers.IO) {
        val alreadySolved = userDao.getSolvedPuzzle(levelId) != null
        if (!alreadySolved) {
            userDao.insertSolvedPuzzle(
                SolvedPuzzleEntity(
                    levelId = levelId,
                    isSolved = true,
                    solvedDate = dateStr,
                    pointsEarned = pointsEarned
                )
            )
            addPoints(pointsEarned)
        }
    }

    suspend fun isPuzzleSolved(levelId: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext userDao.getSolvedPuzzle(levelId) != null
    }

    suspend fun updateDailyStreak(todayDateStr: String) = withContext(Dispatchers.IO) {
        val profile = userDao.getUserProfile().firstOrNull() ?: return@withContext
        if (profile.lastCompletedDailyDate == todayDateStr) {
            // Already solved today
            return@withContext
        }

        val yesterdayDateStr = getYesterdayDateString(todayDateStr)
        val newStreak = if (profile.lastCompletedDailyDate == yesterdayDateStr) {
            profile.dailyStreak + 1
        } else {
            1 // Reset streak to 1 or start anew
        }

        val updated = profile.copy(
            dailyStreak = newStreak,
            lastCompletedDailyDate = todayDateStr
        )
        updateProfile(updated)
    }

    suspend fun addFriend(name: String, avatarEmoji: String, score: Int) = withContext(Dispatchers.IO) {
        userDao.insertLeaderboardEntry(
            LeaderboardEntryEntity(
                name = name,
                avatarEmoji = avatarEmoji,
                score = score,
                isFriend = true,
                isSelf = false
            )
        )
    }

    suspend fun deleteFriend(id: Int) = withContext(Dispatchers.IO) {
        userDao.deleteLeaderboardEntry(id)
    }

    // Basic date calculations helper
    private fun getYesterdayDateString(today: String): String {
        // Date format YYYY-MM-DD splits
        try {
            val parts = today.split("-")
            if (parts.size == 3) {
                val year = parts[0].toInt()
                val month = parts[1].toInt()
                val day = parts[2].toInt()
                
                // Extremely simple month-duration approximations for offline safety
                val prevDay = day - 1
                if (prevDay >= 1) {
                    return String.format("%04d-%02d-%02d", year, month, prevDay)
                } else {
                    val prevMonth = month - 1
                    if (prevMonth >= 1) {
                        val maxDays = when (prevMonth) {
                            2 -> if (year % 4 == 0) 29 else 28
                            4, 6, 9, 11 -> 30
                            else -> 31
                        }
                        return String.format("%04d-%02d-%02d", year, prevMonth, maxDays)
                    } else {
                        return String.format("%04d-%02d-%02d", year - 1, 12, 31)
                    }
                }
            }
        } catch (e: Exception) {
            // Ignore
        }
        return ""
    }
}
