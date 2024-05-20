package it.samaki.notes.fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.samaki.notes.R
import it.samaki.notes.activities.AddToDoActivity
import it.samaki.notes.adapters.ToDosListAdapter
import it.samaki.notes.database.DatabaseHelper
import it.samaki.notes.listeners.ToDoClickListener
import it.samaki.notes.models.ToDo

class ToDosFragment : Fragment(), PopupMenu.OnMenuItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var toDosListAdapter: ToDosListAdapter
    private lateinit var fabAdd: FloatingActionButton
    private var toDos: MutableList<ToDo> = mutableListOf()
    private lateinit var editToDoLauncher: ActivityResultLauncher<Intent>
    private lateinit var searchView: SearchView
    private lateinit var selectedToDo: ToDo
    private lateinit var dbHelper: DatabaseHelper

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_to_dos, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.fragment_to_dos)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }

        dbHelper = DatabaseHelper(requireContext())
        toDos.clear()
        toDos.addAll(dbHelper.getAllTodos())

        recyclerView = view.findViewById(R.id.recycler_home)
        fabAdd = view.findViewById(R.id.fab_add_to_do)
        searchView = view.findViewById(R.id.sv_home)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        toDosListAdapter = ToDosListAdapter(toDos, toDoClickListener)
        recyclerView.adapter = toDosListAdapter

        val addToDoLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val newToDo = result.data!!.getSerializableExtra("it.samaki.notes.to_do") as ToDo
                dbHelper.insertTodo(newToDo)
                toDos.clear()
                toDos.addAll(dbHelper.getAllTodos())
                toDosListAdapter.notifyDataSetChanged()
                recyclerView.smoothScrollToPosition(0)
            }
        }

        editToDoLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val editedToDo = result.data!!.getSerializableExtra("it.samaki.notes.to_do") as ToDo
                dbHelper.updateTodo(editedToDo)
                toDos.clear()
                toDos.addAll(dbHelper.getAllTodos())
                toDosListAdapter.notifyDataSetChanged()
            }
        }

        fabAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddToDoActivity::class.java)
            addToDoLauncher.launch(intent)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return true
            }
        })

        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filter(text: String?) {
        toDos.clear()
        toDos.addAll(dbHelper.getAllTodos())
        val filteredList: MutableList<ToDo> = mutableListOf()
        for (toDo in toDos) {
            if (toDo.content.lowercase().contains(text!!.lowercase())
            ) {
                filteredList.add(toDo)
            }
        }
        toDos.clear()
        toDos.addAll(filteredList)
        toDosListAdapter.notifyDataSetChanged()
    }

    private val toDoClickListener = object : ToDoClickListener {
        override fun onClick(index: Int) {
            val intent = Intent(requireContext(), AddToDoActivity::class.java)
            intent.putExtra("it.samaki.notes.old_to_do", toDos[index])
            editToDoLauncher.launch(intent)
        }

        override fun onLongClick(index: Int, cardView: CardView) {
            selectedToDo = toDos[index]
            showPopup(cardView)
        }

        override fun onCheck(index: Int, isChecked: Boolean) {
            toDos[index].completed = isChecked
            dbHelper.updateTodo(toDos[index])
            recyclerView.post { toDosListAdapter.notifyItemChanged(index) }
        }
    }

    private fun showPopup(cardView: CardView) {
        val popupMenu = PopupMenu(requireContext(), cardView)
        popupMenu.setOnMenuItemClickListener(this)
        popupMenu.inflate(R.menu.menu_popup)
        popupMenu.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.popup_star_unstar -> {
                if (!selectedToDo.starred) {
                    selectedToDo.starred = true
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.to_do_starred), Toast.LENGTH_SHORT
                    ).show()
                } else {
                    selectedToDo.starred = false
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.to_do_unstarred), Toast.LENGTH_SHORT
                    ).show()
                }
                dbHelper.updateTodo(selectedToDo)
                toDos.clear()
                toDos.addAll(dbHelper.getAllTodos())
                toDosListAdapter.notifyDataSetChanged()
                return true
            }

            R.id.popup_delete -> {
                dbHelper.deleteTodo(selectedToDo)
                val index = toDos.indexOfFirst { it.id == selectedToDo.id }
                toDos.remove(selectedToDo)
                toDosListAdapter.notifyItemRemoved(index)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.to_do_deleted), Toast.LENGTH_SHORT
                ).show()
                return true
            }
        }
        return false
    }
}