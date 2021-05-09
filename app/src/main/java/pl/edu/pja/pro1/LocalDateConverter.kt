package pl.edu.pja.pro1

import androidx.room.TypeConverter
import java.time.LocalDate

class LocalDateConverter {

    @TypeConverter
    fun toLocalDateTime(dateString: String): LocalDate {
        return LocalDate.parse(dateString)
    }

    @TypeConverter
    fun toLocalDateTimeString(localDate: LocalDate): String {
            return localDate.toString()
    }
}
