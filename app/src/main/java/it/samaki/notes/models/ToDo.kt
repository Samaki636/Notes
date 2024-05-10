package it.samaki.notes.models

import java.io.Serializable

data class ToDo(
    var id: Int,
    var content: String,
    var completed: Boolean,
    var date: String,
    var starred: Boolean
) : Serializable