package it.samaki.notes

import androidx.cardview.widget.CardView

interface ToDoClickListener {
    fun onClick(index: Int)
    fun onLongClick(index: Int, cardView: CardView)
    fun onCheck(index: Int, isChecked: Boolean)
}