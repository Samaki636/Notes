package it.samaki.notes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import it.samaki.notes.models.Note

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class RoomDB : RoomDatabase() {
    companion object {
        @Volatile
        private var instance: RoomDB? = null
        private const val DATABASE_NAME = "NoteApp"

        @Synchronized
        fun getInstance(context: Context): RoomDB {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomDB::class.java,
                    DATABASE_NAME
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance!!
        }
    }

    abstract fun mainDAO(): MainDAO
}
