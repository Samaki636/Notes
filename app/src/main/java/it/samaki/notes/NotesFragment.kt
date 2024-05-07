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
import it.samaki.notes.adapters.NotesListAdapter
import it.samaki.notes.database.RoomDB
import it.samaki.notes.models.Note

class NotesFragment : Fragment(), PopupMenu.OnMenuItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var notesListAdapter: NotesListAdapter
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var database: RoomDB
    private var notes: MutableList<Note> = mutableListOf()
    private lateinit var noteClickStartForResult: ActivityResultLauncher<Intent>
    private lateinit var searchView: SearchView
    private lateinit var selectedNote: Note

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notes, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.fragment_notes)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }

        recyclerView = view.findViewById(R.id.recycler_home)
        fabAdd = view.findViewById(R.id.fab_add_note)
        searchView = view.findViewById(R.id.sv_home)

        database = RoomDB.getInstance(requireContext())
        notes = database.noteDAO().getAll().toMutableList()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        updateRecycler()

        val fabAddStartForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val newNote = result.data?.getSerializableExtra("it.samaki.notes.note")!! as Note
                database.noteDAO().insert(newNote)
                notes.clear()
                notes.addAll(database.noteDAO().getAll())
                notesListAdapter.notifyItemInserted(0)
            } else {
                // Handle canceled or failed result
            }
        }

        noteClickStartForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val newNote = result.data?.getSerializableExtra("it.samaki.notes.note")!! as Note
                database.noteDAO().update(newNote.id, newNote.title, newNote.content, newNote.date)
                notes.clear()
                notes.addAll(database.noteDAO().getAll())
                notesListAdapter.notifyItemChanged(notes.indexOfFirst {
                    it.id == newNote.id
                })
            } else {
                // Handle canceled or failed result
            }
        }

        fabAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddNoteActivity::class.java)
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
        val filteredList: MutableList<Note> = mutableListOf()
        for (note in notes) {
            if (note.title.lowercase().contains(text!!.lowercase()) ||
                note.content.lowercase().contains(text.lowercase()) ||
                note.date.lowercase().contains(text.lowercase())
            ) {
                filteredList.add(note)
            }
            notesListAdapter.updateList(filteredList)
        }
    }

    private fun updateRecycler() {
        recyclerView.setHasFixedSize(true)
        notesListAdapter = NotesListAdapter(requireContext(), notes, noteClickListener)
        recyclerView.adapter = notesListAdapter
    }

    private val noteClickListener = object : NoteClickListener {
        override fun onClick(note: Note) {
            val intent = Intent(requireContext(), AddNoteActivity::class.java)
            intent.putExtra("it.samaki.notes.old_note", note)
            noteClickStartForResult.launch(intent)
        }

        override fun onLongClick(note: Note, cardView: CardView) {
            selectedNote = note
            showPopup(cardView)
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
                if (!selectedNote.starred) {
                    database.noteDAO().star(selectedNote.id, true)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.note_starred), Toast.LENGTH_SHORT
                    ).show()
                } else {
                    database.noteDAO().star(selectedNote.id, false)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.note_unstarred), Toast.LENGTH_SHORT
                    ).show()
                }

                notes.clear()
                notes.addAll(database.noteDAO().getAll())
                notesListAdapter.notifyItemChanged(notes.indexOfFirst {
                    it.id == selectedNote.id
                })
                return true
            }

            R.id.popup_delete -> {
                database.noteDAO().delete(selectedNote)
                val index = notes.indexOfFirst { it.id == selectedNote.id }
                notes.remove(selectedNote)
                notesListAdapter.notifyItemRemoved(index)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.note_deleted), Toast.LENGTH_SHORT
                ).show()
                return true
            }
        }
        return false
    }
}