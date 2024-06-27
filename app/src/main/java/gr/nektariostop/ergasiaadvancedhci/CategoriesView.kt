package gr.nektariostop.ergasiaadvancedhci

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import gr.nektariostop.ergasiaadvancedhci.data.Category
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Blue
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Gray
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Montserrat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesView(
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

    val context = LocalContext.current

    val searchQuery by categoriesViewModel.searchQuery.collectAsState()
    val categories by categoriesViewModel.categoriesSearchList.collectAsState()

    BackHandler{
        navController.navigate(Screen.HomeScreen.route)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally){

        Row(
            modifier = Modifier.fillMaxWidth().padding(24.dp,0.dp,24.dp,0.dp),
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
                    ).fillMaxWidth(),
                value = searchQuery,
                onValueChange = categoriesViewModel::onSearchQueryChange,
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
                placeholder = { Text(text = "Search Categories") }
            )
        }
        Spacer(modifier = Modifier.size(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(24.dp,0.dp,0.dp,0.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categories",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontFamily = Montserrat,
                fontSize = 16.sp,
            )
            Spacer(modifier = Modifier.size(16.dp))
        }
        Row {
            CategoriesList(categories,context,navController)
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun CategoriesList(categories: List<Category>,context: Context,navController: NavController){

    if(categories.isNotEmpty()){
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(12.dp,24.dp,12.dp,0.dp)
        ){
            items(categories){ category ->
                CategoriesListItem(category,context,navController)
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
                        text = "No available categories",
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
fun CategoriesListItem(
    category: Category,
    context: Context,
    navController: NavController
){



    Card(
        modifier = Modifier
            .height(157.dp)
            .padding(10.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(8.dp),
                ambientColor = DefaultShadowColor,
                spotColor = Color(0x3F000000)
            )
            .clickable {
                val id: Long = category.categoryId
                navController.navigate(Screen.CategoriesScreen.route + "/$id")
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Blue),
            contentAlignment = Alignment.Center
        ){
            if(!category.iconDarkBlue.isNullOrEmpty() && category.getIconID(context,"Dark Blue") != null){
                Image(
                    painter = painterResource(category.getIconID(context,"Dark Blue")!!),
                    contentDescription = "Dark Blue Category Icon",
                    modifier = Modifier
                        .size(92.dp)
                        .offset((-55).dp, 0.dp)
                        .rotate(10f)

                )
            }
            if (!category.iconWhite.isNullOrEmpty() && category.getIconID(context,"White") != null){
                Image(
                    painter = painterResource(category.getIconID(context,"White")!!),
                    contentDescription = "White Category Icon",
                    modifier = Modifier
                        .size(40.dp)
                        .offset(
                            x = 0.dp,
                            y = (-14).dp
                        )
                )
            }
            Text(
                text = category.categoryName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = Montserrat,
                fontSize = 14.sp,
                modifier = Modifier
                    .offset(
                        x = 0.dp,
                        y = 20.dp
                    )
            )
        }
    }
}

