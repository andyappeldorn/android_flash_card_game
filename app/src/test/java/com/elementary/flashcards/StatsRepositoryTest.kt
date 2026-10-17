package com.elementary.flashcards

import com.elementary.flashcards.data.DeckIds
import com.elementary.flashcards.data.MarkedAnswer
import com.elementary.flashcards.data.StatsRepository
import com.elementary.flashcards.data.formatSessionLine
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StatsRepositoryTest {
    @Test
    fun saveAppliesScoreAndKeepsTheMissedCardForTheRecap() = runBlocking {
        val repo = StatsRepository(MemoryStatsStore())
        repo.saveCompletedSession(
            deckId = DeckIds.ADD_SINGLE,
            startedAt = 1_000,
            endedAt = 73_000,
            answers = listOf(
                MarkedAnswer("add_s_2_3", false),
                MarkedAnswer("add_s_2_3", true),
                MarkedAnswer("add_s_1_1", true),
            ),
            missedCardIds = listOf("add_s_2_3"),
        )
        val missed = repo.cardStat(DeckIds.ADD_SINGLE, "add_s_2_3")
        assertEquals(0, missed.score)
        assertEquals(2, missed.times)
        val known = repo.cardStat(DeckIds.ADD_SINGLE, "add_s_1_1")
        assertEquals(1, known.score)
        assertEquals(1, known.times)
        val fresh = repo.cardStat(DeckIds.ADD_SINGLE, "add_s_9_9")
        assertEquals(0, fresh.score)
        assertEquals(0, fresh.times)

        val summary = repo.summaryFor(DeckIds.ADD_SINGLE)
        assertEquals(2, summary?.right)
        assertEquals(3, summary?.answers)
        assertFalse(summary!!.celebrate)
        assertEquals("2 + 3", summary.review.single().prompt)
        assertEquals("5", summary.review.single().answer)
        assertEquals(0, summary.review.single().score)
        assertEquals("2 of 3 · 1:12", formatSessionLine(repo.sessions(DeckIds.ADD_SINGLE).single()))
    }

    @Test
    fun secondSessionCelebratesOnlyWhenBothMeasuresImprove() = runBlocking {
        val repo = StatsRepository(MemoryStatsStore())
        save(repo, right = 8, answers = 10, duration = 80_000, startedAt = 0)
        assertFalse(repo.summaryFor(DeckIds.ADD_SINGLE)!!.celebrate)
        save(repo, right = 8, answers = 10, duration = 70_000, startedAt = 100_000)
        assertFalse(repo.summaryFor(DeckIds.ADD_SINGLE)!!.celebrate)
        save(repo, right = 9, answers = 10, duration = 70_000, startedAt = 200_000)
        assertFalse(repo.summaryFor(DeckIds.ADD_SINGLE)!!.celebrate)
        save(repo, right = 10, answers = 10, duration = 60_000, startedAt = 300_000)
        assertTrue(repo.summaryFor(DeckIds.ADD_SINGLE)!!.celebrate)
    }

    @Test
    fun resetDeckErasesThatDeckOnly() = runBlocking {
        val repo = StatsRepository(MemoryStatsStore())
        repo.saveCompletedSession(
            deckId = DeckIds.ADD_SINGLE,
            startedAt = 0,
            endedAt = 1_000,
            answers = listOf(MarkedAnswer("add_s_1_1", true)),
            missedCardIds = emptyList(),
        )
        repo.saveCompletedSession(
            deckId = DeckIds.SUB_SINGLE,
            startedAt = 0,
            endedAt = 2_000,
            answers = listOf(MarkedAnswer("sub_s_4_1", false)),
            missedCardIds = listOf("sub_s_4_1"),
        )
        repo.resetDeck(DeckIds.ADD_SINGLE)
        assertEquals(0, repo.cardStat(DeckIds.ADD_SINGLE, "add_s_1_1").times)
        assertTrue(repo.sessions(DeckIds.ADD_SINGLE).isEmpty())
        assertNull(repo.summaryFor(DeckIds.ADD_SINGLE))
        assertEquals(-1, repo.cardStat(DeckIds.SUB_SINGLE, "sub_s_4_1").score)
        assertEquals(1, repo.cardStat(DeckIds.SUB_SINGLE, "sub_s_4_1").times)
        assertEquals(1, repo.sessions(DeckIds.SUB_SINGLE).size)
    }

    @Test
    fun resetAllErasesEveryDeck() = runBlocking {
        val repo = StatsRepository(MemoryStatsStore())
        repo.saveCompletedSession(
            deckId = DeckIds.ADD_SINGLE,
            startedAt = 0,
            endedAt = 1_000,
            answers = listOf(MarkedAnswer("add_s_1_1", true)),
            missedCardIds = emptyList(),
        )
        repo.saveCompletedSession(
            deckId = DeckIds.MUL_SINGLE,
            startedAt = 0,
            endedAt = 2_000,
            answers = listOf(MarkedAnswer("mul_s_2_2", false), MarkedAnswer("mul_s_2_2", false)),
            missedCardIds = listOf("mul_s_2_2"),
        )
        repo.resetAll()
        assertEquals(0, repo.cardStat(DeckIds.ADD_SINGLE, "add_s_1_1").score)
        assertEquals(0, repo.cardStat(DeckIds.ADD_SINGLE, "add_s_1_1").times)
        assertEquals(0, repo.cardStat(DeckIds.MUL_SINGLE, "mul_s_2_2").times)
        assertTrue(repo.sessions(DeckIds.ADD_SINGLE).isEmpty())
        assertTrue(repo.sessions(DeckIds.MUL_SINGLE).isEmpty())
        assertNull(repo.summaryFor(DeckIds.MUL_SINGLE))
    }

    @Test
    fun sessionLineUsesMinutesAndPaddedSeconds() {
        assertEquals("8 of 10 · 1:12", formatSessionLine(8, 10, 72_000))
        assertEquals("0:05", com.elementary.flashcards.data.formatClock(5_000))
        assertEquals("0:00", com.elementary.flashcards.data.formatClock(499))
    }

    private suspend fun save(
        repo: StatsRepository,
        right: Int,
        answers: Int,
        duration: Long,
        startedAt: Long,
    ) {
        val answersList = List(answers) { index ->
            MarkedAnswer("add_s_0_0", right = index < right)
        }
        repo.saveCompletedSession(
            deckId = DeckIds.ADD_SINGLE,
            startedAt = startedAt,
            endedAt = startedAt + duration,
            answers = answersList,
            missedCardIds = emptyList(),
        )
    }
}
