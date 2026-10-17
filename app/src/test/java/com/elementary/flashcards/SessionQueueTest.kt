package com.elementary.flashcards

import com.elementary.flashcards.data.Card
import com.elementary.flashcards.data.DeckCatalog
import com.elementary.flashcards.data.DeckIds
import com.elementary.flashcards.data.PlaySession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionQueueTest {
    @Test
    fun shuffleIsStableForASeedAndDrawsTen() {
        val deck = DeckCatalog.byId(DeckIds.ADD_SINGLE)
        val first = DeckCatalog.draw(deck, seed = 42).map { it.id }
        val second = DeckCatalog.draw(deck, seed = 42).map { it.id }
        val other = DeckCatalog.draw(deck, seed = 7).map { it.id }
        assertEquals(first, second)
        assertNotEquals(first, other)
        assertEquals(10, first.size)
        assertEquals(first.size, first.toSet().size)
    }

    @Test
    fun notYetReturnsACardOnce() {
        val cards = listOf(
            Card("a", "1 + 1", "2"),
            Card("b", "1 + 2", "3"),
            Card("c", "1 + 3", "4"),
        )
        val session = PlaySession(cards)
        repeat(3) { session.answer(right = false) }
        assertEquals(3, session.remainingCount)

        val returned = mutableListOf<String>()
        repeat(3) {
            returned += session.current!!.id
            session.answer(right = false)
        }
        assertEquals(listOf("a", "b", "c"), returned)
        assertTrue(session.isComplete)
        assertEquals(6, session.answerCount)
    }

    @Test
    fun rightAnswerDoesNotReturn() {
        val cards = listOf(Card("a", "2 + 2", "4"), Card("b", "3 + 3", "6"))
        val session = PlaySession(cards)
        session.answer(right = true)
        assertEquals("b", session.current!!.id)
        session.answer(right = true)
        assertTrue(session.isComplete)
        assertEquals(2, session.answerCount)
        assertTrue(session.missedCardIds.isEmpty())
    }
}
