package com.example.cap.ui.calendar

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.cap.R
import com.example.cap.databinding.CalendarDayLayoutBinding
import com.example.cap.databinding.CalendarFragmentBinding
import com.example.cap.databinding.CalendarHeaderBinding
import com.example.cap.domain.TriggerMode
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.util.*

class CalendarFragment : Fragment(R.layout.calendar_fragment) {

    private lateinit var binding: CalendarFragmentBinding
    private val calendarView: CalendarView get() = binding.calendarView
    private val viewModel: CalendarViewModel by viewModels()

    private val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.SUNDAY)
    private var selectedDate: LocalDate? = null

    private val inputDialog by lazy {
        val nameEditText = AppCompatEditText(requireContext())
        val timeButton = Button(requireContext()).apply {
            text = "Select Time"
            textSize = 18f
        }

        timeButton.setOnClickListener {
            val currentTime = Calendar.getInstance()
            val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
            val currentMinute = currentTime.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                // This is called when the user clicks "OK" in the TimePickerDialog.
                timeButton.text = String.format("%02d:%02d", hourOfDay, minute)
            }, currentHour, currentMinute, true) // Initial time set to 12:00. Change as needed.

            timePickerDialog.show()
        }

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            val padding = 75
            setPadding(padding, padding, padding, padding)
            addView(nameEditText, FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
            addView(timeButton, FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Add Event")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val name = nameEditText.text.toString()
                val time = timeButton.text.toString()
                val timeParts = time.split(":").map { it.toInt() }
                val hour = timeParts[0]
                val minute = timeParts[1]
                selectedDate?.let {
                    val dateTime = LocalDateTime.of(it.year, it.month, it.dayOfMonth, hour, minute)
                    viewModel.saveEvent(name, dateTime, TriggerMode.NOTIFICATION)
                }
                // tear down the dialog
                nameEditText.setText("")
            }
            .setNegativeButton("Cancel", null)
            .create()
            .apply {
                setOnShowListener {
                    // Show the keyboard
                    nameEditText.requestFocus()
                    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                }
                setOnDismissListener {
                    // Hide the keyboard
                    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                }
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // view binding
        binding = CalendarFragmentBinding.bind(view)

        setupCalendar()

        // Show today's events initially.
        if (savedInstanceState == null) {
            binding.calendarView.post { selectDate(LocalDate.now()) }
        }

        binding.addEventButton.setOnClickListener {
            inputDialog.show()
        }

        // observe the title of the calendar
        viewModel.title.observe(viewLifecycleOwner) {
            // TODO: make it look nicer
            binding.textView.text = it
        }
    }

    private fun setupCalendar() {
        setupCalendarDayBinder()
        setupCalendarMonthBinder()
        setupMonthChangeListener()
        // TODO: setup dateOnclickListener

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)  // Adjust as needed
        val endMonth = currentMonth.plusMonths(100)  // Adjust as needed
        calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        calendarView.scrollToMonth(currentMonth)
    }

    private fun setupMonthChangeListener() {
        binding.calendarView.monthScrollListener = {
            viewModel.setMonthTitle(it.yearMonth)
        }
    }

    private fun setupCalendarDayBinder() {
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val textView = CalendarDayLayoutBinding.bind(view).calendarDayText

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        selectDate(day.date)
                    }
                }
            }
        }

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                val textView = container.textView

                if (data.position == DayPosition.MonthDate) {
                    textView.text = data.date.dayOfMonth.toString()
                    if (data.date == selectedDate) {
                        textView.setBackgroundResource(R.drawable.selected_day)
                    } else {
                        container.textView.background = null
                    }
                }
                else {
                    container.textView.text = null
                }
                // TODO: add selected date's background color
            }
        }
    }

    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { binding.calendarView.notifyDateChanged(it) }
            binding.calendarView.notifyDateChanged(date)
            // TODO: update the adapter for the selected date
//            updateAdapterForDate(date)
        }
    }

    private fun setupCalendarMonthBinder() {
        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = CalendarHeaderBinding.bind(view).legendLayout.root
        }

        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)

            override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                // Setup each header day text if we have not done that already.
                if (container.legendLayout.tag == null) {
                    container.legendLayout.tag = data.yearMonth
                    container.legendLayout.children.map { it as TextView }
                        .forEachIndexed { index, tv ->
                            tv.text = daysOfWeek[index].name.take(3)
//                          // tv.setTextColorRes(R.color.example_3_black)
                        }
                }

            }
        }

    }
}








