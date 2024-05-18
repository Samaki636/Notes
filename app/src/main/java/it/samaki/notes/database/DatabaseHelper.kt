package it.samaki.notes.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import it.samaki.notes.R
import it.samaki.notes.models.Category
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
                    "starred INTEGER, " +
                    "image TEXT, " +
                    "category TEXT)"
        )

        db.execSQL(
            "CREATE TABLE todos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "content TEXT, " +
                    "completed INTEGER, " +
                    "date TEXT, " +
                    "starred INTEGER)"
        )

        db.execSQL(
            "CREATE TABLE categories (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "color TEXT)"
        )

        var values = ContentValues().apply {
            put("name", "Category")
            put("color", "#424242")
        }
        db.insert("categories", null, values)

        values = ContentValues().apply {
            put("name", "Shopping")
            put("color", "#430F43")
        }
        db.insert("categories", null, values)

        values = ContentValues().apply {
            put("name", "Travel")
            put("color", "#43430F")
        }
        db.insert("categories", null, values)

        values = ContentValues().apply {
            put("name", "Personal")
            put("color", "#0D0F43")
        }
        db.insert("categories", null, values)

        values = ContentValues().apply {
            put("name", "Family")
            put("color", "#0F430D")
        }
        db.insert("categories", null, values)

        values = ContentValues().apply {
            put("name", "School")
            put("color", "#4A3712")
        }
        db.insert("categories", null, values)

        values = ContentValues().apply {
            put("name", "Work")
            put("color", "#430F0F")
        }
        db.insert("categories", null, values)

        values = ContentValues().apply {
            put("name", "Add more...")
            put("color", "#424242")
        }
        db.insert("categories", null, values)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS notes")
        db.execSQL("DROP TABLE IF EXISTS todos")
        db.execSQL("DROP TABLE IF EXISTS categories")
        onCreate(db)
    }

    fun insertNote(note: Note) {
        val values = ContentValues().apply {
            put("title", note.title)
            put("content", note.content)
            put("date", note.date)
            put("starred", note.starred)
            put("image", note.picture)
            put("category", note.category.id)
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
            put("image", note.picture)
            put("category", note.category.id)
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

    fun getAllNotes(): List<Note> {
        val notes = mutableListOf<Note>()
        val cursor = readableDatabase.query(
            "notes n JOIN categories c ON n.category = c.id",
            null,
            null,
            null,
            null,
            null,
            "starred DESC, datetime(date) DESC"
        )
        while (cursor.moveToNext()) {
            if (cursor.getBlob(5) != null) {
                notes.add(
                    Note(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4).toBoolean(),
                        cursor.getString(5),
                        Category(
                            cursor.getInt(cursor.getColumnIndexOrThrow("category")),
                            cursor.getString(cursor.getColumnIndexOrThrow("name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("color"))
                        )
                    )
                )
            } else {
                notes.add(
                    Note(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4).toBoolean(),
                        "",
                        Category(
                            cursor.getInt(cursor.getColumnIndexOrThrow("category")),
                            cursor.getString(cursor.getColumnIndexOrThrow("name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("color"))
                        )
                    )
                )
            }
        }
        cursor.close()
        return notes
    }

    fun insertTodo(toDo: ToDo) {
        val values = ContentValues().apply {
            put("content", toDo.content)
            put("completed", toDo.completed)
            put("date", toDo.date)
            put("starred", toDo.starred)
        }
        writableDatabase.insert("todos", null, values)
    }

    fun updateTodo(toDo: ToDo) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("content", toDo.content)
            put("completed", toDo.completed)
            put("date", toDo.date)
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
        val cursor = readableDatabase.query(
            "todos",
            null,
            null,
            null,
            null,
            null,
            "starred DESC, datetime(date) DESC"
        )
        while (cursor.moveToNext()) {
            todos.add(
                ToDo(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2).toBoolean(),
                    cursor.getString(3),
                    cursor.getInt(4).toBoolean()
                )
            )
        }
        cursor.close()
        return todos
    }

    fun insertCategory(category: Category) {
        val values = ContentValues().apply {
            put("name", category.name)
            put("color", category.color)
        }
        writableDatabase.insert("categories", null, values)
    }

    fun updateCategory(category: Category) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", category.name)
            put("color", category.color)
        }
        db.update(
            "categories",
            values,
            "id = ?",
            arrayOf(category.id.toString())
        )
    }

    fun deleteCategory(category: Category) {
        val db = writableDatabase
        db.delete(
            "categories",
            "id = ?",
            arrayOf(category.id.toString())
        )
    }

    fun getAllCategories(): List<Category> {
        val categories = mutableListOf<Category>()
        val cursor = readableDatabase.query(
            "categories",
            null,
            null,
            null,
            null,
            null,
            "id ASC"
        )
        while (cursor.moveToNext()) {
            categories.add(
                Category(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2)
                )
            )
        }
        cursor.close()
        return categories
    }

    private fun Int.toBoolean() = this == 1
}
