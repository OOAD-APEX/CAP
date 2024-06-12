package com.example.cap.ui.calendar

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cap.R
import com.example.cap.databinding.CalendarDayLayoutBinding
import com.example.cap.databinding.CalendarFragmentBinding
import com.example.cap.databinding.CalendarHeaderBinding
import com.example.cap.domain.Alarm
import com.example.cap.domain.Calendar
import com.example.cap.domain.Event
import com.example.cap.domain.TriggerMode
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.util.*

class CalendarFragment : Fragment(R.layout.calendar_fragment) {

    private lateinit var binding: CalendarFragmentBinding
    private val calendarView: CalendarView get() = binding.calendarView
    private val viewModel: CalendarViewModel by viewModels()

    private val calendar = Calendar()

    private var selectedDate: LocalDate
        get() = viewModel.selectedDate.value ?: LocalDate.now()
        set(value) {
            viewModel.selectedDate.value = value
        }

    private val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.SUNDAY)
    private val today = LocalDate.now()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // view binding
        binding = CalendarFragmentBinding.bind(view)

        binding.addEventButton.setOnClickListener {
            EventInputDialog(context = requireContext(), viewModel = viewModel, lifecycleOwner = this ,mode = InputDialogMode.INSERT).create().show()
        }

        // recycle view setup
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = EventAdapter(lifecycleScope, requireContext(), viewModel, this)
        viewModel.events.observe(viewLifecycleOwner) { events ->
            (binding.recyclerView.adapter as EventAdapter).setEvents(events)
            updateAdapterForDate(selectedDate)
        }

        viewModel.selectedDate.observe(viewLifecycleOwner) { selectedDate ->
            updateAdapterForDate(selectedDate)
        }

        viewModel.title.observe(viewLifecycleOwner) {
            // TODO: make it look nicer
            binding.textView.text = it
        }

        setupCalendar()

        if (savedInstanceState == null) {
            binding.calendarView.post { selectDate(today) }
        }
    }

    private fun setupCalendar() {
        setupCalendarDayBinder()
        setupCalendarMonthBinder()
        setupMonthChangeListener()

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
            val binding = CalendarDayLayoutBinding.bind(view)
            val textView = binding.calendarDayText
            val dotView = binding.calendarDotView

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
                val dotView = container.dotView

                if (data.position == DayPosition.MonthDate) {
                    textView.text = data.date.dayOfMonth.toString()
                    when (data.date) {
                        selectedDate -> {
                            textView.setBackgroundResource(R.drawable.selected_day)
                            dotView.isVisible = false
                        }
                        else -> {
                            container.textView.background = null
                            // show the dot if there are events for this day
                            lifecycleScope.launch {
                                // use background thread to query the database,
                                // or it will crush like a dogwater
                                val events = withContext(Dispatchers.IO) {
                                    calendar.getAllItemsForNormalList()
                                }
                                dotView.isVisible = events.any { it.time.toLocalDate() == data.date }
                            }
                        }
                    }
                }
                else {
                    container.textView.text = null
                    dotView.isVisible = false
                }
                // TODO: add selected date's background color
            }
        }
    }

    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate.let { binding.calendarView.notifyDateChanged(it) }
            binding.calendarView.notifyDateChanged(date)

            // TODO: update the adapter for the selected date
            updateAdapterForDate(date)
        }
    }

    private fun updateAdapterForDate(date: LocalDate) {
        viewModel.events.value?.let { events ->
            val eventsForDate = events.filter { it.time.toLocalDate() == date }
            (binding.recyclerView.adapter as EventAdapter).setEvents(eventsForDate)
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








