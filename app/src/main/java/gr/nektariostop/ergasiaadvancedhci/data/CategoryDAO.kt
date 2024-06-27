package gr.nektariostop.ergasiaadvancedhci.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CategoryDAO {

    @Query("select * from `categories-table`")
    abstract fun getCategories(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun addCategory(category: Category)

}