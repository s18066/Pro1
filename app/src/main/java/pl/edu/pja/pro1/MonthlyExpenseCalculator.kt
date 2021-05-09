package pl.edu.pja.pro1

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

class MonthlyExpenseCalculator(private val expenseRepository: ExpenseRepository) {

    suspend fun calculateExpensesForCurrentMonth(): Double {
        val currentMonth = LocalDate.now().month
        val expenses = withContext(Dispatchers.IO) {
            expenseRepository.getAll()
        }
        return expenses.filter { expense -> expense.date.month == currentMonth }.sumByDouble { expense -> expense.amount }
    }

}