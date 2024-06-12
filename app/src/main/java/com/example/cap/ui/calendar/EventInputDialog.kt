package com.example.cap.ui.calendar

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import com.example.cap.domain.Event
import com.example.cap.domain.TriggerMode
import java.time.LocalDateTime


class EventInputDialog(
    private val current: Event,
    private val context: Context,
    private val viewModel: CalendarViewModel,
    private val mode: InputDialogMode,
) {
    private val nameEditText = AppCompatEditText(context)
    private val timeButton = Button(context).apply {
        text = "Select Time"
        textSize = 18f
    }
    private var timeSelected = false

    init {
        timeButton.setOnClickListener {
            val currentTime = java.util.Calendar.getInstance()
            val currentHour = currentTime.get(java.util.Calendar.HOUR_OF_DAY)
            val currentMinute = currentTime.get(java.util.Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(context, { _, hourOfDay, minute ->
                timeButton.text = String.format("%02d:%02d", hourOfDay, minute)
                timeSelected = true
            }, currentHour, currentMinute, true)

            timePickerDialog.show()
        }
    }

    fun create(): AlertDialog {
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            val padding = 75
            setPadding(padding, padding, padding, padding)
            addView(nameEditText, FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
            addView(timeButton, FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        }
        return AlertDialog.Builder(context)
            .setTitle("Add Event")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val time = timeButton.text.toString()
                if (!timeSelected) {
                    val toast = Toast.makeText(context, "Select a time!!!.", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.BOTTOM, 0, 300)
                    toast.show()
                    return@setPositiveButton
                }

                val name = nameEditText.text.toString()
                if (name.isEmpty()) {
                    val toast = Toast.makeText(context, "Fill name!!!.", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.BOTTOM, 0, 300)
                    toast.show()
                    return@setPositiveButton
                }

                val timeParts = time.split(":").map { it.toInt() }
                val hour = timeParts[0]
                val minute = timeParts[1]
                viewModel.selectedDate.value.let {
                    val dateTime = LocalDateTime.of(it!!.year, it.month, it.dayOfMonth, hour, minute)
//                    when (mode) {
//                        InputDialogMode.INSERT -> viewModel.saveEvent(name, dateTime, TriggerMode.NOTIFICATION)
//                        InputDialogMode.UPDATE -> viewModel.updateEvent(name, dateTime, TriggerMode.NOTIFICATION)
//                    }
                    viewModel.updateEvent(current.id, name, dateTime, TriggerMode.NOTIFICATION)
                }
                nameEditText.setText("")
            }
            .setNegativeButton("Cancel", null)
            .create()
            .apply {
                setOnShowListener {
                    nameEditText.requestFocus()
                    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                }
                setOnDismissListener {
                    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                }
            }
    }
}
