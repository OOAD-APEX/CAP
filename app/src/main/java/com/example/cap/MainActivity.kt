package com.example.cap

import android.os.Bundle
import android.widget.Button
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.cap.databinding.ActivityMainBinding
import com.example.cap.game.dontTouchGame.DontTouchGameDialog
import com.example.cap.game.linkGame.LinkGameDialog

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )

        val startGameBotton: Button = findViewById(R.id.startGameButton)
        startGameBotton.setOnClickListener {
            showGameDialog()
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun showGameDialog() {
        val gameDialogs = listOf(
            LinkGameDialog(this),
            DontTouchGameDialog(this)
        )
        val selectedGameDialog = gameDialogs.random()
        selectedGameDialog.show()
    }
}
