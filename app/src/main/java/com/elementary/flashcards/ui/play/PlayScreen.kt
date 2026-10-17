package com.elementary.flashcards.ui.play

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elementary.flashcards.data.SettingsRepository
import com.elementary.flashcards.data.StatsRepository
import com.elementary.flashcards.ui.components.TapPlayer
import com.elementary.flashcards.ui.components.ThinProgress
import com.elementary.flashcards.ui.components.Turtle
import com.elementary.flashcards.ui.components.rememberTapPlayer
import com.elementary.flashcards.ui.theme.CardFace
import com.elementary.flashcards.ui.theme.Clay
import com.elementary.flashcards.ui.theme.Ink
import com.elementary.flashcards.ui.theme.InkSoft
import com.elementary.flashcards.ui.theme.Paper
import com.elementary.flashcards.ui.theme.Sage

@Composable
fun PlayScreen(
    deckId: String,
    stats: StatsRepository,
    settings: SettingsRepository,
    onExit: () -> Unit,
    onFinished: () -> Unit,
) {
    val viewModel: PlayViewModel = viewModel(factory = PlayViewModel.factory(deckId, stats))
    val state by viewModel.state.collectAsStateWithLifecycle()
    val soundOn by settings.soundEnabled.collectAsStateWithLifecycle(true)
    val player = rememberTapPlayer()

    LaunchedEffect(state.finished) {
        if (state.finished) onFinished()
    }

    Scaffold(
        containerColor = Paper,
        topBar = {
            TopAppBar(
                title = { Text(state.deckTitle.ifBlank { "Flash Cards" }) },
                navigationIcon = {
                    IconButton(onClick = onExit) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to decks")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Paper,
                    titleContentColor = Ink,
                    navigationIconContentColor = Ink,
                ),
            )
        },
    ) { padding ->
        if (state.phase == PlayPhase.Loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Turtle(state.mood, Modifier.size(140.dp))
            }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            ) {
                ThinProgress(state.progress)
                Box(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = 40.dp)
                        .padding(top = 12.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (state.banner != null) {
                        Text(
                            state.banner!!,
                            style = MaterialTheme.typography.titleMedium,
                            color = Clay,
                        )
                    }
                }
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = CardFace,
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .semantics {
                            contentDescription = state.prompt
                        },
                ) {
                    Box(Modifier.fillMaxSize().padding(20.dp)) {
                        Text(
                            "Score ${state.score} · ${state.times} times",
                            style = MaterialTheme.typography.bodyMedium,
                            color = InkSoft,
                            modifier = Modifier.align(Alignment.TopEnd),
                        )
                        Column(
                            Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                state.prompt,
                                style = MaterialTheme.typography.displayLarge,
                                textAlign = TextAlign.Center,
                                color = Ink,
                            )
                        }
                        Turtle(
                            mood = state.mood,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .size(108.dp),
                        )
                    }
                }
                Box(Modifier.fillMaxWidth().height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    val options = state.options
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        options.take(2).forEach { option ->
                            OptionButton(
                                option = option,
                                state = state,
                                soundOn = soundOn,
                                player = player,
                                onSelect = { viewModel.selectOption(option) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        options.drop(2).take(2).forEach { option ->
                            OptionButton(
                                option = option,
                                state = state,
                                soundOn = soundOn,
                                player = player,
                                onSelect = { viewModel.selectOption(option) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
                Box(Modifier.fillMaxWidth().height(12.dp))
            }
        }
    }
}

@Composable
private fun OptionButton(
    option: String,
    state: PlayUiState,
    soundOn: Boolean,
    player: TapPlayer,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isReacting = state.phase == PlayPhase.Reacting
    val isCorrectAnswer = option == state.answer
    val isSelectedOption = option == state.selectedOption

    val (containerColor, contentColor) = when {
        isReacting && isCorrectAnswer -> Sage to Color.White
        isReacting && isSelectedOption -> Clay to Color.White
        isReacting -> CardFace.copy(alpha = 0.5f) to InkSoft
        else -> CardFace to Ink
    }

    Button(
        onClick = {
            if (state.phase == PlayPhase.Question) {
                player.play(soundOn)
                onSelect()
            }
        },
        enabled = state.phase == PlayPhase.Question,
        modifier = modifier.heightIn(min = 68.dp),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor,
            disabledContentColor = contentColor,
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
    ) {
        Text(
            text = option,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}
