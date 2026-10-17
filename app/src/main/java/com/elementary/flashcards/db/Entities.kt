package com.elementary.flashcards.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.elementary.flashcards.data.CardStat
import com.elementary.flashcards.data.SessionRecord

@Entity(tableName = "CardStat", primaryKeys = ["deckId", "cardId"])
data class CardStatEntity(
    val deckId: String,
    val cardId: String,
    val score: Int,
    val times: Int,
)

@Entity(tableName = "Session")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deckId: String,
    val startedAt: Long,
    val endedAt: Long,
    val durationMillis: Long,
    val rightCount: Int,
    val answerCount: Int,
    val missedCardIds: String,
)

fun CardStatEntity.toModel(): CardStat = CardStat(deckId, cardId, score, times)

fun CardStat.toEntity(): CardStatEntity = CardStatEntity(deckId, cardId, score, times)

fun SessionEntity.toModel(): SessionRecord {
    return SessionRecord(
        id = id,
        deckId = deckId,
        startedAt = startedAt,
        endedAt = endedAt,
        durationMillis = durationMillis,
        rightCount = rightCount,
        answerCount = answerCount,
        missedCardIds = if (missedCardIds.isBlank()) emptyList() else missedCardIds.split(','),
    )
}

fun SessionRecord.toEntity(): SessionEntity {
    return SessionEntity(
        id = id,
        deckId = deckId,
        startedAt = startedAt,
        endedAt = endedAt,
        durationMillis = durationMillis,
        rightCount = rightCount,
        answerCount = answerCount,
        missedCardIds = missedCardIds.joinToString(","),
    )
}
