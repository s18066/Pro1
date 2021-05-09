package pl.edu.pja.pro1

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import pl.edu.pja.pro1.databinding.ActivityMainBinding
import pl.edu.pja.pro1.dialogs.DeleteExpenseDialogFragment
import pl.edu.pja.pro1.dialogs.DeleteExpenseDialogListener
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    @Inject
    lateinit var expenseRepository: ExpenseRepository
    private val expenseAdapter by lazy {
        ExpenseAdapter(
            expenseRepository,
            onExpenseClickListener,
            onExpenseLongClickListener
        )
    }
    private val monthlyExpenseCalculator by lazy { MonthlyExpenseCalculator(expenseRepository) }

    private var activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                launch {
                    refreshScreen()
                }
            }
            launch {
                refreshScreen()
            }
        }

    private val onExpenseClickListener = object : OnExpenseClickListener {
        lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

        override fun onItemClicked(expense: Expense) {
            val intent = Intent(this@MainActivity, EditExpenseActivity::class.java)
            intent.putExtra("EXPENSE_ID", expense.id)
            activityResultLauncher.launch(intent)
        }
    }

    private val onExpenseLongClickListener = object : OnExpenseLongClickListener {
        override fun onItemLongClicked(expense: Expense) {
            val deleteExpenseDialogFragment =
                DeleteExpenseDialogFragment(object :
                    DeleteExpenseDialogListener {
                    override fun onDialogNegativeClick(dialog: DialogFragment) {

                    }

                    override fun onDialogPositiveClick(dialog: DialogFragment) {
                        launch {
                            expenseRepository.remove(expense)
                            refreshScreen()
                        }
                    }
                })
            deleteExpenseDialogFragment.show(supportFragmentManager, "DeleteExpense")
        }

    }

    init {
        onExpenseClickListener.activityResultLauncher = activityResultLauncher
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.addNewExpenseButton.setOnClickListener { onAddExpenseClick() }
        displayExpensesList()
        launch { displaySumOfExpensesForCurrentMonth() }
    }

    private fun onAddExpenseClick(): Unit {
        val intent = Intent(this, AddExpenseActivity::class.java);
        activityResultLauncher.launch(intent)
    }

    private fun displayExpensesList() {
        binding.expensesList.apply {
            adapter = expenseAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private suspend fun refreshScreen() {
        expenseAdapter.notifyDataSetChanged()
        displaySumOfExpensesForCurrentMonth()
    }

    private suspend fun displaySumOfExpensesForCurrentMonth() {
        binding.monthlyExpenseSummaryText.text =
            monthlyExpenseCalculator.calculateExpensesForCurrentMonth().toString()
    }
}