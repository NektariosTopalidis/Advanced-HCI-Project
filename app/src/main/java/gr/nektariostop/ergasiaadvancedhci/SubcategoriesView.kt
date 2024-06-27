package gr.nektariostop.ergasiaadvancedhci

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import gr.nektariostop.ergasiaadvancedhci.data.SubCategory
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Gray
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Montserrat

@Composable
fun SubcategoriesView(
    id: Long,
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

    subcategoriesViewModel.setFilteredSubcategories(id)

    BackHandler{
        navController.navigate(Screen.CategoriesScreen.route)
    }

    val searchQuery by subcategoriesViewModel.searchQuery.collectAsState()
    val subCategories by subcategoriesViewModel.subCategoriesSearchList.collectAsState()

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
        ) {
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
                onValueChange = subcategoriesViewModel::onSearchQueryChange,
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
                placeholder = { Text(text = "Search Subcategories") }
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
                categoriesViewModel.getCategoryWithID(id)?.let {
                    Column {
                        Text(
                            text = "Subcategories of ${it.categoryName}",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Montserrat,
                            fontSize = 16.sp,
                        )
                    }
                }
            }
        Row {
            SubcategoriesList(subCategories,navController,id)
        }
    }
}


@Composable
fun SubcategoriesList(subCategories: List<SubCategory>,navController: NavController,id: Long){

    if(subCategories.isNotEmpty()){

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ){
            items(subCategories){
                subCategory ->
                    SubcategoriesListItem(subCategory,navController,id)
                    Spacer(modifier = Modifier.size(12.dp))
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
                        text = "No available subcategories",
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
fun SubcategoriesListItem(subCategory: SubCategory,navController: NavController,id: Long){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .border(1.dp, Color(0x0A000000), RoundedCornerShape(10.dp))
            .clickable {
                navController.navigate(Screen.CategoriesScreen.route + "/$id/${subCategory.subCategoryId}")
            }
            .padding(12.dp, 0.dp)
            ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = subCategory.subCategoryName, color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontFamily = Montserrat,
                fontSize = 16.sp,
            )
        }
        Column {
            Icon(Icons.Filled.KeyboardArrowRight , contentDescription = "Right Arrow", tint = Color.Black)
        }
    }
}