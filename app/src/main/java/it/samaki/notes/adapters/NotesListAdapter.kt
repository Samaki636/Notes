package it.samaki.notes.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import it.samaki.notes.NoteClickListener
import it.samaki.notes.R
import it.samaki.notes.models.Note

class NotesListAdapter(
    private val context: Context,
    private val list: List<Note>,
    private val listener: NoteClickListener
) : RecyclerView.Adapter<NotesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_notes, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.tvTitle.text = list[position].title
        holder.tvTitle.isSelected = true

        holder.tvNote.text = list[position].note

        holder.tvDate.text = list[position].date
        holder.tvDate.isSelected = true

        if (list[position].pinned) {
            holder.ivStar.visibility = View.VISIBLE
        } else {
            holder.ivStar.visibility = View.INVISIBLE
        }

        holder.notesContainer.setOnClickListener {
            listener.onClick(list[holder.adapterPosition])
        }

        holder.notesContainer.setOnLongClickListener {
            listener.onLongClick(list[holder.adapterPosition], holder.notesContainer)
            true
        }
    }

}
class NotesViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
        val notesContainer:CardView = itemView.findViewById(R.id.notes_container)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        val tvNote: TextView = itemView.findViewById(R.id.tv_note)
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val ivStar: ImageView = itemView.findViewById(R.id.iv_star)
    }