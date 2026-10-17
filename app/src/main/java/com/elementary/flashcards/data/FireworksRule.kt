package com.elementary.flashcards.data

object FireworksRule {
    /**
     * True only when this session is strictly more accurate and strictly
     * shorter than the previous session of the same deck. The first session
     * has no previous row, so it never celebrates. A tie on either measure
     * does not.
     */
    fun shouldCelebrate(current: SessionRecord, previous: SessionRecord?): Boolean {
        if (previous == null) return false
        if (current.answerCount <= 0 || previous.answerCount <= 0) return false
        val accuracyBetter =
            current.rightCount.toLong() * previous.answerCount >
                previous.rightCount.toLong() * current.answerCount
        val faster = current.durationMillis < previous.durationMillis
        return accuracyBetter && faster
    }
}
