package com.fic.memotoriweb

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar
import java.util.Locale

class TimePickerFragment(
    private val listener: (String) -> Unit,
    private val title: String = "Selecciona una hora"
) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val picker = TimePickerDialog(requireContext(), this, hour, minute, true)
        picker.setTitle(title)
        return picker
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
        listener(formattedTime)
    }
}
