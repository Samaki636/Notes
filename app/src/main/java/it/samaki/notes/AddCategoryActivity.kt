package it.samaki.notes

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import it.samaki.notes.models.Category

class AddCategoryActivity : AppCompatActivity() {
    private lateinit var btnCancel: ImageButton
    private lateinit var btnSave: ImageButton
    private lateinit var etName: EditText
    private lateinit var etColor: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_category)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnCancel = findViewById(R.id.btn_back)
        btnSave = findViewById(R.id.btn_save)
        etName = findViewById(R.id.et_category_name)
        etColor = findViewById(R.id.et_category_color)

        btnCancel.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            val intent = Intent()
            intent.putExtra(
                "it.samaki.notes.category",
                Category(
                    0,
                    etName.text.toString(),
                    etColor.text.toString()
                )
            )
            setResult(RESULT_OK, intent)

            finish()
        }
    }
}