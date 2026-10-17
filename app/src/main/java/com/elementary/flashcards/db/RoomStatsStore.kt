package com.elementary.flashcards.db

import androidx.room.withTransaction
import com.elementary.flashcards.data.CardStat
import com.elementary.flashcards.data.SessionRecord
import com.elementary.flashcards.data.StatsStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomStatsStore(private val database: AppDatabase) : StatsStore {
    override suspend fun cards(deckId: String, cardIds: List<String>): List<CardStat> {
        if (cardIds.isEmpty()) return emptyList()
        return database.cardStats().getByIds(deckId, cardIds).map { it.toModel() }
    }

    override suspend fun allCards(): List<CardStat> {
        return database.cardStats().getAll().map { it.toModel() }
    }

    override suspend fun allSessions(): List<SessionRecord> {
        return database.sessions().getAll().map { it.toModel() }
    }

    override fun observeCards(): Flow<List<CardStat>> {
        return database.cardStats().observeAll().map { rows -> rows.map { it.toModel() } }
    }

    override fun observeSessions(): Flow<List<SessionRecord>> {
        return database.sessions().observeAll().map { rows -> rows.map { it.toModel() } }
    }

    override suspend fun <T> transaction(block: suspend () -> T): T {
        return database.withTransaction(block)
    }

    override suspend fun upsertCards(stats: List<CardStat>) {
        if (stats.isEmpty()) return
        database.cardStats().upsertAll(stats.map { it.toEntity() })
    }

    override suspend fun insertSession(session: SessionRecord) {
        database.sessions().insert(session.toEntity())
    }

    override suspend fun deleteDeck(deckId: String) {
        database.withTransaction {
            database.cardStats().deleteDeck(deckId)
            database.sessions().deleteDeck(deckId)
        }
    }

    override suspend fun deleteAll() {
        database.withTransaction {
            database.cardStats().deleteAll()
            database.sessions().deleteAll()
        }
    }
}
