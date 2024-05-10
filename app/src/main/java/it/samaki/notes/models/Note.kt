package it.samaki.notes.models

import java.io.Serializable

data class Note(
    var id: Int,
    var title: String,
    var content: String,
    var date: String,
    var starred: Boolean,
    @Suppress("ArrayInDataClass") var image: ByteArray?
) : Serializable