package it.samaki.notes.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "notes")
class Note : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @ColumnInfo(name = "title")
    var title = ""

    @ColumnInfo(name = "note")
    var content = ""

    @ColumnInfo(name = "date")
    var date = ""

    @ColumnInfo(name = "starred")
    var starred = false
}