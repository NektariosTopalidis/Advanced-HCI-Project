package gr.nektariostop.ergasiaadvancedhci

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gr.nektariostop.ergasiaadvancedhci.data.Alteration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate

class AlterationsViewModel(
    private val alterationsRepository: AlterationsRepository = Graph.alterationsRepository
): ViewModel()  {


    lateinit var getAllAlterations: Flow<List<Alteration>>

    init {
        viewModelScope.launch {
            getAllAlterations = alterationsRepository.getAlterations()
        }
    }

    var showNotification by mutableStateOf(false)

    private var _alterations by mutableStateOf(listOf<Alteration>())

    var waitingForDeletion by mutableStateOf(false)
    var insertedID by mutableLongStateOf(0L)

    var homeScreenAlterations by mutableStateOf(listOf<Alteration>())

    fun getAlterationWithID(id: Long): Alteration?{
        return _alterations.find { it.id == id }
    }

    fun setAllAlterations(alterations: List<Alteration>){
        _alterations = alterations
    }

    fun setHomeScreenAlterations(){
        if (_alterations.size > 5){
            homeScreenAlterations = _alterations.takeLast(5).reversed()
        }
        else{
            homeScreenAlterations = _alterations.reversed()
        }
    }


    fun addAlteration(alteration: Alteration){
        viewModelScope.launch(Dispatchers.IO) {
            insertedID = alterationsRepository.addAlteration(alteration)
        }
    }

    fun undoAlteration(alteration: Alteration){
        viewModelScope.launch(Dispatchers.IO) {
            _alterations = _alterations.filter { alt -> alt.id != alteration.id }
            alterationsRepository.deleteAlteration(alteration)
        }
    }

    fun filterAlterations(startDate: LocalDate,endDate: LocalDate,userID: String?,mainCatID: Long?, subCatID: Long?,productsViewModel: ProductsViewModel,categoriesViewModel: CategoriesViewModel,subcategoriesViewModel: SubcategoriesViewModel): List<Alteration>{

        var alterationsFiltered = _alterations.filter {
            alt ->
                (alt.date.isAfter(startDate) || alt.date.isEqual(startDate))
                &&
                (alt.date.isBefore(endDate) || alt.date.isEqual(endDate))
        }
        if (userID != null){
            alterationsFiltered = alterationsFiltered.filter { alt -> alt.userId == userID }
        }

        if (mainCatID != null){
            alterationsFiltered = alterationsFiltered.filter {
                alt ->

                val product = productsViewModel.getProductWithId(alt.productId)

                val productSubCat = subcategoriesViewModel.getSingleSubCategory(product!!.subCategoryId)
                val productMainCat = categoriesViewModel.getCategoryWithID(productSubCat!!.parentCategoryId)

                return@filter productMainCat?.categoryId == mainCatID
            }
        }

        if (subCatID != null){
            alterationsFiltered = alterationsFiltered.filter {
                    alt ->
                val product = productsViewModel.getProductWithId(alt.productId)

                return@filter product!!.subCategoryId == subCatID
            }
        }

        return alterationsFiltered.sortedByDescending {
            it.date
        }
    }



}