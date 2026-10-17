package com.elementary.flashcards.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CardStatDao {
    @Query("SELECT * FROM CardStat WHERE deckId = :deckId AND cardId IN (:cardIds)")
    suspend fun getByIds(deckId: String, cardIds: List<String>): List<CardStatEntity>

    @Query("SELECT * FROM CardStat")
    suspend fun getAll(): List<CardStatEntity>

    @Query("SELECT * FROM CardStat")
    fun observeAll(): Flow<List<CardStatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(stats: List<CardStatEntity>)

    @Query("DELETE FROM CardStat WHERE deckId = :deckId")
    suspend fun deleteDeck(deckId: String)

    @Query("DELETE FROM CardStat")
    suspend fun deleteAll()
}

@Dao
interface SessionDao {
    @Insert
    suspend fun insert(session: SessionEntity)

    @Query("SELECT * FROM Session")
    suspend fun getAll(): List<SessionEntity>

    @Query("SELECT * FROM Session ORDER BY endedAt ASC, id ASC")
    fun observeAll(): Flow<List<SessionEntity>>

    @Query("DELETE FROM Session WHERE deckId = :deckId")
    suspend fun deleteDeck(deckId: String)

    @Query("DELETE FROM Session")
    suspend fun deleteAll()
}
