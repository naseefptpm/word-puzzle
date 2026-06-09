package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    // Solved Puzzles
    @Query("SELECT * FROM solved_puzzles WHERE levelId = :levelId LIMIT 1")
    suspend fun getSolvedPuzzle(levelId: String): SolvedPuzzleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSolvedPuzzle(solved: SolvedPuzzleEntity)

    @Query("SELECT COUNT(*) FROM solved_puzzles WHERE isSolved = 1")
    fun getSolvedCountFlow(): Flow<Int>

    // Leaderboard
    @Query("SELECT * FROM leaderboard_entries ORDER BY score DESC")
    fun getLeaderboardEntries(): Flow<List<LeaderboardEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboardEntry(entry: LeaderboardEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboardEntries(entries: List<LeaderboardEntryEntity>)

    @Query("DELETE FROM leaderboard_entries WHERE id = :id")
    suspend fun deleteLeaderboardEntry(id: Int)

    @Query("DELETE FROM leaderboard_entries WHERE isSelf = 1")
    suspend fun deleteSelfLeaderboardEntries()
}
