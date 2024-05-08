package it.samaki.notes.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import it.samaki.notes.models.Note
import it.samaki.notes.models.ToDo

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "notes.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE notes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title TEXT, " +
                    "content TEXT, " +
                    "date TEXT, " +
                    "starred INTEGER)"
        )
        db.execSQL(
            "CREATE TABLE todos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "content TEXT, " +
                    "completed INTEGER, " +
                    "starred INTEGER)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS notes")
        db.execSQL("DROP TABLE IF EXISTS todos")
        onCreate(db)
    }

    fun insertNote(note: Note) {
        val values = ContentValues().apply {
            put("title", note.title)
            put("content", note.content)
            put("date", note.date)
            put("starred", note.starred)
        }
        writableDatabase.insert("notes", null, values)
    }

    fun updateNote(note: Note) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", note.title)
            put("content", note.content)
            put("date", note.date)
            put("starred", note.starred)
        }
        db.update(
            "notes",
            values,
            "id = ?",
            arrayOf(note.id.toString())
        )
    }

    fun deleteNote(note: Note) {
        val db = writableDatabase
        db.delete(
            "notes",
            "id = ?",
            arrayOf(note.id.toString())
        )
    }

    fun getNotesCursor(): Cursor {
        val db = writableDatabase
        val cursor = db.query("notes", null, null, null, null, null, null)
        return cursor
    }

    fun getToDosCursor(): Cursor {
        val db = writableDatabase
        val cursor = db.query("todos", null, null, null, null, null, null)
        return cursor
    }

    fun getAllNotes(): List<Note> {
        val notes = mutableListOf<Note>()
        val cursor = readableDatabase.query("notes", null, null, null, null, null, null)
        while (cursor.moveToNext()) {
            notes.add(
                Note(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4).toBoolean()
                )
            )
        }
        cursor.close()
        return notes
    }

    fun insertTodo(toDo: ToDo) {
        val values = ContentValues().apply {
            put("content", toDo.content)
            put("completed", toDo.completed)
            put("starred", toDo.starred)
        }
        writableDatabase.insert("todos", null, values)
    }

    fun updateTodo(toDo: ToDo) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("content", toDo.content)
            put("completed", toDo.completed)
            put("starred", toDo.starred)
        }
        db.update(
            "todos",
            values,
            "id = ?",
            arrayOf(toDo.id.toString())
        )
    }

    fun deleteTodo(toDo: ToDo) {
        val db = writableDatabase
        db.delete(
            "todos",
            "id = ?",
            arrayOf(toDo.id.toString())
        )
    }

    fun getAllTodos(): List<ToDo> {
        val todos = mutableListOf<ToDo>()
        val cursor = readableDatabase.query("todos", null, null, null, null, null, null)
        while (cursor.moveToNext()) {
            todos.add(
                ToDo(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2).toBoolean(),
                    cursor.getInt(3).toBoolean()
                )
            )
        }
        cursor.close()
        return todos
    }

    private fun Int.toBoolean() = this == 1
}
