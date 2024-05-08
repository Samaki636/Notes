package it.samaki.notes.adapters

import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import it.samaki.notes.R
import it.samaki.notes.ToDoClickListener

class ToDosListAdapter(
    private val cursor: Cursor,
    private val listener: ToDoClickListener
) : RecyclerView.Adapter<ToDosViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDosViewHolder {
        return ToDosViewHolder(
            listener,
            LayoutInflater.from(parent.context).inflate(R.layout.to_dos_list, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return cursor.count
    }

    override fun onBindViewHolder(holder: ToDosViewHolder, position: Int) {
        cursor.moveToPosition(position)
        holder.tvContent.text = cursor.getString(1)
        holder.checkBox.isChecked = cursor.getInt(2) == 1

        if (cursor.getInt(3) == 1) {
            holder.ivStar.visibility = View.VISIBLE
        } else {
            holder.ivStar.visibility = View.INVISIBLE
        }
    }
}

class ToDosViewHolder(listener: ToDoClickListener, itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    private val toDosContainer: CardView = itemView.findViewById(R.id.to_dos_container)
    val tvContent: TextView = itemView.findViewById(R.id.tv_content)
    val ivStar: ImageView = itemView.findViewById(R.id.iv_star)
    val checkBox: CheckBox = itemView.findViewById(R.id.check_box)

    init {
        itemView.setOnClickListener {
            listener.onClick(bindingAdapterPosition)
        }

        itemView.setOnLongClickListener {
            listener.onLongClick(bindingAdapterPosition, toDosContainer)
            true
        }

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            listener.onCheck(bindingAdapterPosition, isChecked)
        }
    }
}