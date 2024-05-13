package it.samaki.notes

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
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
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.samaki.notes.models.Note
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Suppress("SpellCheckingInspection")
class AddNoteActivity : AppCompatActivity() {
    private lateinit var photoFile: File
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
        val etCategory = findViewById<EditText>(R.id.et_category)

        photoFile = createImageFile()

        lateinit var note: Note
        var isOldNote = false

        takePictureLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                ivPicture.setImageBitmap(BitmapFactory.decodeFile(photoFile.absolutePath))
            }
        }

        try {
            note = (intent.getSerializableExtra("it.samaki.notes.old_note") as Note?)!!
            etTitle.setText(note.title)
            etNote.setText(note.content)
            etCategory.setText(note.category)
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
            val category = etCategory.text.toString()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, getString(R.string.note_toast_text), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val image = ivPicture.drawable
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            if (image != null) {
                if (!isOldNote) {
                    note =
                        Note(
                            0,
                            title,
                            content,
                            formatter.format(Date()),
                            false,
                            photoFile.absolutePath,
                            category
                        )
                }
                note.picture = photoFile.absolutePath
            } else {
                if (!isOldNote) {
                    note = Note(0, title, content, formatter.format(Date()), false, "", category)
                }
            }

            note.title = title
            note.content = content
            note.date = formatter.format(Date())
            note.category = category

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
                val photoURI =
                    FileProvider.getUriForFile(this, "it.samaki.notes.fileprovider", photoFile)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
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
                val photoURI =
                    FileProvider.getUriForFile(this, "it.samaki.notes.fileprovider", photoFile)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
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

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss".format(Date()), Locale.getDefault())
        val imageFileName = "PNG_" + timeStamp + "_"
        return File.createTempFile(imageFileName, ".png", cacheDir)
    }
}