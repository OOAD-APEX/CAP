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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.example.cap.domain.Alarm
import com.example.cap.domain.Event
import com.example.cap.domain.TriggerMode
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


class EventInputDialog(
    private val current: Event? = null,
    private val context: Context,
    private val viewModel: CalendarViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val mode: InputDialogMode,
) {
    private val nameEditText = AppCompatEditText(context).apply {
        setText(current?.title)
        setSelection(text?.length ?: 0)
        textSize = 18f
    }
    private val timeButton = Button(context).apply {
        text = current?.time?.let { String.format("%02d:%02d", it.hour, it.minute) } ?: "Select Time"
        textSize = 18f
    }
    private var timeSelected = false
    private val alarm = Alarm()
    private val currentEvent: Event
        get() = current!!

    init {
        timeButton.setOnClickListener {
            val currentTime = java.util.Calendar.getInstance()
            var currentHour = currentTime.get(java.util.Calendar.HOUR_OF_DAY)
            var currentMinute = currentTime.get(java.util.Calendar.MINUTE)

            // if time is already selected, set the time picker to the selected time
            if (current != null) {
                currentHour = currentEvent.time.hour
                currentMinute = currentEvent.time.minute
            }
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
                    val toast = Toast.makeText(context, "Fill the name!!!.", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.BOTTOM, 0, 300)
                    toast.show()
                    return@setPositiveButton
                }

                val timeParts = time.split(":").map { it.toInt() }
                val hour = timeParts[0]
                val minute = timeParts[1]
                viewModel.selectedDate.value.let {
                    val dateTime = LocalDateTime.of(it!!.year, it.month, it.dayOfMonth, hour, minute)
                    when (mode) {
                        InputDialogMode.INSERT -> {
                            val idLiveData = viewModel.saveEvent(name, dateTime, TriggerMode.NOTIFICATION)
                            // dateTime to Calendar
                            idLiveData.observe(lifecycleOwner) { id ->
                                val cal = java.util.Calendar.getInstance()
                                cal.timeInMillis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                alarm.setAlarm(context, cal, id.toInt())
                            }
                        }
                        InputDialogMode.UPDATE -> {
                            viewModel.updateEvent(currentEvent.id, name, dateTime, TriggerMode.NOTIFICATION)
                            val cal = java.util.Calendar.getInstance()
                            cal.timeInMillis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                            alarm.updateAlarm(context, cal, currentEvent.id.toInt())
                        }
                    }
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
