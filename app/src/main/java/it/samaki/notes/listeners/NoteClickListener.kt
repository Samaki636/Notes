package it.samaki.notes.listeners

import androidx.cardview.widget.CardView

interface NoteClickListener {
    fun onClick(index: Int)
    fun onLongClick(index: Int, cardView: CardView)
    fun onPictureClick(index: Int)
    fun onStarClick(index: Int)
    fun onDeleteClick(index: Int)
}