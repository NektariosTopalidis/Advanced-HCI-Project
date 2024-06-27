package gr.nektariostop.ergasiaadvancedhci.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AlterationDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addAlteration(alterationEntity: Alteration): Long

    @Query("select * from `history-table`")
    abstract fun getAllAlterations(): Flow<List<Alteration>>

    @Query("select * from `history-table` where id=:id ")
    abstract fun getAlterationsById(id: Long): Flow<Alteration>

    @Delete
    abstract suspend fun deleteAlteration(alterationEntity: Alteration)

}


