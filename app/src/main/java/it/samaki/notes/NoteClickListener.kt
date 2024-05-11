package it.samaki.notes

import androidx.cardview.widget.CardView

interface NoteClickListener {
    fun onClick(index: Int)
    fun onLongClick(index: Int, cardView: CardView)
    fun onPictureClick(index: Int)
}