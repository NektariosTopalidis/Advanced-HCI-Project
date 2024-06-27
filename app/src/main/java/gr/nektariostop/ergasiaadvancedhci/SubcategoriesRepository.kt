package gr.nektariostop.ergasiaadvancedhci

import gr.nektariostop.ergasiaadvancedhci.data.SubCategory
import gr.nektariostop.ergasiaadvancedhci.data.SubCategoryDAO
import kotlinx.coroutines.flow.Flow

class SubcategoriesRepository(
    private val subcategoriesDAO: SubCategoryDAO
) {

    fun getSubcategories(): Flow<List<SubCategory>> = subcategoriesDAO.getAllSubcategories()

}
