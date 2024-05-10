package it.samaki.notes

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.samaki.notes.models.Note
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddNoteActivity : AppCompatActivity() {
    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
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
        val ivPicture = findViewById<ImageView>(R.id.iv_picture)
        val fabTakePhoto = findViewById<FloatingActionButton>(R.id.fab_take_photo)
        val bSave = findViewById<ImageButton>(R.id.b_save)
        val bCancel = findViewById<ImageButton>(R.id.b_back)
        lateinit var note: Note
        var isOldNote = false

        takePictureLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val imageBitmap = result.data!!.extras?.get("data") as Bitmap
                ivPicture.setImageBitmap(imageBitmap)
            }
        }

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
            val image = ivPicture.drawable

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, getString(R.string.note_toast_text), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val stream = ByteArrayOutputStream()
            image.toBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream)

            if (!isOldNote) {
                note =
                    Note(0, title, content, formatter.format(Date()), false, stream.toByteArray())
            }
            note.title = title
            note.content = content
            note.date = formatter.format(Date())
            note.image = stream.toByteArray()

            val intent = Intent()
            intent.putExtra("it.samaki.notes.note", note)
            setResult(RESULT_OK, intent)
            finish()
        }

        fabTakePhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    1
                )
            } else {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePictureLauncher.launch(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePictureLauncher.launch(intent)
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.camera_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}