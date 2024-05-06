package it.samaki.notes.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import it.samaki.notes.models.Note

@Dao
interface NoteDAO {
    @Insert(onConflict = REPLACE)
    fun insert(note: Note)

    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAll(): List<Note>

    @Query("UPDATE notes SET title = :title, note = :note, date = :date WHERE id = :id")
    fun update(id: Int, title: String, note: String, date: String)

    @Delete
    fun delete(note: Note)

    @Query("UPDATE notes SET starred = :starred WHERE id = :id")
    fun star(id: Int, starred: Boolean)
}