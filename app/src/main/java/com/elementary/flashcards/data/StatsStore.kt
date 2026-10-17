package com.elementary.flashcards.data

import kotlinx.coroutines.flow.Flow

/**
 * Card scores and finished sessions. Room is the on-device store.
 * A session that is left early is never written here.
 */
interface StatsStore {
    suspend fun cards(deckId: String, cardIds: List<String>): List<CardStat>
    suspend fun allCards(): List<CardStat>
    suspend fun allSessions(): List<SessionRecord>
    fun observeCards(): Flow<List<CardStat>>
    fun observeSessions(): Flow<List<SessionRecord>>
    suspend fun <T> transaction(block: suspend () -> T): T
    suspend fun upsertCards(stats: List<CardStat>)
    suspend fun insertSession(session: SessionRecord)
    suspend fun deleteDeck(deckId: String)
    suspend fun deleteAll()
}
