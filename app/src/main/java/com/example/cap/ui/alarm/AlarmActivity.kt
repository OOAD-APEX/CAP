package com.example.cap.ui.alarm



import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.cap.R
import com.example.cap.game.dontTouchGame.DontTouchGameDialog
import com.example.cap.game.linkGame.LinkGameDialog

class AlarmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        val startgameBtn = findViewById<Button>(R.id.startgameBtn)
        startgameBtn.setOnClickListener {
            showGameDialog()
        }
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