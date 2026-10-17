package com.elementary.flashcards.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.elementary.flashcards.data.Deck
import com.elementary.flashcards.data.DeckCatalog
import com.elementary.flashcards.data.SessionRecord
import com.elementary.flashcards.data.SettingsRepository
import com.elementary.flashcards.data.StatsRepository
import com.elementary.flashcards.data.formatSessionLine
import com.elementary.flashcards.ui.components.rememberTapPlayer
import com.elementary.flashcards.ui.theme.CardFace
import com.elementary.flashcards.ui.theme.Ink
import com.elementary.flashcards.ui.theme.InkSoft
import com.elementary.flashcards.ui.theme.Paper
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    stats: StatsRepository,
    settings: SettingsRepository,
    onPlay: (String) -> Unit,
    onProgress: () -> Unit,
) {
    val sessions by stats.observeSessions().collectAsStateWithLifecycle(emptyList())
    val soundOn by settings.soundEnabled.collectAsStateWithLifecycle(true)
    val scope = rememberCoroutineScope()
    val player = rememberTapPlayer()
    val latest = remember(sessions) { latestSessionByDeck(sessions) }

    Scaffold(containerColor = Paper) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
        ) {
            Row(
                Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Flash Cards",
                    style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(1f),
                )
                TextButton(
                    onClick = {
                        val next = !soundOn
                        scope.launch { settings.setSoundEnabled(next) }
                        if (next) player.play(true)
                    },
                ) {
                    Text(if (soundOn) "Sound on" else "Muted")
                }
                TextButton(onClick = onProgress) {
                    Text("Progress")
                }
            }
            Text(
                "Pick a deck",
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                color = InkSoft,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f),
            ) {
                items(DeckCatalog.decks, key = { it.id }) { deck ->
                    DeckCard(
                        deck = deck,
                        last = latest[deck.id],
                        onClick = { onPlay(deck.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun DeckCard(deck: Deck, last: SessionRecord?, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = androidx.compose.material3.MaterialTheme.shapes.large,
        color = CardFace,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth().heightIn(min = 112.dp),
    ) {
        Column(Modifier.padding(horizontal = 24.dp, vertical = 22.dp)) {
            Text(deck.title, style = androidx.compose.material3.MaterialTheme.typography.titleLarge, color = Ink)
            if (last != null) {
                Text(
                    formatSessionLine(last),
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = InkSoft,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
    }
}

internal fun latestSessionByDeck(sessions: List<SessionRecord>): Map<String, SessionRecord> {
    return sessions.groupBy { it.deckId }.mapValues { (_, rows) ->
        rows.maxBy { it.id }
    }
}
