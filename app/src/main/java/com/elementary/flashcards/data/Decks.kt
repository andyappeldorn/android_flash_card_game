package com.elementary.flashcards.data

import kotlin.random.Random

object DeckIds {
    const val ADD_SINGLE = "add_s"
    const val ADD_TO_100 = "add_100"
    const val SUB_SINGLE = "sub_s"
    const val SUB_TO_100 = "sub_100"
    const val MUL_SINGLE = "mul_s"
    const val DIV_TO_100 = "div_100"
}

const val SESSION_SIZE = 10

fun additionSingle(): List<Card> = buildList {
    for (a in 0..9) {
        for (b in 0..9) {
            add(mathCard(DeckIds.ADD_SINGLE, a, b, "+", a + b))
        }
    }
}

fun additionTo100(): List<Card> = buildList {
    for (a in 0..100) {
        for (b in 0..100) {
            if (a + b > 100) continue
            if (a < 10 && b < 10) continue
            add(mathCard(DeckIds.ADD_TO_100, a, b, "+", a + b))
        }
    }
}

fun subtractionSingle(): List<Card> = buildList {
    for (a in 0..9) {
        for (b in 0..a) {
            add(mathCard(DeckIds.SUB_SINGLE, a, b, "-", a - b))
        }
    }
}

fun subtractionTo100(): List<Card> = buildList {
    for (a in 10..100) {
        for (b in 0..a) {
            add(mathCard(DeckIds.SUB_TO_100, a, b, "-", a - b))
        }
    }
}

fun multiplicationSingle(): List<Card> = buildList {
    for (a in 0..9) {
        for (b in 0..9) {
            add(mathCard(DeckIds.MUL_SINGLE, a, b, "×", a * b))
        }
    }
}

fun divisionTo100(): List<Card> = buildList {
    for (dividend in 1..100) {
        for (divisor in 1..dividend) {
            if (dividend % divisor != 0) continue
            add(mathCard(DeckIds.DIV_TO_100, dividend, divisor, "÷", dividend / divisor))
        }
    }
}

object DeckCatalog {
    val decks: List<Deck> by lazy {
        listOf(
            Deck(DeckIds.ADD_SINGLE, "Addition, single digits", additionSingle()),
            Deck(DeckIds.ADD_TO_100, "Addition to 100", additionTo100()),
            Deck(DeckIds.SUB_SINGLE, "Subtraction, single digits", subtractionSingle()),
            Deck(DeckIds.SUB_TO_100, "Subtraction to 100", subtractionTo100()),
            Deck(DeckIds.MUL_SINGLE, "Multiplication, single digits", multiplicationSingle()),
            Deck(DeckIds.DIV_TO_100, "Division to 100", divisionTo100()),
        )
    }

    fun byId(id: String): Deck = decks.first { it.id == id }

    fun draw(deck: Deck, seed: Long, count: Int = SESSION_SIZE): List<Card> {
        return deck.cards.shuffled(Random(seed)).take(count)
    }
}

private fun mathCard(deckId: String, a: Int, b: Int, operator: String, answer: Int): Card {
    return Card(
        id = "${deckId}_${a}_${b}",
        prompt = "$a $operator $b",
        answer = answer.toString(),
    )
}
