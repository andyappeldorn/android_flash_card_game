package com.elementary.flashcards.ui.progress

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.elementary.flashcards.data.CardStat
import com.elementary.flashcards.data.DeckCatalog
import com.elementary.flashcards.data.StatsRepository
import com.elementary.flashcards.data.formatSessionLine
import com.elementary.flashcards.ui.components.Turtle
import com.elementary.flashcards.ui.components.TurtleMood
import com.elementary.flashcards.ui.theme.Clay
import com.elementary.flashcards.ui.theme.Ink
import com.elementary.flashcards.ui.theme.InkSoft
import com.elementary.flashcards.ui.theme.Paper
import com.elementary.flashcards.ui.theme.Sage
import kotlinx.coroutines.launch

private enum class ResetTarget { Deck, Everything }

@Composable
fun ProgressScreen(
    stats: StatsRepository,
    onBack: () -> Unit,
) {
    val cards by stats.observeCards().collectAsStateWithLifecycle(emptyList())
    val sessions by stats.observeSessions().collectAsStateWithLifecycle(emptyList())
    val practiced = cards.filter { it.times > 0 }
    val prompts = remember {
        DeckCatalog.decks.flatMap { deck -> deck.cards }.associate { it.id to it.prompt }
    }
    var selectedId by remember { mutableStateOf(DeckCatalog.decks.first().id) }
    var confirm by remember { mutableStateOf<ResetTarget?>(null) }
    val scope = rememberCoroutineScope()
    val anyData = practiced.isNotEmpty() || sessions.isNotEmpty()
    val deckCards = practiced
        .filter { it.deckId == selectedId }
        .sortedWith(compareBy<CardStat> { it.score }.thenBy { prompts[it.cardId] ?: it.cardId })
    val deckSessions = sessions
        .filter { it.deckId == selectedId }
        .sortedByDescending { it.id }
    val deckHasData = deckCards.isNotEmpty() || deckSessions.isNotEmpty()

    Scaffold(
        containerColor = Paper,
        topBar = {
            TopAppBar(
                title = { Text("Progress") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
        if (!anyData) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Turtle(TurtleMood.Curious, Modifier.size(140.dp))
                Text(
                    "No scores yet.",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = 12.dp),
                )
                Text(
                    "They show up after the first answer.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = InkSoft,
                )
            }
        } else {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        DeckCatalog.decks.forEach { deck ->
                            FilterChip(
                                selected = deck.id == selectedId,
                                onClick = { selectedId = deck.id },
                                label = { Text(deck.title) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Sage,
                                    selectedLabelColor = Color.White,
                                ),
                            )
                        }
                    }
                }
                if (!deckHasData) {
                    item {
                        Text(
                            "No answers for this deck yet.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = InkSoft,
                            modifier = Modifier.padding(vertical = 12.dp),
                        )
                    }
                } else {
                    item {
                        Text("Cards", style = MaterialTheme.typography.titleLarge)
                    }
                    items(deckCards, key = { it.cardId }) { stat ->
                        Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                            Text(
                                prompts[stat.cardId] ?: stat.cardId,
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                "Score ${stat.score} · ${stat.times} times",
                                style = MaterialTheme.typography.bodyLarge,
                                color = InkSoft,
                            )
                        }
                    }
                    item {
                        Text(
                            "Recent sessions",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(top = 12.dp),
                        )
                    }
                    items(deckSessions, key = { it.id }) { session ->
                        Text(
                            formatSessionLine(session),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                    }
                }
                item {
                    Column(
                        Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 28.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        if (deckHasData) {
                            OutlinedButton(
                                onClick = { confirm = ResetTarget.Deck },
                                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                            ) {
                                Text("Reset this deck", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Button(
                            onClick = { confirm = ResetTarget.Everything },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Clay, contentColor = Color.White),
                        ) {
                            Text("Reset everything", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (confirm != null) {
        val target = confirm
        AlertDialog(
            onDismissRequest = { confirm = null },
            title = {
                Text(if (target == ResetTarget.Everything) "Reset everything?" else "Reset this deck?")
            },
            text = { Text("Scores and session history will be erased.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirm = null
                        scope.launch {
                            if (target == ResetTarget.Everything) {
                                stats.resetAll()
                            } else {
                                stats.resetDeck(selectedId)
                            }
                        }
                    },
                ) {
                    Text("Erase")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirm = null }) {
                    Text("Keep")
                }
            },
        )
    }
}
