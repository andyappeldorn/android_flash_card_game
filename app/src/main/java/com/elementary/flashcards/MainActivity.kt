package com.elementary.flashcards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.elementary.flashcards.ui.FlashNav
import com.elementary.flashcards.ui.theme.FlashCardsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashCardsTheme {
                FlashNav()
            }
        }
    }
}
