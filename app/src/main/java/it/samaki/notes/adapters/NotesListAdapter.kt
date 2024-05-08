package it.samaki.notes.adapters

import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import it.samaki.notes.NoteClickListener
import it.samaki.notes.R

class NotesListAdapter(
    private val cursor: Cursor,
    private val listener: NoteClickListener
) : RecyclerView.Adapter<NotesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            listener,
            LayoutInflater.from(parent.context).inflate(R.layout.notes_list, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return cursor.count
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        cursor.moveToPosition(position)
        holder.tvTitle.text = cursor.getString(1)
        holder.tvNote.text = cursor.getString(2)
        holder.tvDate.text = cursor.getString(3)

        if (cursor.getInt(4) == 1) {
            holder.ivStar.visibility = View.VISIBLE
        } else {
            holder.ivStar.visibility = View.INVISIBLE
        }
    }

}

class NotesViewHolder(listener: NoteClickListener, itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    private val notesContainer: CardView = itemView.findViewById(R.id.notes_container)
    val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
    val tvNote: TextView = itemView.findViewById(R.id.tv_note)
    val tvDate: TextView = itemView.findViewById(R.id.tv_date)
    val ivStar: ImageView = itemView.findViewById(R.id.iv_star)

    init {
        itemView.setOnClickListener {
            listener.onClick(bindingAdapterPosition)
        }

        itemView.setOnLongClickListener {
            listener.onLongClick(bindingAdapterPosition, notesContainer)
            true
        }
    }
}