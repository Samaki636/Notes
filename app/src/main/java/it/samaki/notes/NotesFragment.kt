package it.samaki.notes

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
import it.samaki.notes.adapters.NotesListAdapter
import it.samaki.notes.database.DatabaseHelper
import it.samaki.notes.models.Note

class NotesFragment : Fragment(), PopupMenu.OnMenuItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var notesListAdapter: NotesListAdapter
    private lateinit var fabAdd: FloatingActionButton
    private var notes: MutableList<Note> = mutableListOf()
    private lateinit var editNoteLauncher: ActivityResultLauncher<Intent>
    private lateinit var searchView: SearchView
    private lateinit var selectedNote: Note
    private lateinit var dbHelper: DatabaseHelper

    @SuppressLint("NotifyDataSetChanged")
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

        dbHelper = DatabaseHelper(requireContext())
        notes.clear()
        notes.addAll(dbHelper.getAllNotes())

        recyclerView = view.findViewById(R.id.recycler_home)
        fabAdd = view.findViewById(R.id.fab_add_note)
        searchView = view.findViewById(R.id.sv_home)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        notesListAdapter = NotesListAdapter(notes, noteClickListener)
        recyclerView.adapter = notesListAdapter

        val addNoteLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val newNote = result.data!!.getSerializableExtra("it.samaki.notes.note") as Note
                dbHelper.insertNote(newNote)
                notes.clear()
                notes.addAll(dbHelper.getAllNotes())
                notesListAdapter.notifyDataSetChanged()
                recyclerView.smoothScrollToPosition(0)
            }
        }

        editNoteLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val editedNote = result.data!!.getSerializableExtra("it.samaki.notes.note") as Note
                dbHelper.updateNote(editedNote)
                notes.clear()
                notes.addAll(dbHelper.getAllNotes())
                notesListAdapter.notifyDataSetChanged()
            }
        }

        fabAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddNoteActivity::class.java)
            addNoteLauncher.launch(intent)
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
        notes.clear()
        notes.addAll(dbHelper.getAllNotes())
        val filteredList: MutableList<Note> = mutableListOf()
        for (note in notes) {
            if (note.title.lowercase().contains(text!!.lowercase()) ||
                note.content.lowercase().contains(text.lowercase()) ||
                note.date.lowercase().contains(text.lowercase())
            ) {
                filteredList.add(note)
            }
        }
        notes.clear()
        notes.addAll(filteredList)
        notesListAdapter.notifyDataSetChanged()
    }

    private val noteClickListener = object : NoteClickListener {
        override fun onClick(index: Int) {
            val intent = Intent(requireContext(), AddNoteActivity::class.java)
            intent.putExtra("it.samaki.notes.old_note", notes[index])
            editNoteLauncher.launch(intent)
        }

        override fun onLongClick(index: Int, cardView: CardView) {
            selectedNote = notes[index]
            showPopup(cardView)
        }

        override fun onPictureClick(index: Int) {
            val intent = Intent(requireContext(), MediaVisualizerActivity::class.java)
            intent.putExtra("it.samaki.notes.picture", notes[index].picture)
            startActivity(intent)
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
                if (!selectedNote.starred) {
                    selectedNote.starred = true
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.note_starred), Toast.LENGTH_SHORT
                    ).show()
                } else {
                    selectedNote.starred = false
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.note_unstarred), Toast.LENGTH_SHORT
                    ).show()
                }
                dbHelper.updateNote(selectedNote)
                notes.clear()
                notes.addAll(dbHelper.getAllNotes())
                notesListAdapter.notifyDataSetChanged()
                return true
            }

            R.id.popup_delete -> {
                dbHelper.deleteNote(selectedNote)
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