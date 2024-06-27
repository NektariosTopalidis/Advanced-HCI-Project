package gr.nektariostop.ergasiaadvancedhci

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gr.nektariostop.ergasiaadvancedhci.data.Product
import gr.nektariostop.ergasiaadvancedhci.data.SubCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductsViewModel(
    private val productsRepository: ProductsRepository = Graph.productsRepository
): ViewModel() {

    lateinit var selectedProduct: Flow<Product>
    lateinit var shownProducts: Flow<List<Product>>
    lateinit var allProducts: Flow<List<Product>>

    init {
        viewModelScope.launch {

            allProducts = productsRepository.getAllProducts()

        }
    }

    private var _products by mutableStateOf(listOf<Product>())

    fun getProducts(){
        viewModelScope.launch {

            allProducts = productsRepository.getAllProducts()

        }
    }

    fun setProducts(products: List<Product>){
        _products = products
    }

    fun setNewStockForProduct(product: Product?,amount: Int,alterationType: String){
      viewModelScope.launch(Dispatchers.IO) {
          if(alterationType == "Addition"){
              product!!.productStock += amount
          }
          else{
              product!!.productStock -= amount
          }
          productsRepository.updateProduct(product!!)
      }

    }

    fun undoAlteration(product: Product?, amount: Number, action: String){

        viewModelScope.launch(Dispatchers.IO) {
            var newStock: Int
            if(action == "Addition"){
                newStock = product?.productStock!! - amount.toInt()
            }
            else{
                newStock = product?.productStock!! + amount.toInt()
            }
            product.productStock = newStock
            productsRepository.updateProduct(product)
        }

    }

    fun getProductWithId(productId: Long): Product?{
        return _products.find { product -> product.productId == productId }
    }

    fun getProductWithBarcode(barcode: String): Product?{
        return _products.find { product -> product.barcode == barcode }
    }

    fun getProductsCount(subcategories: List<SubCategory>): Int{
        var count: Int = 0
        subcategories.forEach {
            subcat ->
            count += _products.count {
                product ->
                product.subCategoryId == subcat.subCategoryId
            }
        }
        return  count
    }

    fun setFilteredProducts(id: Long){
        viewModelScope.launch {
            shownProducts = productsRepository.getProductsBySubcategoryID(id)
        }
    }

    fun setProductsSearchList(products: List<Product>){
        _productsSearchList.value = products
    }


    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _productsSearchList = MutableStateFlow(listOf<Product>())
    val productsSearchList = searchQuery.combine(_productsSearchList){
            query, products ->
        if(query.isBlank()){
            products
        }
        else{
            products.filter { it.itMatchesQuery(query) }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _productsSearchList.value
    )

    fun onSearchQueryChange(query: String){
        _searchQuery.value = query
    }

}