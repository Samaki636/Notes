package it.samaki.notes.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
class Note {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @ColumnInfo(name = "title")
    var title = ""

    @ColumnInfo(name = "note")
    var note = ""

    @ColumnInfo(name = "date")
    var date = ""

    @ColumnInfo(name = "pinned")
    var pinned = false
}