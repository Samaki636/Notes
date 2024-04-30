package it.samaki.notes.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Suppress("unused")
@Entity(tableName = "notes")
class Notes {
    @PrimaryKey(autoGenerate = true)
    val id = 0

    @ColumnInfo(name = "title")
    var title = ""

    @ColumnInfo(name = "notes")
    var notes = ""

    @ColumnInfo(name = "date")
    var date = ""

    @ColumnInfo(name = "pinned")
    var pinned = false
}