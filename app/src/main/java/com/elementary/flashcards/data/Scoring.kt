package com.elementary.flashcards.data

fun CardStat.afterAnswer(right: Boolean): CardStat {
    return copy(
        score = score + if (right) 1 else -1,
        times = times + 1,
    )
}

/**
 * Applies every answer in order. A card that comes back is scored again.
 * Score may go below zero. Untouched cards are left out of the result.
 */
fun applyAnswers(
    deckId: String,
    existing: Map<String, CardStat>,
    answers: List<MarkedAnswer>,
): List<CardStat> {
    val rolling = existing.toMutableMap()
    val touched = linkedSetOf<String>()
    for (answer in answers) {
        val current = rolling[answer.cardId] ?: CardStat(deckId, answer.cardId, 0, 0)
        rolling[answer.cardId] = current.afterAnswer(answer.right)
        touched += answer.cardId
    }
    return touched.map { rolling.getValue(it) }
}
