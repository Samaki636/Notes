package it.samaki.notes.activities

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView
import it.samaki.notes.R
import it.samaki.notes.fragments.NotesFragment
import it.samaki.notes.fragments.ToDosFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val notesFragment = NotesFragment()
        val toDosFragment = ToDosFragment()
        setCurrentFragment(notesFragment)

        if (resources.configuration.isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_LARGE)) {
            val railBar = findViewById<NavigationRailView>(R.id.navigationRail)

            railBar.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.navigation_notes -> setCurrentFragment(notesFragment)
                    R.id.navigation_to_dos -> setCurrentFragment(toDosFragment)
                }
                true
            }
        } else {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(0, systemBars.top, 0, 0)
                insets
            }

            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

            bottomNavigationView.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.navigation_notes -> setCurrentFragment(notesFragment)
                    R.id.navigation_to_dos -> setCurrentFragment(toDosFragment)
                }
                true
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
}