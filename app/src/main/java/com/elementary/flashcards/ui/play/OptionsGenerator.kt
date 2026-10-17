package com.elementary.flashcards.ui.play

import com.elementary.flashcards.data.Card
import com.elementary.flashcards.data.Deck
import com.elementary.flashcards.data.DeckCatalog
import kotlin.math.abs
import kotlin.random.Random

object OptionsGenerator {
    fun generateOptions(card: Card, deck: Deck, random: Random = Random): List<String> {
        val correctAnswer = card.answer
        val correctInt = correctAnswer.toIntOrNull()
        val distractors = mutableSetOf<String>()

        if (correctInt != null) {
            // Pool of close numeric offsets
            val offsets = listOf(-1, 1, -2, 2, -3, 3, -4, 4, -5, 5, -10, 10, -8, 8)
            val closeCandidates = mutableListOf<Int>()

            for (offset in offsets) {
                val candidate = correctInt + offset
                if (candidate >= 0 && candidate != correctInt) {
                    closeCandidates.add(candidate)
                }
            }

            // Also consider closest numeric answers present in the deck
            val deckInts = deck.cards
                .mapNotNull { it.answer.toIntOrNull() }
                .filter { it != correctInt }
                .sortedBy { abs(it - correctInt) }
            closeCandidates.addAll(deckInts.take(6))

            val shuffledClose = closeCandidates.distinct().shuffled(random)
            for (candidate in shuffledClose) {
                if (distractors.size < 3) {
                    distractors.add(candidate.toString())
                }
            }
        } else {
            // Non-numeric fallback
            val deckAnswers = deck.cards
                .map { it.answer }
                .filter { it != correctAnswer }
                .distinct()
                .shuffled(random)
            distractors.addAll(deckAnswers.take(3))
        }

        // Safety fallback if still fewer than 3 distractors
        if (distractors.size < 3) {
            val catalogDistractors = DeckCatalog.decks
                .flatMap { d -> d.cards.map { c -> c.answer } }
                .filter { it != correctAnswer && it !in distractors }
                .distinct()
                .shuffled(random)
            distractors.addAll(catalogDistractors.take(3 - distractors.size))
        }

        var offset = 1
        while (distractors.size < 3 && correctInt != null) {
            val candidate1 = (correctInt + offset).toString()
            if (candidate1 !in distractors && candidate1 != correctAnswer) distractors.add(candidate1)
            if (distractors.size < 3) {
                val candidate2 = (correctInt - offset).toString()
                if (candidate2 !in distractors && candidate2 != correctAnswer && (correctInt - offset >= 0)) {
                    distractors.add(candidate2)
                }
            }
            offset++
        }

        var dummy = 0
        while (distractors.size < 3) {
            val dummyStr = dummy.toString()
            if (dummyStr != correctAnswer && dummyStr !in distractors) {
                distractors.add(dummyStr)
            }
            dummy++
        }

        val allOptions = (distractors + correctAnswer).toList().shuffled(random)
        return allOptions
    }
}
