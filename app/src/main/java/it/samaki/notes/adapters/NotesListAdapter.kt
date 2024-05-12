package it.samaki.notes.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import java.io.File

class NotesListAdapter(
    private val noteList: List<Note>,
    private val listener: NoteClickListener
) : RecyclerView.Adapter<NotesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            listener,
            LayoutInflater.from(parent.context).inflate(R.layout.notes_list, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.tvTitle.text = noteList[position].title
        holder.tvNote.text = noteList[position].content
        holder.tvDate.text = noteList[position].date

        if (noteList[position].picture.isNotEmpty()) {
            holder.ivPicture.visibility = View.VISIBLE
            if (holder.ivPicture.drawable == null) {
                holder.ivPicture.setImageBitmap(noteList[position].picture.let {
                    Bitmap.createScaledBitmap(
                        BitmapFactory.decodeByteArray(
                            File(noteList[position].picture).readBytes(),
                            0,
                            File(noteList[position].picture).readBytes().size
                        ), 70, 70, false
                    )
                })
            }
        } else {
            holder.ivPicture.visibility = View.INVISIBLE
        }

        if (noteList[position].starred) {
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
    val ivPicture: ImageView = itemView.findViewById(R.id.iv_picture)
    private val btnDelete: ImageView = itemView.findViewById(R.id.btn_delete)
    private val btnStar: ImageView = itemView.findViewById(R.id.btn_star)

    init {
        btnStar.setOnClickListener {
            listener.onStarClick(bindingAdapterPosition)
        }

        btnDelete.setOnClickListener {
            listener.onDeleteClick(bindingAdapterPosition)
        }

        itemView.setOnClickListener {
            listener.onClick(bindingAdapterPosition)
        }

        itemView.setOnLongClickListener {
            listener.onLongClick(bindingAdapterPosition, notesContainer)
            true
        }

        ivPicture.setOnClickListener {
            listener.onPictureClick(bindingAdapterPosition)
        }
    }
}