package com.elementary.flashcards

import com.elementary.flashcards.data.CardStat
import com.elementary.flashcards.data.FireworksRule
import com.elementary.flashcards.data.MarkedAnswer
import com.elementary.flashcards.data.SessionRecord
import com.elementary.flashcards.data.afterAnswer
import com.elementary.flashcards.data.applyAnswers
import com.elementary.flashcards.data.formatClock
import com.elementary.flashcards.data.formatSessionLine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScoringTest {
    @Test
    fun scoreMovesByOneAndTimesAlwaysGrow() {
        val once = CardStat("add_s", "1", 0, 0).afterAnswer(right = true)
        assertEquals(1, once.score)
        assertEquals(1, once.times)
        val twiceWrong = CardStat("add_s", "1", 0, 0).afterAnswer(false).afterAnswer(false)
        assertEquals(-2, twiceWrong.score)
        assertEquals(2, twiceWrong.times)
    }

    @Test
    fun aReturnedCardIsScoredAgain() {
        val updated = applyAnswers(
            deckId = "add_s",
            existing = emptyMap(),
            answers = listOf(
                MarkedAnswer("7_5", right = false),
                MarkedAnswer("7_5", right = true),
            ),
        )
        assertEquals(1, updated.size)
        assertEquals(0, updated.single().score)
        assertEquals(2, updated.single().times)
    }

    @Test
    fun fireworksNeedBothAFasterAndMoreAccurateSession() {
        val previous = session(right = 5, answers = 10, duration = 10_000)
        assertTrue(FireworksRule.shouldCelebrate(session(6, 10, 9_999), previous))
        assertFalse(FireworksRule.shouldCelebrate(session(10, 10, 1), null))
        assertFalse(FireworksRule.shouldCelebrate(session(6, 10, 10_000), previous))
        assertFalse(FireworksRule.shouldCelebrate(session(5, 10, 9_000), previous))
        assertFalse(FireworksRule.shouldCelebrate(session(4, 10, 1_000), previous))
        assertFalse(FireworksRule.shouldCelebrate(session(2, 6, 4_000), session(1, 3, 5_000)))
    }

    @Test
    fun clockFormatsTheHomeLine() {
        assertEquals("1:12", formatClock(72_000))
        assertEquals("0:05", formatClock(5_000))
        assertEquals("8 of 10 · 1:12", formatSessionLine(8, 10, 72_000))
    }

    private fun session(right: Int, answers: Int, duration: Long) = SessionRecord(
        deckId = "add_s",
        startedAt = 0,
        endedAt = duration,
        durationMillis = duration,
        rightCount = right,
        answerCount = answers,
        missedCardIds = emptyList(),
    )
}
