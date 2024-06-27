package gr.nektariostop.ergasiaadvancedhci.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products-table")
data class Product (
    @PrimaryKey(autoGenerate = false)
    val productId: Long = 0L,
    @ColumnInfo(name = "product-name")
    val productName: String = "",
    @ColumnInfo(name = "product-stock")
    var productStock: Int = 0,
    @ColumnInfo(name = "subcategory-id")
    val subCategoryId: Long = 0L,
    @ColumnInfo(name = "product-image")
    val imageUrl: String = "",
    @ColumnInfo(name = "product-barcode")
    val barcode: String = ""
){

    fun itMatchesCategotyID(id: Long): Boolean{
        return subCategoryId == id
    }

    fun itMatchesQuery(query: String): Boolean{
        return productName.contains(query,ignoreCase = true)
    }

    fun updateStock(newStock: Int){
        productStock = newStock
    }

}