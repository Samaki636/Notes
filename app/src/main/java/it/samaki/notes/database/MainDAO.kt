package it.samaki.notes.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import it.samaki.notes.models.Note

@Suppress("unused")
@Dao
interface MainDAO {
    @Insert(onConflict = REPLACE)
    fun insert(note: Note)

    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAll(): List<Note>

    @Query("UPDATE notes SET title = :title, note = :note WHERE id = :id")
    fun update(id: Int, title: String, note: String)

    @Delete
    fun delete(note: Note)
}