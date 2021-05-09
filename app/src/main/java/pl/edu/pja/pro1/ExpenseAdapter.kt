package pl.edu.pja.pro1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import pl.edu.pja.pro1.databinding.ExpenseBinding

class ExpenseAdapter(
    private val expensesRepository: ExpenseRepository,
    private val onExpenseClickedListener: OnExpenseClickListener,
    private val onExpenseLongClickListener: OnExpenseLongClickListener
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>(), CoroutineScope by MainScope() {

    class ExpenseViewHolder(private val binding: ExpenseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            expense: Expense,
            onExpenseClickedListener: OnExpenseClickListener,
            onExpenseLongClickListener: OnExpenseLongClickListener
        ) {
            binding.ammount.text = expense.amount.toString()
            binding.category.text = expense.category
            binding.date.text = expense.date.toString()
            binding.place.text = expense.place

            itemView.setOnClickListener {
                onExpenseClickedListener.onItemClicked(expense)
            }

            itemView.setOnLongClickListener {
                onExpenseLongClickListener.onItemLongClicked(expense)
                return@setOnLongClickListener true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        launch {
            val expenses = withContext(Dispatchers.IO) {
                expensesRepository.getAll()
            }
            holder.bind(expenses[position], onExpenseClickedListener, onExpenseLongClickListener)
        }
    }

    override fun getItemCount(): Int {
        return runBlocking {
            withContext(Dispatchers.IO) {
                expensesRepository.count()
            } }
    }
}

interface OnExpenseClickListener {
    fun onItemClicked(expense: Expense)
}

interface OnExpenseLongClickListener {
    fun onItemLongClicked(expense: Expense)
}
