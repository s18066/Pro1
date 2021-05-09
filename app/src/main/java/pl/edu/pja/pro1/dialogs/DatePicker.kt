package pl.edu.pja.pro1.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.time.LocalDate

class DatePicker(private val dateSetListener: DateSetListener) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val date = LocalDate.now()
            return DatePickerDialog(requireContext(), this, date.year, date.monthValue - 1, date.dayOfMonth)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        dateSetListener.onDateSet(LocalDate.of(year, month + 1, dayOfMonth))
    }
}

interface DateSetListener {
    fun onDateSet(localDate: LocalDate)
}