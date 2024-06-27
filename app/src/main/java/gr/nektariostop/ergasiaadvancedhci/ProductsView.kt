package gr.nektariostop.ergasiaadvancedhci

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import gr.nektariostop.ergasiaadvancedhci.data.Product
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Blue
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Gray
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Montserrat

@Composable
fun ProductsView(
    mainId: Long,
    subId: Long,
    innerPadding: PaddingValues,
    navController: NavController,
    scanViewModel: ScanViewModel,
    categoriesViewModel: CategoriesViewModel,
    alterationsViewModel: AlterationsViewModel,
    productsViewModel: ProductsViewModel,
    usersViewModel: UsersViewModel,
    subcategoriesViewModel: SubcategoriesViewModel,
    commentsViewModel: CommentsViewModel
){

    productsViewModel.setFilteredProducts(subId)
    val shownProducts by productsViewModel.shownProducts.collectAsState(initial = listOf())
    productsViewModel.setProductsSearchList(shownProducts)



    BackHandler{
        navController.navigate(Screen.CategoriesScreen.route + "/$mainId")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally){


        val context = LocalContext.current

        val searchQuery by productsViewModel.searchQuery.collectAsState()
        val products by productsViewModel.productsSearchList.collectAsState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally){

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp, 0.dp, 24.dp, 0.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            )  {
                TextField(
                    modifier = Modifier
                        .shadow(
                            elevation = 1.dp,
                            shape = RoundedCornerShape(8.dp),
                            ambientColor = DefaultShadowColor,
                            spotColor = Color(0x3F000000)
                        )
                        .fillMaxWidth(),
                    value = searchQuery,
                    onValueChange = productsViewModel::onSearchQueryChange,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            tint = Color.Black,
                            contentDescription = null
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        disabledContainerColor = Color(0xFFF4F4F4),
                        errorContainerColor = Color(0xFFF4F4F4),
                        focusedContainerColor = Color(0xFFF4F4F4),
                        unfocusedContainerColor = Color(0xFFF4F4F4),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Gray,
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp),
                    placeholder = { Text(text = "Search Products") }
                )
            }
            Spacer(modifier = Modifier.size(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp, 0.dp, 0.dp, 0.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Products of ${subcategoriesViewModel.getSingleSubCategoryName(subId)}",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Montserrat,
                    fontSize = 16.sp,
                )
                Spacer(modifier = Modifier.size(16.dp))
            }
            Row {
                ProductsList(products,mainId,subId,context,navController)
            }
        }
    }
}

@Composable
fun ProductsList(products: List<Product>, mainId: Long, subId: Long, context: Context,navController: NavController){
    if (products.isNotEmpty()){
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(12.dp,24.dp,12.dp,0.dp)
        ){
            items(products){ product ->
                ProductsListItem(product,mainId,subId,context,navController)
            }
        }
    }
    else{
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row {
                Column {
                    Text(
                        text = "No available products",
                        color = Gray,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Montserrat,
                        fontSize = 14.sp,
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.no_results_icon),
                        contentDescription = "No results icon"
                    )
                }
            }

        }
    }
}

@Composable
fun ProductsListItem(product: Product, mainId: Long, subId: Long, context: Context,navController: NavController){

    Card(
        modifier = Modifier
            .padding(10.dp)
            .height(240.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = DefaultShadowColor,
                spotColor = Color(0x3F000000)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(0.dp, 12.dp, 0.dp, 12.dp)
            ,
        ){

            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ){
                if(product.imageUrl != null){
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = "Image of ${product.productName}",
                        placeholder = painterResource(R.drawable.product_image_placeholder),
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
                else{
                    Image(
                        painter = painterResource(R.drawable.product_image_placeholder),
                        contentDescription = "No Image For Product ${product.productName}",
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
            }
            Spacer(modifier = Modifier.size(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.productName,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Montserrat,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Button(
                    onClick = {
                        navController.navigate(Screen.CategoriesScreen.route + "/$mainId/$subId/${product.productId}")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Blue)
                ) {
                    Text(text = "EDIT PRODUCT", color = Color.White ,fontFamily = Montserrat,fontWeight = FontWeight.Bold, fontSize = 8.sp)
                }
            }

        }
    }

}