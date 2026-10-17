package com.elementary.flashcards.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.elementary.flashcards.ui.theme.PaperDeep
import com.elementary.flashcards.ui.theme.Sage

@Composable
fun ThinProgress(progress: Float, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(PaperDeep),
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .background(Sage),
        )
    }
}
