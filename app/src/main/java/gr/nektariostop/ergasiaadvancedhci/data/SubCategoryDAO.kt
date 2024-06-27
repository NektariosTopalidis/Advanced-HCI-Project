package gr.nektariostop.ergasiaadvancedhci.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SubCategoryDAO {

    @Query("select * from `subcategories-table`")
    abstract fun getAllSubcategories(): Flow<List<SubCategory>>
}

