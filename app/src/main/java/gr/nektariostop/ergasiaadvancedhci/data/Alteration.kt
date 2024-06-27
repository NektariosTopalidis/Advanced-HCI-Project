package gr.nektariostop.ergasiaadvancedhci.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "history-table")
data class Alteration(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    @ColumnInfo(name = "alteration-type")
    val type: String = "",
    @ColumnInfo("product-id")
    val productId: Long = 0L,
    @ColumnInfo(name = "user-id")
    val userId: String = "",
    @ColumnInfo(name = "alteration-amount")
    val amount: Int = 0,
    @ColumnInfo(name = "stock-after-update")
    val stockAfterUpdate: Int,
    @ColumnInfo(name = "alteration-date")
    val date: LocalDate = LocalDate.now()
)