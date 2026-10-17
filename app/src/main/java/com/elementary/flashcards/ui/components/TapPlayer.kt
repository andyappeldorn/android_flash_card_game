package com.elementary.flashcards.ui.components

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class TapPlayer(context: Context) {
    private val tone: ToneGenerator? = try {
        ToneGenerator(AudioManager.STREAM_MUSIC, 28)
    } catch (_: RuntimeException) {
        null
    }

    fun play(enabled: Boolean) {
        if (!enabled) return
        tone?.startTone(ToneGenerator.TONE_PROP_ACK, 40)
    }

    fun release() {
        tone?.release()
    }
}

@Composable
fun rememberTapPlayer(): TapPlayer {
    val context = LocalContext.current
    val player = remember { TapPlayer(context.applicationContext) }
    DisposableEffect(player) {
        onDispose { player.release() }
    }
    return player
}
