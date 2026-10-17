package com.elementary.flashcards

import com.elementary.flashcards.data.Card
import com.elementary.flashcards.data.PlaySession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaySessionTest {
    private fun cards(count: Int) = List(count) { index ->
        val n = index + 1
        Card("c$n", "$n + 0", "$n")
    }

    @Test
    fun notYetComesBackOnceAndASecondNotYetDoesNot() {
        val session = PlaySession(cards(4))
        assertEquals("c1", session.current?.id)
        session.answer(right = false)
        assertEquals(listOf("c2", "c3", "c4", "c1"), session.queuedIds())
        session.answer(right = true)
        session.answer(right = true)
        session.answer(right = true)
        assertEquals("c1", session.current?.id)
        session.answer(right = false)
        assertTrue(session.isComplete)
        assertEquals(5, session.answerCount)
        assertEquals(3, session.rightCount)
        assertEquals(listOf("c1"), session.missedCardIds)
    }

    @Test
    fun rightAnswersAreNotRequeued() {
        val session = PlaySession(cards(3))
        repeat(3) { session.answer(right = true) }
        assertTrue(session.isComplete)
        assertEquals(3, session.answerCount)
        assertEquals(3, session.rightCount)
        assertTrue(session.missedCardIds.isEmpty())
    }

    @Test
    fun twoMissesEachReturnOnceInTheOrderTheyWereMissed() {
        val session = PlaySession(cards(3))
        session.answer(right = false) // c1 returns
        session.answer(right = false) // c2 returns
        session.answer(right = true) // c3
        assertEquals("c1", session.current?.id)
        session.answer(right = true)
        assertEquals("c2", session.current?.id)
        session.answer(right = false) // second miss, stays done
        assertTrue(session.isComplete)
        assertEquals(listOf("c1", "c2"), session.missedCardIds)
        assertEquals(5, session.answerCount)
        assertEquals(2, session.rightCount)
    }

    @Test
    fun aLoneMissedCardReturnsImmediatelyThenStops() {
        val session = PlaySession(cards(1))
        session.answer(right = false)
        assertFalse(session.isComplete)
        assertEquals("c1", session.current?.id)
        session.answer(right = false)
        assertTrue(session.isComplete)
        assertEquals(2, session.answerCount)
        assertEquals(0, session.rightCount)
        assertEquals(listOf("c1"), session.missedCardIds)
    }
}
