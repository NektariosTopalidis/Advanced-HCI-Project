package gr.nektariostop.ergasiaadvancedhci

import gr.nektariostop.ergasiaadvancedhci.data.Category
import gr.nektariostop.ergasiaadvancedhci.data.CategoryDAO
import kotlinx.coroutines.flow.Flow

class CategoriesRepository(
    private val categoriesDAO: CategoryDAO
) {

    fun getCategories(): Flow<List<Category>> = categoriesDAO.getCategories()

    suspend fun addCategory(category: Category){
        categoriesDAO.addCategory(category)
    }
}