package gr.nektariostop.ergasiaadvancedhci

import gr.nektariostop.ergasiaadvancedhci.data.Product
import gr.nektariostop.ergasiaadvancedhci.data.ProductDAO
import kotlinx.coroutines.flow.Flow

class ProductsRepository(
    private val productDAO: ProductDAO
) {

    suspend fun getAllProducts(): Flow<List<Product>> = productDAO.getAllProducts()

    suspend fun addProduct(product: Product){
        productDAO.addProduct(product)
    }

    suspend fun getProductsBySubcategoryID(id: Long): Flow<List<Product>> = productDAO.getProductsBySubcategoryID(id)

    suspend fun updateProduct(product: Product){
        productDAO.addProduct(product)
    }

    suspend fun getProductWithId(id: Long): Flow<Product> = productDAO.getProductById(id)
}
