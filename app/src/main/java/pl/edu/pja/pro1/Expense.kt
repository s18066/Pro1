package pl.edu.pja.pro1

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
class Expense (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val place: String,
    val amount: Double,
    val category: String,
    val date: LocalDate
)