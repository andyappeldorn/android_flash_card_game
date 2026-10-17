package com.elementary.flashcards

import com.elementary.flashcards.data.MarkedAnswer
import com.elementary.flashcards.data.StatsRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PracticeStoreLogicTest {
    @Test
    fun firstSessionDoesNotCelebrateAndTheNextOneCan() = runBlocking {
        val store = MemoryStatsStore()
        val stats = StatsRepository(store)
        stats.saveCompletedSession(
            deckId = "add_s",
            startedAt = 0,
            endedAt = 5_000,
            answers = listOf(MarkedAnswer("add_s_4_4", right = false)),
            missedCardIds = listOf("add_s_4_4"),
        )
        assertFalse(stats.summaryFor("add_s")!!.celebrate)

        stats.saveCompletedSession(
            deckId = "add_s",
            startedAt = 10_000,
            endedAt = 14_000,
            answers = listOf(MarkedAnswer("add_s_4_4", right = true)),
            missedCardIds = emptyList(),
        )
        val summary = stats.summaryFor("add_s")!!
        assertTrue(summary.celebrate)
        assertEquals(1, summary.right)
        val card = store.allCards().single { it.cardId == "add_s_4_4" }
        assertEquals(0, card.score)
        assertEquals(2, card.times)
    }

    @Test
    fun aTieOnEitherMeasureSkipsFireworks() = runBlocking {
        val stats = StatsRepository(MemoryStatsStore())
        stats.saveCompletedSession(
            deckId = "add_s",
            startedAt = 0,
            endedAt = 5_000,
            answers = listOf(MarkedAnswer("a", true), MarkedAnswer("a", false)),
            missedCardIds = listOf("a"),
        )
        stats.saveCompletedSession(
            deckId = "add_s",
            startedAt = 10_000,
            endedAt = 14_000,
            answers = listOf(MarkedAnswer("a", true), MarkedAnswer("a", false)),
            missedCardIds = listOf("a"),
        )
        assertFalse(stats.summaryFor("add_s")!!.celebrate)

        stats.saveCompletedSession(
            deckId = "add_s",
            startedAt = 20_000,
            endedAt = 24_000,
            answers = listOf(MarkedAnswer("a", true)),
            missedCardIds = emptyList(),
        )
        assertFalse(stats.summaryFor("add_s")!!.celebrate)
    }

    @Test
    fun resetDeckLeavesTheOtherDeckAlone() = runBlocking {
        val store = MemoryStatsStore()
        val stats = StatsRepository(store)
        stats.saveCompletedSession(
            deckId = "add_s",
            startedAt = 0,
            endedAt = 1_000,
            answers = listOf(MarkedAnswer("add_s_1_1", true)),
            missedCardIds = emptyList(),
        )
        stats.saveCompletedSession(
            deckId = "mul_s",
            startedAt = 0,
            endedAt = 2_000,
            answers = listOf(MarkedAnswer("mul_s_9_9", false)),
            missedCardIds = listOf("mul_s_9_9"),
        )
        stats.resetDeck("add_s")
        assertTrue(store.allSessions().all { it.deckId == "mul_s" })
        assertEquals(listOf("mul_s"), store.allCards().map { it.deckId })
        assertEquals(-1, store.allCards().single().score)
        assertEquals(1, store.allCards().single().times)
    }

    @Test
    fun resetAllClearsScoresAndSessions() = runBlocking {
        val store = MemoryStatsStore()
        val stats = StatsRepository(store)
        stats.saveCompletedSession(
            deckId = "add_s",
            startedAt = 0,
            endedAt = 1_000,
            answers = listOf(MarkedAnswer("add_s_1_1", false)),
            missedCardIds = listOf("add_s_1_1"),
        )
        stats.resetAll()
        assertTrue(store.allCards().isEmpty())
        assertTrue(store.allSessions().isEmpty())
        assertEquals(null, stats.summaryFor("add_s"))
    }
}
