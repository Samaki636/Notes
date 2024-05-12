package it.samaki.notes.adapters

import android.graphics.Paint
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
import it.samaki.notes.models.ToDo

class ToDosListAdapter(
    private val toDosList: List<ToDo>,
    private val listener: ToDoClickListener
) : RecyclerView.Adapter<ToDosViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDosViewHolder {
        return ToDosViewHolder(
            listener,
            LayoutInflater.from(parent.context).inflate(R.layout.to_dos_list, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return toDosList.size
    }

    override fun onBindViewHolder(holder: ToDosViewHolder, position: Int) {
        holder.tvContent.text = toDosList[position].content
        holder.checkBox.isChecked = toDosList[position].completed

        if (toDosList[position].starred) {
            holder.ivStar.visibility = View.VISIBLE
        } else {
            holder.ivStar.visibility = View.INVISIBLE
        }

        if (toDosList[position].completed) {
            holder.tvContent.paintFlags = holder.tvContent.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.tvContent.setTextColor(holder.tvContent.context.getColor(R.color.grey800))
        } else {
            holder.tvContent.paintFlags =
                holder.tvContent.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.tvContent.setTextColor(holder.tvContent.context.getColor(R.color.white))
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