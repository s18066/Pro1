package pl.edu.pja.pro1

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepository @Inject constructor(private val expenseDao: ExpenseDao) {

    suspend fun add(expense: Expense) {
        expenseDao.add(expense)
    }

    suspend fun save(expense: Expense) {
        expenseDao.update(expense)
    }

    suspend fun remove(expense: Expense) {
        expenseDao.remove(expense)
    }

    suspend fun getAll(): List<Expense> {
        return expenseDao.getAll()
    }

    suspend fun get(id: Int): Expense {
        return expenseDao.get(id)
    }

    suspend fun count(): Int {
        return expenseDao.count()
    }
}

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    fun provideExpenseDao(appDatabase: AppDatabase) : ExpenseDao {
        return appDatabase.expenseDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext applicationContext: Context) : AppDatabase {
        return Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "ExpenseDatabase").build()
    }
}