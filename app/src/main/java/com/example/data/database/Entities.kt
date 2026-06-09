package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1, // Single-user local profile
    val name: String = "WordMaster",
    val avatarEmoji: String = "🧙‍♂️",
    val selectedThemeIndex: Int = 0, // App theme
    val currentLevel: Int = 1,
    val totalPoints: Int = 0,
    val dailyStreak: Int = 0,
    val lastCompletedDailyDate: String = "" // "YYYY-MM-DD" style
)

@Entity(tableName = "solved_puzzles")
data class SolvedPuzzleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val levelId: String, // "daily_2026_06_10_1" or "classic_level_1"
    val isSolved: Boolean = true,
    val solvedDate: String = "",
    val pointsEarned: Int = 0
)

@Entity(tableName = "leaderboard_entries")
data class LeaderboardEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val avatarEmoji: String,
    val score: Int,
    val isFriend: Boolean = false, // True if added by player
    val isSelf: Boolean = false // True if this represents the local user
)
