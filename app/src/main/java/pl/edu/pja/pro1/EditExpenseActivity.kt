package pl.edu.pja.pro1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pl.edu.pja.pro1.databinding.EditExpenseBinding
import pl.edu.pja.pro1.dialogs.DatePicker
import pl.edu.pja.pro1.dialogs.DateSetListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class EditExpenseActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private val binding by lazy { EditExpenseBinding.inflate(layoutInflater) }

    @Inject
    lateinit var expenseRepository: ExpenseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val expenseId = intent.extras?.get("EXPENSE_ID").toString().toInt()
        launch{ bindOldData(expenseId)}

        binding.dateInput.inputType = InputType.TYPE_NULL
        binding.dateInput.setOnClickListener(){ DatePicker(
            dateSetListener = object : DateSetListener {
                override fun onDateSet(localDate: LocalDate) {
                    binding.dateInput.setText(localDate.toString())
                }
            }).show(supportFragmentManager, "datePicker")}

        binding.updateExpense.setOnClickListener{ launch{ save(expenseId)}}
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_expense_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.shareExpenseButton -> {
                var expense = runBlocking {
                    expenseRepository.get(intent.extras?.get("EXPENSE_ID").toString().toInt())
                }
                var expenseShare = ExpenseShare(
                    expense.place,
                    expense.amount,
                    expense.category,
                    expense.date
                )
                shareExpense(expenseShare)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private suspend fun bindOldData(id: Int) {
        val expense = expenseRepository.get(id)
        binding.placeInput.setText(expense.place)
        binding.categoryInput.setText(expense.category)
        binding.amountInput.setText(expense.amount.toString())
        binding.dateInput.setText(expense.date.toString())
    }

    private suspend fun save(expenseId: Int) {
        if(!validateInput())
            return

        val expense = Expense(
            expenseId,
            binding.placeInput.text.toString(),
            binding.amountInput.text.toString().toDouble(),
            binding.categoryInput.text.toString(),
            LocalDate.parse(binding.dateInput.text.toString())
        )
        expenseRepository.save(expense)
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun shareExpense(expenseShare: ExpenseShare) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "Expense")
        intent.putExtra(Intent.EXTRA_TEXT, expenseShare.toString())
        startActivity(Intent.createChooser(intent, "Udostępnij używając"))
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

data class ExpenseShare(
    val place: String,
    val amount: Double,
    val category: String,
    val date: LocalDate
) {
    override fun toString(): String {
        return "Check out my expense! $place, $amount, $category, ${date.format(DateTimeFormatter.BASIC_ISO_DATE)}"
    }
}