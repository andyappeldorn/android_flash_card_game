package com.elementary.flashcards.data

fun formatClock(durationMillis: Long): String {
    val totalSeconds = (durationMillis.coerceAtLeast(0L)) / 1000L
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}

fun formatSessionLine(right: Int, answers: Int, durationMillis: Long): String {
    return "$right of $answers · ${formatClock(durationMillis)}"
}

fun formatSessionLine(session: SessionRecord): String {
    return formatSessionLine(session.rightCount, session.answerCount, session.durationMillis)
}
