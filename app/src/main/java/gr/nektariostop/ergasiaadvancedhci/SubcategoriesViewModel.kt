package gr.nektariostop.ergasiaadvancedhci

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gr.nektariostop.ergasiaadvancedhci.data.SubCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SubcategoriesViewModel(
    private val subcategoriesRepository: SubcategoriesRepository = Graph.subcategoriesRepository
): ViewModel() {

    lateinit var getAllSubcategories: Flow<List<SubCategory>>

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getAllSubcategories = subcategoriesRepository.getSubcategories()
        }
    }

    private var _subCategories by mutableStateOf(
        listOf<SubCategory>(
//            SubCategory(1,"Beer",1),
//            SubCategory(2,"Soda",1),
//            SubCategory(3,"Premade Coctails",1),
//            SubCategory(4,"Water",1),
//            SubCategory(5,"Energy Drinks",1),
//            SubCategory(6,"Potato Chips",2),
//            SubCategory(7,"Energy Bars",2),
//            SubCategory(8,"Chocolate",2),
//            SubCategory(9,"Gum",2),
//            SubCategory(10,"Cold Sandwiches",2),
//            SubCategory(11,"Scratch Tickets",3),
//            SubCategory(12,"Packs",4),
//            SubCategory(13,"Pouches",4),
//            SubCategory(14,"Snus",4),
        )
    )

    fun setAllSubcategories(subcategories:List<SubCategory>){
        _subCategories = subcategories
    }




    fun getSingleSubCategoryName(id: Long): String{
        return _subCategories.find { it.subCategoryId == id }?.subCategoryName ?: "Subcategory"
    }

    fun getSingleSubCategory(id: Long?): SubCategory?{
        return _subCategories.find { it.subCategoryId == id }
    }

    private var _filteredSubcategories by mutableStateOf(listOf<SubCategory>())

    fun getSubCategoriesWithMainCategoryID(id: Long): List<SubCategory>{
        return _subCategories.filter { it.itMatchesParentID(id) }
    }

    fun setFilteredSubcategories(id: Long){
        _filteredSubcategories = getSubCategoriesWithMainCategoryID(id)
        _subCategoriesSearchList.value = _filteredSubcategories
    }


    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _subCategoriesSearchList = MutableStateFlow(listOf<SubCategory>())
    val subCategoriesSearchList = searchQuery.combine(_subCategoriesSearchList){
            query, subCategories ->
        if(query.isBlank()){
            subCategories
        }
        else{
            subCategories.filter { it.itMatchesQuery(query) }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _subCategoriesSearchList.value
    )

    fun onSearchQueryChange(query: String){
        _searchQuery.value = query
    }


}