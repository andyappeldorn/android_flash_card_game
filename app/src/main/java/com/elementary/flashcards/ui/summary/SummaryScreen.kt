package com.elementary.flashcards.ui.summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elementary.flashcards.data.SessionSummary
import com.elementary.flashcards.data.StatsRepository
import com.elementary.flashcards.data.formatClock
import com.elementary.flashcards.ui.components.Fireworks
import com.elementary.flashcards.ui.components.Turtle
import com.elementary.flashcards.ui.components.TurtleMood
import com.elementary.flashcards.ui.theme.Ink
import com.elementary.flashcards.ui.theme.InkSoft
import com.elementary.flashcards.ui.theme.Paper
import com.elementary.flashcards.ui.theme.Sage

private class LoadedSummary(val summary: SessionSummary?)

@Composable
fun SummaryScreen(
    deckId: String,
    stats: StatsRepository,
    onDone: () -> Unit,
) {
    val loaded by produceState<LoadedSummary?>(initialValue = null, deckId) {
        value = LoadedSummary(stats.summaryFor(deckId))
    }
    Scaffold(containerColor = Paper) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            val recap = loaded?.summary
            if (loaded == null) {
                CircularProgressIndicator(Modifier.align(Alignment.Center), color = Sage)
            } else if (recap == null) {
                Column(
                    Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text("That session is not here.", style = MaterialTheme.typography.titleLarge)
                    Button(onClick = onDone) { Text("Back to decks") }
                }
            } else {
                if (recap.celebrate) {
                    Fireworks(Modifier.fillMaxSize())
                }
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Turtle(
                        mood = if (recap.right > 0) TurtleMood.Pleased else TurtleMood.Unbothered,
                        modifier = Modifier.size(120.dp),
                    )
                    Text(
                        "You got ${recap.right} of ${recap.answers}.",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Ink,
                    )
                    Text(
                        "That took ${formatClock(recap.durationMillis)}.",
                        style = MaterialTheme.typography.titleMedium,
                        color = InkSoft,
                    )
                    Text(
                        if (recap.review.isEmpty()) "Every card was right this time." else "These want another look",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                    recap.review.forEach { card ->
                        Column(Modifier.padding(vertical = 4.dp)) {
                            Text(card.prompt, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Answer ${card.answer} · score ${card.score}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = InkSoft,
                            )
                        }
                    }
                    Button(
                        onClick = onDone,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .heightIn(min = 64.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(containerColor = Sage, contentColor = Color.White),
                    ) {
                        Text("Back to decks", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
