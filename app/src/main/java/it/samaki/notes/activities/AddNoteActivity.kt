package it.samaki.notes.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
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
import it.samaki.notes.R
import it.samaki.notes.database.DatabaseHelper
import it.samaki.notes.models.Category
import it.samaki.notes.models.Note
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Suppress("SpellCheckingInspection")
class AddNoteActivity : AppCompatActivity() {
    private lateinit var photoFile: File
    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private lateinit var category: Category
    private var categories: MutableList<Category> = mutableListOf()
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_note)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        categories.clear()
        categories.addAll(dbHelper.getAllCategories())

        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, categories.map { it.name })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val etTitle = findViewById<EditText>(R.id.et_title)
        val etNote = findViewById<EditText>(R.id.et_note)
        val ivPicture = findViewById<ImageView>(R.id.iv_picture)
        val fabTakePhoto = findViewById<FloatingActionButton>(R.id.fab_take_photo)
        val btnSave = findViewById<ImageButton>(R.id.btn_save)
        val btnCancel = findViewById<ImageButton>(R.id.btn_back)
        val spinnerCategory = findViewById<Spinner>(R.id.spinner_category)

        photoFile = createImageFile()

        lateinit var note: Note
        var isOldNote = false

        spinnerCategory.adapter = adapter

        val addCategoryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val newCategory =
                    result.data!!.getSerializableExtra("it.samaki.notes.category") as Category
                dbHelper.insertCategory(newCategory)
                categories.clear()
                categories.addAll(dbHelper.getAllCategories())
                adapter.clear()
                adapter.addAll(dbHelper.getAllCategories().map { it.name })
                adapter.notifyDataSetChanged()
            }
        }

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == categories.indexOfFirst { it.name == "Add more..." }) {
                    val intent = Intent(this@AddNoteActivity, AddCategoryActivity::class.java)
                    addCategoryLauncher.launch(intent)
                    category = categories[categories.indexOfFirst {
                        it.name == spinnerCategory.getItemAtPosition(categories.size - 1) as String
                    }]
                    return
                }
                category = categories[categories.indexOfFirst {
                    it.name == spinnerCategory.getItemAtPosition(position) as String
                }]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

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
            spinnerCategory.setSelection(categories.indexOfFirst { it.id == note.category.id })

            isOldNote = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        btnCancel.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            val title = etTitle.text.toString()
            val content = etNote.text.toString()
            val category = categories[categories.indexOfFirst {
                it.name == spinnerCategory.getSelectedItem().toString()
            }]

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