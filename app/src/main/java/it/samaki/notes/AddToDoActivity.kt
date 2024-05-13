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
import it.samaki.notes.models.ToDo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddToDoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_to_do)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etContent = findViewById<EditText>(R.id.et_content)
        val bSave = findViewById<ImageButton>(R.id.btn_save)
        val bCancel = findViewById<ImageButton>(R.id.btn_back)
        lateinit var toDo: ToDo
        var isOldToDo = false

        try {
            toDo = (intent.getSerializableExtra("it.samaki.notes.old_to_do") as ToDo?)!!
            etContent.setText(toDo.content)
            isOldToDo = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        bCancel.setOnClickListener {
            finish()
        }

        bSave.setOnClickListener {
            val content = etContent.text.toString()

            if (content.isEmpty()) {
                Toast.makeText(this, getString(R.string.to_do_toast_text), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            if (!isOldToDo) {
                toDo =
                    ToDo(0, content, completed = false, formatter.format(Date()), starred = false)
            }
            toDo.content = content
            toDo.date = formatter.format(Date())

            val intent = Intent()
            intent.putExtra("it.samaki.notes.to_do", toDo)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}