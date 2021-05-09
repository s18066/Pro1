package pl.edu.pja.pro1

import androidx.room.*

@Database(entities = [Expense::class], version = 1)
@TypeConverters(LocalDateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao

    companion object {
        const val EXPENSE_TABLE = "expense"
    }
}