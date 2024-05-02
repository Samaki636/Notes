package it.samaki.notes

import androidx.cardview.widget.CardView
import it.samaki.notes.models.Note

interface NoteClickListener {
    fun onClick(note: Note)
    fun onLongClick(note: Note, cardView: CardView)
}