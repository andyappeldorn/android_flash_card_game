package com.elementary.flashcards.ui.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.elementary.flashcards.data.Deck
import com.elementary.flashcards.data.DeckCatalog
import com.elementary.flashcards.data.PlaySession
import com.elementary.flashcards.data.SESSION_SIZE
import com.elementary.flashcards.data.StatsRepository
import com.elementary.flashcards.data.afterAnswer
import com.elementary.flashcards.ui.components.TurtleMood
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

enum class PlayPhase { Loading, Question, Reacting }

data class PlayUiState(
    val phase: PlayPhase = PlayPhase.Loading,
    val deckTitle: String = "",
    val prompt: String = "",
    val answer: String = "",
    val options: List<String> = emptyList(),
    val selectedOption: String? = null,
    val score: Int = 0,
    val times: Int = 0,
    val progress: Float = 0f,
    val mood: TurtleMood = TurtleMood.Curious,
    val banner: String? = null,
    val finished: Boolean = false,
)

class PlayViewModel(
    private val deck: Deck,
    private val repository: StatsRepository,
    seed: Long = System.nanoTime(),
) : ViewModel() {
    private val random = Random(seed)
    private val drawn = DeckCatalog.draw(deck, seed, SESSION_SIZE)
    private val session = PlaySession(drawn)
    private val startedAt = System.currentTimeMillis()
    private val bases = mutableMapOf<String, com.elementary.flashcards.data.CardStat>()
    private var saved = false

    private val _state = MutableStateFlow(PlayUiState(deckTitle = deck.title))
    val state: StateFlow<PlayUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            bases.putAll(repository.cardStats(deck.id, drawn.map { it.id }))
            showQuestion(banner = null)
        }
    }

    fun selectOption(option: String) {
        val current = _state.value
        if (current.phase != PlayPhase.Question || session.current == null) return
        val card = session.current ?: return
        val right = option == card.answer
        session.answer(right)
        val shown = scoreFor(card.id)
        val complete = session.isComplete
        _state.value = current.copy(
            phase = PlayPhase.Reacting,
            selectedOption = option,
            mood = if (right) TurtleMood.Pleased else TurtleMood.Unbothered,
            banner = if (right) null else "Correct answer: ${card.answer}",
            score = shown.score,
            times = shown.times,
            progress = session.progress(),
        )
        viewModelScope.launch {
            if (complete) {
                persistIfComplete()
            }
            delay(if (right) 650 else 1000)
            if (complete) {
                _state.update { it.copy(finished = true) }
            } else {
                showQuestion(banner = if (right) null else "Let's see that one again.")
            }
        }
    }

    private suspend fun persistIfComplete() {
        if (saved || !session.isComplete) return
        saved = true
        val endedAt = System.currentTimeMillis()
        withContext(NonCancellable) {
            repository.saveCompletedSession(
                deckId = deck.id,
                startedAt = startedAt,
                endedAt = endedAt,
                answers = session.answers,
                missedCardIds = session.missedCardIds,
            )
        }
    }

    private fun showQuestion(banner: String?) {
        val card = session.current ?: return
        val shown = scoreFor(card.id)
        val options = OptionsGenerator.generateOptions(card, deck, random)
        _state.value = PlayUiState(
            phase = PlayPhase.Question,
            deckTitle = deck.title,
            prompt = card.prompt,
            answer = card.answer,
            options = options,
            selectedOption = null,
            score = shown.score,
            times = shown.times,
            progress = session.progress(),
            mood = TurtleMood.Curious,
            banner = banner,
            finished = false,
        )
    }

    private fun scoreFor(cardId: String): com.elementary.flashcards.data.CardStat {
        val base = bases[cardId] ?: com.elementary.flashcards.data.CardStat(deck.id, cardId, 0, 0)
        return session.answers
            .filter { it.cardId == cardId }
            .fold(base) { stat, answer -> stat.afterAnswer(answer.right) }
    }

    companion object {
        fun factory(deckId: String, repository: StatsRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PlayViewModel(DeckCatalog.byId(deckId), repository) as T
                }
            }
        }
    }
}
