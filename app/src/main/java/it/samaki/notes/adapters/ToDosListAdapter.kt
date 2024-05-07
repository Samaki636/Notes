package it.samaki.notes.adapters

import android.annotation.SuppressLint
import android.content.Context
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
    private val context: Context,
    private var list: List<ToDo>,
    private val listener: ToDoClickListener
) : RecyclerView.Adapter<ToDosViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDosViewHolder {
        return ToDosViewHolder(
            LayoutInflater.from(context).inflate(R.layout.to_dos_list, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(filteredList: MutableList<ToDo>) {
        list = filteredList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ToDosViewHolder, position: Int) {
        holder.tvContent.text = list[position].content
        holder.tvContent.isSelected = true

        holder.checkBox.isChecked = list[position].completed

        if (list[position].starred) {
            holder.ivStar.visibility = View.VISIBLE
        } else {
            holder.ivStar.visibility = View.INVISIBLE
        }

        holder.toDosContainer.setOnClickListener {
            listener.onClick(list[holder.bindingAdapterPosition])
        }

        holder.toDosContainer.setOnLongClickListener {
            listener.onLongClick(list[holder.bindingAdapterPosition], holder.toDosContainer)
            true
        }

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            listener.onCheck(list[holder.bindingAdapterPosition], isChecked)
        }
    }

}

class ToDosViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    val toDosContainer: CardView = itemView.findViewById(R.id.to_dos_container)
    val tvContent: TextView = itemView.findViewById(R.id.tv_content)
    val ivStar: ImageView = itemView.findViewById(R.id.iv_star)
    val checkBox: CheckBox = itemView.findViewById(R.id.check_box)
}