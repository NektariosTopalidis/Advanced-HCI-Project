package gr.nektariostop.ergasiaadvancedhci

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gr.nektariostop.ergasiaadvancedhci.data.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val categoriesRepository: CategoriesRepository = Graph.categoriesRepository
): ViewModel() {

    lateinit var getAllCategories: Flow<List<Category>>

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getAllCategories = categoriesRepository.getCategories()
        }
    }

    var categories by mutableStateOf(
        listOf<Category>()
    )

    fun setAllCategories(cats: List<Category>){
        categories = cats
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    fun setCategoriesSearchList(categories: List<Category>){
        _categoriesSearchList.value = categories
    }

    private val _categoriesSearchList = MutableStateFlow(categories)
    val categoriesSearchList = searchQuery.combine(_categoriesSearchList){
        query, categories ->
        if(query.isBlank()){
            categories
        }
        else{
            categories.filter { it.doesMatchSearchQuery(query) }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _categoriesSearchList.value
    )

    fun onSearchQueryChange(query: String){
        _searchQuery.value = query
    }

    fun getCategoryWithID(id: Long?): Category?{
        return categories.find { it.categoryId == id }
    }

}

