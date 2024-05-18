package it.samaki.notes.models

import java.io.Serializable

data class Category(
    var id: Int,
    var name: String,
    var color: String
) : Serializable