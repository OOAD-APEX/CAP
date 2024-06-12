package com.example.cap.ui.calendar;

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.cap.databinding.EventItemBinding
import com.example.cap.domain.Calendar
import com.example.cap.domain.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class EventAdapter (private val scope: CoroutineScope, private val context: Context, private val viewModel: CalendarViewModel) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    private var events = emptyList<Event>()
    private val calendar = Calendar()


    class EventViewHolder(binding: EventItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val nameText: TextView = binding.eventName
        val timeText: TextView = binding.eventTime
        val editButton: ImageButton = binding.editButton
        val deleteButton: ImageButton = binding.deleteButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = EventItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return EventViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val current: Event = events[position]
        holder.nameText.text = current.title
        holder.timeText.text = String.format("%02d:%02d", current.time.hour, current.time.minute)
        holder.editButton.setOnClickListener {
            EventInputDialog(current, context, viewModel, InputDialogMode.UPDATE).create().show()
        }
        holder.deleteButton.setOnClickListener {
            showDeleteDialog(it, current)
        }
    }

    private fun showDeleteDialog(it: View, current: Event) {
        AlertDialog.Builder(it.context)
            .setTitle("Delete Event")
            .setMessage("Are you sure you want to delete this event?")
            .setPositiveButton("Yes") { _, _ ->
                scope.launch {
                    calendar.deleteEvent(current)
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    internal fun setEvents(events: List<Event>) {
        notifyItemRangeRemoved(0, itemCount)
        this.events = events
        notifyItemRangeInserted(0, events.size)
    }

    override fun getItemCount() = events.size
}
