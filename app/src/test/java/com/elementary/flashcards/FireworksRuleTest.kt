package com.elementary.flashcards

import com.elementary.flashcards.data.FireworksRule
import com.elementary.flashcards.data.SessionRecord
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FireworksRuleTest {
    @Test
    fun firstSessionNeverCelebrates() {
        assertFalse(FireworksRule.shouldCelebrate(session(right = 10, answers = 10, duration = 1000), null))
    }

    @Test
    fun bothStrictlyBetterCelebrates() {
        val previous = session(right = 8, answers = 10, duration = 80_000)
        val current = session(right = 9, answers = 10, duration = 70_000)
        assertTrue(FireworksRule.shouldCelebrate(current, previous))
    }

    @Test
    fun tieOnAccuracyDoesNotCelebrate() {
        val previous = session(right = 8, answers = 10, duration = 80_000)
        val current = session(right = 8, answers = 10, duration = 50_000)
        assertFalse(FireworksRule.shouldCelebrate(current, previous))
    }

    @Test
    fun tieOnTimeDoesNotCelebrate() {
        val previous = session(right = 8, answers = 10, duration = 60_000)
        val current = session(right = 9, answers = 10, duration = 60_000)
        assertFalse(FireworksRule.shouldCelebrate(current, previous))
    }

    @Test
    fun fasterButLessAccurateDoesNotCelebrate() {
        val previous = session(right = 9, answers = 10, duration = 90_000)
        val current = session(right = 8, answers = 10, duration = 40_000)
        assertFalse(FireworksRule.shouldCelebrate(current, previous))
    }

    @Test
    fun moreAccurateButSlowerDoesNotCelebrate() {
        val previous = session(right = 6, answers = 10, duration = 40_000)
        val current = session(right = 10, answers = 10, duration = 41_000)
        assertFalse(FireworksRule.shouldCelebrate(current, previous))
    }

    @Test
    fun equalFractionsAreATieEvenWhenCountsDiffer() {
        val previous = session(right = 1, answers = 2, duration = 30_000)
        val current = session(right = 2, answers = 4, duration = 10_000)
        assertFalse(FireworksRule.shouldCelebrate(current, previous))
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
