package it.samaki.notes

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.samaki.notes.models.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.Manifest

class AddNoteActivity : AppCompatActivity() {
    private lateinit var ivPicture: ImageView
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
        val bSave = findViewById<Button>(R.id.b_save)
        val bCancel = findViewById<Button>(R.id.b_cancel)
        val fabTakePhoto = findViewById<FloatingActionButton>(R.id.fab_take_photo)
        ivPicture = findViewById(R.id.iv_picture)
        lateinit var note: Note

        bSave.setOnClickListener {
            val title = etTitle.text.toString()
            val content = etNote.text.toString()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_text), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            note = Note()
            note.title = title
            note.content = content
            note.date = formatter.format(Date())

            val intent = Intent()
            intent.putExtra("note", note)
            setResult(RESULT_OK, intent)
            finish()
        }

        fabTakePhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA),
                    1)
            } else {
                // Permission is already granted, proceed with taking the photo
                dispatchTakePictureIntent()
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            // Do something with the image bitmap
            ivPicture.setImageBitmap(imageBitmap)
        }
    }
}