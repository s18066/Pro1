package pl.edu.pja.pro1.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class DeleteExpenseDialogFragment(private val deleteExpenseDialogListener: DeleteExpenseDialogListener) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Usunac?")

            builder.setPositiveButton("Ok") { _, _ ->
                deleteExpenseDialogListener.onDialogPositiveClick(this)
            }

            builder.setNegativeButton("Anuluj") { _, _ ->
                deleteExpenseDialogListener.onDialogNegativeClick(this)
            }

            builder.create()
        }
    }
}

interface DeleteExpenseDialogListener {
    fun onDialogPositiveClick(dialog: DialogFragment)
    fun onDialogNegativeClick(dialog: DialogFragment)
}