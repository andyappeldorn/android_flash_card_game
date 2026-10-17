package com.elementary.flashcards.data

/**
 * One sitting of cards. A "Not yet" puts that card at the end of the queue
 * once. A second "Not yet" on the same card does not put it back.
 */
class PlaySession(cards: List<Card>) {
    private val queue = ArrayDeque(cards)
    private val requeued = mutableSetOf<String>()
    private val missed = linkedSetOf<String>()
    private val marked = mutableListOf<MarkedAnswer>()

    init {
        require(cards.isNotEmpty()) { "A session needs at least one card." }
        require(cards.map { it.id }.distinct().size == cards.size) {
            "Session cards must have unique ids."
        }
    }

    val current: Card? get() = queue.firstOrNull()
    val isComplete: Boolean get() = queue.isEmpty()
    val answerCount: Int get() = marked.size
    val rightCount: Int get() = marked.count { it.right }
    val remainingCount: Int get() = queue.size
    val missedCardIds: List<String> get() = missed.toList()
    val answers: List<MarkedAnswer> get() = marked.toList()

    internal fun queuedIds(): List<String> = queue.map { it.id }

    fun progress(): Float {
        val total = answerCount + remainingCount
        if (total == 0) return 1f
        return answerCount.toFloat() / total.toFloat()
    }

    fun answer(right: Boolean): MarkedAnswer {
        check(queue.isNotEmpty()) { "The session is already finished." }
        val card = queue.removeFirst()
        val markedAnswer = MarkedAnswer(card.id, right)
        marked += markedAnswer
        if (!right) {
            missed += card.id
            if (card.id !in requeued) {
                requeued += card.id
                queue.addLast(card)
            }
        }
        return markedAnswer
    }
}
