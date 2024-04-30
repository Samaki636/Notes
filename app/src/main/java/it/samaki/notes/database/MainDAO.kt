package it.samaki.notes.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import it.samaki.notes.models.Notes

@Suppress("unused")
@Dao
interface MainDAO {
    @Insert(onConflict = REPLACE)
    fun insert(notes: Notes)

    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAll(): List<Notes>

    @Query("UPDATE notes SET title = :title, notes = :notes WHERE id = :id")
    fun update(id: Int, title: String, notes: String)

    @Delete
    fun delete(notes: Notes)
}