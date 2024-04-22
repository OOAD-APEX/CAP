package com.example.cap.ui.calendar

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.cap.R
import com.example.cap.databinding.CalendarDayLayoutBinding
import com.example.cap.databinding.CalendarFragmentBinding
import com.example.cap.databinding.CalendarHeaderBinding
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

class CalendarFragment : Fragment(R.layout.calendar_fragment) {

    private lateinit var binding: CalendarFragmentBinding
    private val calendarView: CalendarView get() = binding.calendarView
    private val viewModel: CalendarViewModel by viewModels<CalendarViewModel>()

    private val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.SUNDAY)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // view binding
        binding = CalendarFragmentBinding.bind(view)

        setupCalendar()

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

    private fun setupDateOnClickListener() {
        TODO("Not yet implemented")
    }

    private fun setupCalendarDayBinder() {
        class DayViewContainer(view: View) : ViewContainer(view) {
            val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
        }

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                when (data.position == DayPosition.MonthDate) {
                    true -> {
                        container.textView.text = data.date.dayOfMonth.toString()
                    }
                    false -> {
                        container.textView.text = null
                    }
                }
                // TODO: add selected date's background color
            }
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








