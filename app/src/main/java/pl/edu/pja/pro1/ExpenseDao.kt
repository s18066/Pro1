package pl.edu.pja.pro1

import androidx.room.*

@Dao
interface ExpenseDao {

    @Insert
    suspend fun add(expense: Expense)

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun remove(expense: Expense)

    @Query("select * from expense order by date desc")
    suspend fun getAll(): List<Expense>

    @Query("select * from expense where id = :id")
    suspend fun get(id: Int): Expense

    @Query("select count(1) from expense")
    suspend fun count() : Int
}