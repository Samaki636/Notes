package it.samaki.notes

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import it.samaki.notes.models.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddNoteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_note)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etTitle = findViewById<EditText>(R.id.et_title)
        val etNote = findViewById<EditText>(R.id.et_note)
        val bSave = findViewById<ImageButton>(R.id.b_save)
        val bCancel = findViewById<ImageButton>(R.id.b_back)
        lateinit var note: Note
        var isOldNote = false

        try {
            note = (intent.getSerializableExtra("it.samaki.notes.old_note") as Note?)!!
            etTitle.setText(note.title)
            etNote.setText(note.content)
            isOldNote = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        bCancel.setOnClickListener {
            finish()
        }

        bSave.setOnClickListener {
            val title = etTitle.text.toString()
            val content = etNote.text.toString()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, getString(R.string.note_toast_text), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val formatter = SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.getDefault())

            if (!isOldNote) {
                note = Note(0, title, content, formatter.format(Date()), false)
            }
            note.title = title
            note.content = content
            note.date = formatter.format(Date())

            val intent = Intent()
            intent.putExtra("it.samaki.notes.note", note)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}