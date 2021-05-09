package pl.edu.pja.pro1

import android.app.Activity
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import pl.edu.pja.pro1.databinding.AddExpenseBinding
import pl.edu.pja.pro1.dialogs.DatePicker
import pl.edu.pja.pro1.dialogs.DateSetListener
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class AddExpenseActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private val binding by lazy { AddExpenseBinding.inflate(layoutInflater) }

    @Inject
    lateinit var expenseRepository: ExpenseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.dateInput.setOnClickListener() {
            DatePicker(
                dateSetListener = object : DateSetListener {
                    override fun onDateSet(localDate: LocalDate) {
                        binding.dateInput.setText(localDate.toString())
                    }
                }).show(supportFragmentManager, "datePicker")
        }

        binding.saveExpense.setOnClickListener { launch { saveForm() } }
    }

    private suspend fun saveForm() {
        if(!validateInput())
            return

        val expense = Expense(
            0,
            binding.placeInput.text.toString(),
            binding.amountInput.text.toString().toDouble(),
            binding.categoryInput.text.toString(),
            LocalDate.parse(binding.dateInput.text.toString())
        )

        expenseRepository.add(expense)
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun validateInput(): Boolean {
        return validateField(binding.placeInput) and validateField(binding.amountInput) and validateField(binding.categoryInput) and validateField(binding.dateInput)
    }

    private fun validateField(toCheck: EditText): Boolean {
        if (toCheck.text.isEmpty()) {
            toCheck.error = "Musi być wypełninone"
            return false
        }

        return true
    }
}