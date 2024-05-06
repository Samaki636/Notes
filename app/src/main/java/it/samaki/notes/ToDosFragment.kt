package it.samaki.notes

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
import it.samaki.notes.adapters.ToDosListAdapter
import it.samaki.notes.database.RoomDB
import it.samaki.notes.models.ToDo

class ToDosFragment : Fragment(), PopupMenu.OnMenuItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var toDosListAdapter: ToDosListAdapter
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var database: RoomDB
    private var toDos: MutableList<ToDo> = mutableListOf()
    private lateinit var toDosClickStartForResult: ActivityResultLauncher<Intent>
    private lateinit var searchView: SearchView
    private lateinit var selectedToDo: ToDo

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

        recyclerView = view.findViewById(R.id.recycler_home)
        fabAdd = view.findViewById(R.id.fab_add_to_do)
        searchView = view.findViewById(R.id.sv_home)

        database = RoomDB.getInstance(requireContext())
        toDos = database.toDoDAO().getAll().toMutableList()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        updateRecycler()

        val fabAddStartForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val newToDo = result.data?.getSerializableExtra("it.samaki.notes.to_do")!! as ToDo
                database.toDoDAO().insert(newToDo)
                toDos.clear()
                toDos.addAll(database.toDoDAO().getAll())
                toDosListAdapter.notifyDataSetChanged()
            } else {
                // Handle canceled or failed result
            }
        }

        toDosClickStartForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val newToDo = result.data?.getSerializableExtra("it.samaki.notes.to_do")!! as ToDo
                database.toDoDAO().update(newToDo.id, newToDo.content)
                toDos.clear()
                toDos.addAll(database.toDoDAO().getAll())
                toDosListAdapter.notifyDataSetChanged()
            } else {
                // Handle canceled or failed result
            }
        }

        fabAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddToDoActivity::class.java)
            fabAddStartForResult.launch(intent)
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

    private fun filter(text: String?) {
        val filteredList: MutableList<ToDo> = mutableListOf()
        for (toDo in toDos) {
            if (toDo.content.lowercase().contains(text!!.lowercase())
            ) {
                filteredList.add(toDo)
            }
            toDosListAdapter.updateList(filteredList)
        }
    }

    private fun updateRecycler() {
        recyclerView.setHasFixedSize(true)
        toDosListAdapter = ToDosListAdapter(requireContext(), toDos, toDoClickListener)
        recyclerView.adapter = toDosListAdapter
    }

    private val toDoClickListener = object : ToDoClickListener {
        override fun onClick(toDo: ToDo) {
            val intent = Intent(requireContext(), AddToDoActivity::class.java)
            intent.putExtra("it.samaki.notes.old_to_do", toDo)
            toDosClickStartForResult.launch(intent)
        }

        override fun onLongClick(toDo: ToDo, cardView: CardView) {
            selectedToDo = toDo
            showPopup(cardView)
        }

        override fun onCheck(toDo: ToDo, isChecked: Boolean) {
            database.toDoDAO().complete(toDo.id, isChecked)
            toDos.clear()
            toDos.addAll(database.toDoDAO().getAll())
            recyclerView.post {
                toDosListAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun showPopup(cardView: CardView) {
        val popupMenu = PopupMenu(requireContext(), cardView)
        popupMenu.setOnMenuItemClickListener(this)
        popupMenu.inflate(R.menu.menu_popup)
        popupMenu.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.popup_star_unstar -> {
                if (!selectedToDo.starred) {
                    database.toDoDAO().star(selectedToDo.id, true)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.to_do_starred), Toast.LENGTH_SHORT
                    ).show()
                } else {
                    database.toDoDAO().star(selectedToDo.id, false)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.to_do_unstarred), Toast.LENGTH_SHORT
                    ).show()
                }

                toDos.clear()
                toDos.addAll(database.toDoDAO().getAll())
                toDosListAdapter.notifyDataSetChanged()
                return true
            }

            R.id.popup_delete -> {
                database.toDoDAO().delete(selectedToDo)
                toDos.remove(selectedToDo)
                toDosListAdapter.notifyDataSetChanged()
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