package it.samaki.notes.models

import java.io.Serializable

data class Note(
    var id: Int,
    var title: String,
    var content: String,
    var date: String,
    var starred: Boolean,
    var picture: String,
    var category: Category,
) : Serializable