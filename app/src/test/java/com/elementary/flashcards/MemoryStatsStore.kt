package com.elementary.flashcards

import com.elementary.flashcards.data.CardStat
import com.elementary.flashcards.data.SessionRecord
import com.elementary.flashcards.data.StatsStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * In-memory stand-in for Room. Reset removes the same rows the Room store
 * deletes: card stats and sessions for one deck, or every row.
 */
class MemoryStatsStore : StatsStore {
    private val cards = mutableListOf<CardStat>()
    private val sessions = mutableListOf<SessionRecord>()
    private var nextId = 1L
    private val cardFlow = MutableStateFlow<List<CardStat>>(emptyList())
    private val sessionFlow = MutableStateFlow<List<SessionRecord>>(emptyList())

    override suspend fun cards(deckId: String, cardIds: List<String>): List<CardStat> {
        if (cardIds.isEmpty()) return emptyList()
        val wanted = cardIds.toSet()
        return cards.filter { it.deckId == deckId && it.cardId in wanted }
    }

    override suspend fun allCards(): List<CardStat> = cards.toList()

    override suspend fun allSessions(): List<SessionRecord> = sessions.toList()

    override fun observeCards(): Flow<List<CardStat>> = cardFlow

    override fun observeSessions(): Flow<List<SessionRecord>> = sessionFlow

    override suspend fun <T> transaction(block: suspend () -> T): T = block()

    override suspend fun upsertCards(stats: List<CardStat>) {
        for (stat in stats) {
            cards.removeAll { it.deckId == stat.deckId && it.cardId == stat.cardId }
            cards += stat
        }
        cardFlow.value = cards.toList()
    }

    override suspend fun insertSession(session: SessionRecord) {
        val stored = if (session.id == 0L) session.copy(id = nextId++) else session
        sessions += stored
        sessionFlow.value = sessions.toList()
    }

    override suspend fun deleteDeck(deckId: String) {
        cards.removeAll { it.deckId == deckId }
        sessions.removeAll { it.deckId == deckId }
        cardFlow.value = cards.toList()
        sessionFlow.value = sessions.toList()
    }

    override suspend fun deleteAll() {
        cards.clear()
        sessions.clear()
        cardFlow.value = emptyList()
        sessionFlow.value = emptyList()
    }
}
