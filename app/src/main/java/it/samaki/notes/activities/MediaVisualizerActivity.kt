package it.samaki.notes.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import it.samaki.notes.R
import java.io.File

class MediaVisualizerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_media_visualizer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imageURI = intent.getStringExtra("it.samaki.notes.picture")
        val ivFullPicture = findViewById<ImageView>(R.id.iv_full_picture)

        ivFullPicture.setImageBitmap(
            imageURI?.let { File(it).readBytes().size }?.let { it ->
                BitmapFactory.decodeByteArray(
                    imageURI.let { File(it).readBytes() },
                    0,
                    it
                )
            }
        )
    }
}