package it.samaki.notes.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import it.samaki.notes.models.ToDo

@Dao
interface ToDoDAO {
    @Insert(onConflict = REPLACE)
    fun insert(toDo: ToDo)

    @Query("SELECT * FROM `to-dos` ORDER BY id DESC")
    fun getAll(): List<ToDo>

    @Query("UPDATE `to-dos` SET content = :content WHERE id = :id")
    fun update(id: Int, content: String)

    @Delete
    fun delete(toDo: ToDo)

    @Query("UPDATE `to-dos` SET completed = :completed WHERE id = :id")
    fun complete(id: Int, completed: Boolean)

    @Query("UPDATE `to-dos` SET starred = :starred WHERE id = :id")
    fun star(id: Int, starred: Boolean)
}