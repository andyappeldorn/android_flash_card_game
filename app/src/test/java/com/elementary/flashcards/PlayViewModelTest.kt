package com.elementary.flashcards

import com.elementary.flashcards.data.DeckCatalog
import com.elementary.flashcards.data.DeckIds
import com.elementary.flashcards.data.StatsRepository
import com.elementary.flashcards.ui.components.TurtleMood
import com.elementary.flashcards.ui.play.PlayPhase
import com.elementary.flashcards.ui.play.PlayViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlayViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialStateProvidesQuestionAndFourOptions() = runTest {
        val repo = StatsRepository(MemoryStatsStore())
        val deck = DeckCatalog.byId(DeckIds.ADD_SINGLE)
        val viewModel = PlayViewModel(deck, repo, seed = 42L)

        val state = viewModel.state.value
        assertEquals(PlayPhase.Question, state.phase)
        assertEquals(4, state.options.size)
        assertEquals(4, state.options.toSet().size)
        assertTrue(state.options.contains(state.answer))
        assertNull(state.selectedOption)
    }

    @Test
    fun selectingCorrectOptionSetsPleasedMood() = runTest {
        val repo = StatsRepository(MemoryStatsStore())
        val deck = DeckCatalog.byId(DeckIds.ADD_SINGLE)
        val viewModel = PlayViewModel(deck, repo, seed = 42L)

        val correctAnswer = viewModel.state.value.answer
        viewModel.selectOption(correctAnswer)

        val state = viewModel.state.value
        assertEquals(PlayPhase.Reacting, state.phase)
        assertEquals(correctAnswer, state.selectedOption)
        assertEquals(TurtleMood.Pleased, state.mood)
        assertNull(state.banner)
    }

    @Test
    fun selectingWrongOptionSetsUnbotheredMoodAndCorrectAnswerBanner() = runTest {
        val repo = StatsRepository(MemoryStatsStore())
        val deck = DeckCatalog.byId(DeckIds.ADD_SINGLE)
        val viewModel = PlayViewModel(deck, repo, seed = 42L)

        val correctAnswer = viewModel.state.value.answer
        val wrongOption = viewModel.state.value.options.first { it != correctAnswer }

        viewModel.selectOption(wrongOption)

        val state = viewModel.state.value
        assertEquals(PlayPhase.Reacting, state.phase)
        assertEquals(wrongOption, state.selectedOption)
        assertEquals(TurtleMood.Unbothered, state.mood)
        assertEquals("Correct answer: $correctAnswer", state.banner)
    }
}
