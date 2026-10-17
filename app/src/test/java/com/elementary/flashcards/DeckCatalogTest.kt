package com.elementary.flashcards

import com.elementary.flashcards.data.DeckCatalog
import com.elementary.flashcards.data.DeckIds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeckCatalogTest {
    @Test
    fun decksMatchTheMathRules() {
        val decks = DeckCatalog.decks.associateBy { it.id }
        assertEquals(
            setOf(
                DeckIds.ADD_SINGLE,
                DeckIds.ADD_TO_100,
                DeckIds.SUB_SINGLE,
                DeckIds.SUB_TO_100,
                DeckIds.MUL_SINGLE,
                DeckIds.DIV_TO_100,
            ),
            decks.keys,
        )

        val addSingle = decks.getValue(DeckIds.ADD_SINGLE)
        assertEquals(100, addSingle.cards.size)
        addSingle.cards.forEach { card ->
            val (a, b) = parts(card.id)
            assertTrue(a in 0..9 && b in 0..9)
            assertEquals("$a + $b", card.prompt)
            assertEquals((a + b).toString(), card.answer)
        }

        val add100 = decks.getValue(DeckIds.ADD_TO_100)
        assertEquals(5051, add100.cards.size)
        add100.cards.forEach { card ->
            val (a, b) = parts(card.id)
            assertTrue(a in 0..100 && b in 0..100)
            assertTrue(a + b <= 100)
            assertTrue(a >= 10 || b >= 10)
            assertEquals("$a + $b", card.prompt)
            assertEquals((a + b).toString(), card.answer)
        }
        assertTrue(add100.cards.none { it.id.endsWith("_9_9") && it.id.contains("add") })
        assertTrue(add100.cards.any { it.prompt == "10 + 0" })
        assertTrue(add100.cards.any { it.prompt == "50 + 50" })
        assertTrue(add100.cards.none { it.prompt == "60 + 50" })

        val subSingle = decks.getValue(DeckIds.SUB_SINGLE)
        assertEquals(55, subSingle.cards.size)
        subSingle.cards.forEach { card ->
            val (a, b) = parts(card.id)
            assertTrue(a in 0..9 && b in 0..a)
            assertEquals("$a - $b", card.prompt)
            assertEquals((a - b).toString(), card.answer)
        }

        val sub100 = decks.getValue(DeckIds.SUB_TO_100)
        assertEquals(5096, sub100.cards.size)
        sub100.cards.forEach { card ->
            val (a, b) = parts(card.id)
            assertTrue(a in 10..100 && b in 0..a)
            assertEquals("$a - $b", card.prompt)
            assertEquals((a - b).toString(), card.answer)
        }
        assertTrue(sub100.cards.none { it.prompt == "9 - 0" })
        assertTrue(sub100.cards.any { it.prompt == "100 - 0" })

        val mul = decks.getValue(DeckIds.MUL_SINGLE)
        assertEquals(100, mul.cards.size)
        mul.cards.forEach { card ->
            val (a, b) = parts(card.id)
            assertTrue(a in 0..9 && b in 0..9)
            assertEquals("$a × $b", card.prompt)
            assertEquals((a * b).toString(), card.answer)
        }

        val div = decks.getValue(DeckIds.DIV_TO_100)
        assertEquals(482, div.cards.size)
        div.cards.forEach { card ->
            val (dividend, divisor) = parts(card.id)
            assertTrue(dividend in 1..100)
            assertTrue(divisor in 1..dividend)
            assertEquals(0, dividend % divisor)
            assertEquals("$dividend ÷ $divisor", card.prompt)
            assertEquals((dividend / divisor).toString(), card.answer)
        }
        assertTrue(div.cards.any { it.prompt == "100 ÷ 10" })
        assertTrue(div.cards.none { it.prompt == "100 ÷ 3" })
        assertEquals(div.cards.size, div.cards.map { it.id }.toSet().size)

        decks.values.forEach { deck ->
            assertEquals(deck.cards.size, deck.cards.map { it.id }.toSet().size)
        }
    }

    private fun parts(id: String): Pair<Int, Int> {
        val pieces = id.split("_")
        return pieces[pieces.size - 2].toInt() to pieces.last().toInt()
    }
}
