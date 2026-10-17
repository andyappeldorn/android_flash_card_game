package com.elementary.flashcards.data

import kotlinx.coroutines.flow.Flow

class StatsRepository(private val store: StatsStore) {
    fun observeCards(): Flow<List<CardStat>> = store.observeCards()

    fun observeSessions(): Flow<List<SessionRecord>> = store.observeSessions()

    suspend fun cardStat(deckId: String, cardId: String): CardStat {
        return store.cards(deckId, listOf(cardId)).firstOrNull()
            ?: CardStat(deckId, cardId, 0, 0)
    }

    suspend fun cardStats(deckId: String, cardIds: List<String>): Map<String, CardStat> {
        val found = store.cards(deckId, cardIds).associateBy { it.cardId }
        return cardIds.associateWith { id ->
            found[id] ?: CardStat(deckId, id, 0, 0)
        }
    }

    suspend fun sessions(deckId: String): List<SessionRecord> {
        return store.allSessions()
            .filter { it.deckId == deckId }
            .sortedBy { it.id }
    }

    suspend fun saveCompletedSession(
        deckId: String,
        startedAt: Long,
        endedAt: Long,
        answers: List<MarkedAnswer>,
        missedCardIds: List<String>,
    ) {
        val ids = answers.map { it.cardId }.distinct()
        val existing = store.cards(deckId, ids).associateBy { it.cardId }
        val updated = applyAnswers(deckId, existing, answers)
        val session = SessionRecord(
            deckId = deckId,
            startedAt = startedAt,
            endedAt = endedAt,
            durationMillis = (endedAt - startedAt).coerceAtLeast(0L),
            rightCount = answers.count { it.right },
            answerCount = answers.size,
            missedCardIds = missedCardIds,
        )
        store.transaction {
            store.upsertCards(updated)
            store.insertSession(session)
        }
    }

    suspend fun summaryFor(deckId: String): SessionSummary? {
        val history = sessions(deckId)
        val current = history.lastOrNull() ?: return null
        val previous = history.dropLast(1).lastOrNull()
        val prompts = DeckCatalog.byId(deckId).cards.associateBy { it.id }
        val scores = store.allCards()
            .filter { it.deckId == deckId }
            .associateBy { it.cardId }
        val review = current.missedCardIds.mapNotNull { cardId ->
            val card = prompts[cardId] ?: return@mapNotNull null
            ReviewCard(
                prompt = card.prompt,
                answer = card.answer,
                score = scores[cardId]?.score ?: 0,
            )
        }
        return SessionSummary(
            right = current.rightCount,
            answers = current.answerCount,
            durationMillis = current.durationMillis,
            celebrate = FireworksRule.shouldCelebrate(current, previous),
            review = review,
        )
    }

    suspend fun resetDeck(deckId: String) {
        store.deleteDeck(deckId)
    }

    suspend fun resetAll() {
        store.deleteAll()
    }
}
