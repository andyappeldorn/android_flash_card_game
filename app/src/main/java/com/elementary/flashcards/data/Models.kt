package com.elementary.flashcards.data

data class Card(
    val id: String,
    val prompt: String,
    val answer: String,
)

data class Deck(
    val id: String,
    val title: String,
    val cards: List<Card>,
)

data class CardStat(
    val deckId: String,
    val cardId: String,
    val score: Int,
    val times: Int,
)

data class MarkedAnswer(
    val cardId: String,
    val right: Boolean,
)

data class SessionRecord(
    val id: Long = 0,
    val deckId: String,
    val startedAt: Long,
    val endedAt: Long,
    val durationMillis: Long,
    val rightCount: Int,
    val answerCount: Int,
    val missedCardIds: List<String>,
)

data class ReviewCard(
    val prompt: String,
    val answer: String,
    val score: Int,
)

data class SessionSummary(
    val right: Int,
    val answers: Int,
    val durationMillis: Long,
    val celebrate: Boolean,
    val review: List<ReviewCard>,
)
