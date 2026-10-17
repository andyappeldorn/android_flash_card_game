package com.elementary.flashcards

import com.elementary.flashcards.data.Card
import com.elementary.flashcards.data.Deck
import com.elementary.flashcards.data.DeckCatalog
import com.elementary.flashcards.data.DeckIds
import com.elementary.flashcards.ui.play.OptionsGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs
import kotlin.random.Random

class OptionsGeneratorTest {
    @Test
    fun returnsFourDistinctOptionsContainingCorrectAnswerForNormalDeck() {
        val deck = DeckCatalog.byId(DeckIds.ADD_SINGLE)
        val card = deck.cards.first() // 0 + 0 = 0
        val options = OptionsGenerator.generateOptions(card, deck, Random(123))

        assertEquals(4, options.size)
        assertEquals(4, options.toSet().size)
        assertTrue(options.contains(card.answer))
    }

    @Test
    fun generatesOptionsCloseToCorrectAnswerForNumericCards() {
        val card = Card("c1", "25 + 20", "45")
        val deck = DeckCatalog.byId(DeckIds.ADD_TO_100)
        val options = OptionsGenerator.generateOptions(card, deck, Random(789))

        assertEquals(4, options.size)
        assertEquals(4, options.toSet().size)
        assertTrue(options.contains("45"))

        val correctInt = 45
        for (option in options) {
            val optionInt = option.toInt()
            val difference = abs(optionInt - correctInt)
            assertTrue("Option $optionInt should be close to $correctInt (diff was $difference)", difference <= 10)
        }
    }

    @Test
    fun handlesSmallDeckWithSingleCardGracefully() {
        val singleCard = Card("c1", "5 + 5", "10")
        val smallDeck = Deck("small", "Small Deck", listOf(singleCard))
        val options = OptionsGenerator.generateOptions(singleCard, smallDeck, Random(456))

        assertEquals(4, options.size)
        assertEquals(4, options.toSet().size)
        assertTrue(options.contains("10"))
    }
}
