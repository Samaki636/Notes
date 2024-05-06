package it.samaki.notes.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "to-dos")
class ToDo : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    @ColumnInfo(name = "content")
    var content = ""

    @ColumnInfo(name = "completed")
    var completed = false

    @ColumnInfo(name = "starred")
    var starred = false
}