package it.samaki.notes

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.samaki.notes.adapters.NotesListAdapter
import it.samaki.notes.database.RoomDB
import it.samaki.notes.models.Note

class NotesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var notesListAdapter: NotesListAdapter
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var database: RoomDB
    private lateinit var notes: List<Note>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notes, container, false)

        recyclerView = view.findViewById(R.id.recycler_home)
        fabAdd = view.findViewById(R.id.fab_add_note)
        database = RoomDB.getInstance(requireContext())
        notes = database.mainDAO().getAll()

        updateRecycler(notes)

        fabAdd.setOnClickListener {
            fun onClick(view: View) {
                val intent = Intent(context, AddNoteActivity::class.java)
                val startForResult = registerForActivityResult(
                    ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == RESULT_OK) {
                        // Handle successful result
                    } else {
                        // Handle canceled or failed result
                    }
                }
                startForResult.launch(intent)            }
        }

        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    private fun updateRecycler(notes: List<Note>) {
        recyclerView.setHasFixedSize(true)
        notesListAdapter = NotesListAdapter(requireContext(), notes, noteClickListener)
        recyclerView.adapter = notesListAdapter
    }

    private val noteClickListener = object : NoteClickListener {
        override fun onClick(note: Note) {

        }

        override fun onLongClick(note: Note, cardView: CardView) {

        }
    }
}