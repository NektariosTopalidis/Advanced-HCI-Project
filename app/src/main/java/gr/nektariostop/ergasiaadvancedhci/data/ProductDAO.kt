package gr.nektariostop.ergasiaadvancedhci.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addProduct(product: Product)


    @Query("select * from `products-table` where productId=:id ")
    abstract fun getProductById(id: Long): Flow<Product>

    @Query("select * from `products-table`")
    abstract fun getAllProducts(): Flow<List<Product>>

    @Query("select * from `products-table` where `subcategory-id`=:id ")
    abstract fun getProductsBySubcategoryID(id: Long): Flow<List<Product>>

    @Query("select * from `products-table` where `product-barcode`=:barcode ")
    abstract fun getProductByBarcode(barcode: String): Flow<Product>

    @Update
    abstract fun updateProduct(product: Product)

}

