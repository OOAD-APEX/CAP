package com.example.cap.ui.alarm



import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.cap.R
import com.example.cap.game.GameDialog
import com.example.cap.game.GameDialogObserver
import com.example.cap.ui.fortune.DailyFortuneDialog

class AlarmActivity : AppCompatActivity(), GameDialogObserver {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        val startgameBtn = findViewById<Button>(R.id.startgameBtn)
        startgameBtn.setOnClickListener {
            showGameDialog()
        }
    }

    private fun showGameDialog() {
        val gameDialog = GameDialog(this)
        gameDialog.addObserver(this)
        gameDialog.show()
    }

    override fun onGameComplete() {
        AlarmFragment().cancelAlarm(this)
        val dialog = DailyFortuneDialog(this)
        dialog.show()
        // close the activity
        dialog.setOnDismissListener {
            finish()
        }
    }

}