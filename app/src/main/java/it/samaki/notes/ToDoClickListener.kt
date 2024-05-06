package it.samaki.notes

import androidx.cardview.widget.CardView
import it.samaki.notes.models.ToDo

interface ToDoClickListener {
    fun onClick(toDo: ToDo)
    fun onLongClick(toDo: ToDo, cardView: CardView)
    fun onCheck(toDo: ToDo, isChecked: Boolean)
}