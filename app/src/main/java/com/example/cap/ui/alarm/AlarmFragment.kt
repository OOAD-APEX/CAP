package com.example.cap.ui.alarm

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cap.R
import com.example.cap.databinding.FragmentDashboardBinding
import com.example.cap.domain.Alarm
import java.text.SimpleDateFormat
import java.util.*

class AlarmFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val alarm = Alarm()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {

        val alarmViewModel =
            ViewModelProvider(this).get(AlarmViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val setAlarmbtn = root.findViewById<Button>(R.id.setAlarmBtn)
        val modifyalarmbtn = root.findViewById<Button>(R.id.mAlarmBtn)
        val tvTime = root.findViewById<TextView>(R.id.tvTime)

        setAlarmbtn.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                cal.set(Calendar.SECOND, 0)
                tvTime.text = SimpleDateFormat("HH:mm").format(cal.time)

                alarm.setAlarm(requireContext(), cal)
            }
            val timepickerDialog = TimePickerDialog(
                this.requireContext(),
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            )
            timepickerDialog.show()
        }

        modifyalarmbtn.setOnClickListener{
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                cal.set(Calendar.SECOND, 0)
                tvTime.text = SimpleDateFormat("HH:mm").format(cal.time)

                alarm.replaceTheOnlyAlarm(requireContext(), cal)
            }
            val timepickerDialog = TimePickerDialog(
                this.requireContext(),
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            )
            timepickerDialog.show()
        }

        val selectRingtoneButton = root.findViewById<Button>(R.id.selectRingtoneButton)
        selectRingtoneButton.setOnClickListener {
            val ringtoneManager = RingtoneManager(context)
            ringtoneManager.setType(RingtoneManager.TYPE_ALARM)
            val cursor = ringtoneManager.cursor

            val ringtones = mutableListOf<Uri>()
            while (cursor.moveToNext()) {
                val ringtoneUri = ringtoneManager.getRingtoneUri(cursor.position)
                ringtones.add(ringtoneUri)
            }

            val ringtoneNames = ringtones.map { RingtoneManager.getRingtone(context, it).getTitle(context) }.toTypedArray()

            AlertDialog.Builder(context)
                .setTitle("Select a ringtone")
                .setItems(ringtoneNames) { _, which ->
                    val selectedRingtoneUri = ringtones[which]
                    val sharedPreferences = context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    sharedPreferences?.edit()?.putString("selected_ringtone_uri", selectedRingtoneUri.toString())?.apply()
                }
                .show()
        }
        val deleteAlarmButton = root.findViewById<Button>(R.id.deleteAlarmBtn)
        deleteAlarmButton.setOnClickListener {
            alarm.deleteAlarm(requireContext())
            tvTime.text = "00:00"
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun cancelAlarm(context: Context) {
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.action = "STOP_ALARM"
        context.sendBroadcast(intent)
    }
}