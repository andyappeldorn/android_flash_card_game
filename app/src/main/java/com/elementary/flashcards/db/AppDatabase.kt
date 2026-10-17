package com.elementary.flashcards.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CardStatEntity::class, SessionEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardStats(): CardStatDao
    abstract fun sessions(): SessionDao

    companion object {
        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "flash-cards.db")
                .build()
        }
    }
}
