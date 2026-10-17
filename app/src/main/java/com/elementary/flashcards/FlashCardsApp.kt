package com.elementary.flashcards

import android.app.Application
import android.content.Context
import com.elementary.flashcards.data.SettingsRepository
import com.elementary.flashcards.data.StatsRepository
import com.elementary.flashcards.db.AppDatabase
import com.elementary.flashcards.db.RoomStatsStore

class FlashCardsApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val database = AppDatabase.create(appContext)
    val stats = StatsRepository(RoomStatsStore(database))
    val settings = SettingsRepository(appContext)
}

fun Context.flashCardsApp(): FlashCardsApp = applicationContext as FlashCardsApp
