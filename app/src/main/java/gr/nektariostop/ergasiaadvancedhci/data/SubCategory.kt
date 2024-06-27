package gr.nektariostop.ergasiaadvancedhci.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subcategories-table")
data class SubCategory (
    @PrimaryKey(autoGenerate = false)
    val subCategoryId: Long = 0L,
    @ColumnInfo(name = "subcategory-name")
    val subCategoryName: String = "",
    @ColumnInfo(name = "parent-category-id")
    val parentCategoryId: Long = 0L,
){
    fun itMatchesParentID(id: Long): Boolean{
        return parentCategoryId == id
    }

    fun itMatchesQuery(query: String): Boolean{
        return subCategoryName.contains(query,ignoreCase = true)
    }
}