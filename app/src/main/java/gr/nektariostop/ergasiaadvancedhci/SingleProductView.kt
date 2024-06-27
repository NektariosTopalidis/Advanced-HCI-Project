package gr.nektariostop.ergasiaadvancedhci

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.compose.AsyncImage
import gr.nektariostop.ergasiaadvancedhci.data.Alteration
import gr.nektariostop.ergasiaadvancedhci.data.Category
import gr.nektariostop.ergasiaadvancedhci.data.Comment
import gr.nektariostop.ergasiaadvancedhci.data.Product
import gr.nektariostop.ergasiaadvancedhci.data.SubCategory
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Blue
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Gray
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Montserrat
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Timer
import kotlin.concurrent.schedule

@Composable
fun SingleProductView(
    mainId: Long,
    subId: Long,
    productId: Long,
    innerPadding: PaddingValues,
    navController: NavController,
    scanViewModel: ScanViewModel,
    categoriesViewModel: CategoriesViewModel,
    alterationsViewModel: AlterationsViewModel,
    productsViewModel: ProductsViewModel,
    usersViewModel: UsersViewModel,
    subcategoriesViewModel: SubcategoriesViewModel,
    commentsViewModel: CommentsViewModel
) {

    val context = LocalContext.current

    val pattern = remember { Regex("^\\d+\$") }

    val commentsResult by commentsViewModel.getCommentsResult.observeAsState()
    val addCommentResult by commentsViewModel.addCommentsResult.observeAsState()

    val scope = rememberCoroutineScope()



    when (commentsResult){
        is Result.Success<*> -> {
            commentsViewModel.setComments(((commentsResult as Result.Success<*>).data as List<Comment>))
            commentsViewModel.clearGetComments()
        }

        is Result.Error -> {

        }

        else -> {

        }
    }

    when (addCommentResult){
        is Result.Success<*> -> {
            commentsViewModel.clearAddComment()
        }

        is Result.Error -> {

        }

        else -> {

        }
    }

    val product: Product? = productsViewModel.getProductWithId(productId)
    val subCategory: SubCategory? =
        subcategoriesViewModel.getSingleSubCategory(product?.subCategoryId)

    val category: Category? = categoriesViewModel.getCategoryWithID(subCategory?.parentCategoryId)


    var productStock by remember {
        mutableStateOf(product?.productStock.toString())
    }

    var commentText by remember {
        mutableStateOf("")
    }

    var showCancelDialog by remember {
        mutableStateOf(false)
    }

    var showConfirmDialog by remember {
        mutableStateOf(false)
    }

    val alterationsResult by alterationsViewModel.getAllAlterations.collectAsState(initial = listOf())

    when (alterationsResult){
        else -> {
            alterationsViewModel.setAllAlterations(alterationsResult)
        }
    }

    BackHandler{
        if (product != null) {
            if (productStock.toInt() != product.productStock){
                showCancelDialog = true
            }
            else{
                navController.navigate(Screen.CategoriesScreen.route + "/$mainId/$subId")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp, 0.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if(showConfirmDialog){
            Dialog(
                onDismissRequest = { showCancelDialog = false },
                properties = DialogProperties(true, dismissOnClickOutside = true)
            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(0.9f)
                        .fillMaxHeight(0.2f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp, 0.dp)
                            .fillMaxWidth()
                            .fillMaxHeight(0.5f)
                    ) {
                        Text(
                            text = "Are you sure you want to change the stock of this product?",
                            textAlign = TextAlign.Center
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(12.dp, 0.dp)
                            .fillMaxWidth()
                            .fillMaxHeight(0.5f),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.Bottom
                    ) {

                        Button(
                            onClick = {
                                showConfirmDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        ) {
                            Text(
                                text = "CLOSE",
                                color = Color.Black,
                                fontFamily = Montserrat,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.size(8.dp))
                        Button(
                            onClick = {

                                var alterationType: String
                                var amount: Int

                                if (product != null) {
                                    if (product.productStock > productStock.toInt()) {
                                        alterationType = "Subtraction"
                                        amount = product.productStock - productStock.toInt()
                                    } else {
                                        alterationType = "Addition"
                                        amount = productStock.toInt() - product.productStock
                                    }

                                    println(alterationType)
                                    val alteration = Alteration(
                                        0,
                                        alterationType,
                                        productId,
                                        usersViewModel.activeUser!!.userId,
                                        amount,
                                        productStock.toInt()
                                    )
                                    alterationsViewModel.addAlteration(alteration)
                                    Timer().schedule(100) {
                                        productsViewModel.setNewStockForProduct(product,amount,alterationType)
                                        if (commentText.isNotEmpty()) {
                                            val comment = Comment(
                                                0,
                                                alterationsViewModel.insertedID,
                                                alteration.userId,
                                                commentText,
                                                LocalDateTime.now()
                                            )
                                            commentsViewModel.addComment(comment)
                                        }

                                        scope.launch {
                                            Toast.makeText(
                                                context,
                                                "Product ${product.productName} was updated succesfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            alterationsViewModel.showNotification = true
                                            navController.navigate(Screen.HomeScreen.route)
                                        }
                                    }
                                }
                            }
                        ) {
                            Text(
                                text = "CONFIRM",
                                fontFamily = Montserrat,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }


                    }
                }
            }
        }

        if (showCancelDialog) {
            Dialog(
                onDismissRequest = { showCancelDialog = false },
                properties = DialogProperties(true, dismissOnClickOutside = true)
            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(0.9f)
                        .fillMaxHeight(0.2f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp, 0.dp)
                            .fillMaxWidth()
                            .fillMaxHeight(0.5f)
                    ) {
                        Text(
                            text = "By canceling your changes will be lost.",
                            textAlign = TextAlign.Center
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(12.dp, 0.dp)
                            .fillMaxWidth()
                            .fillMaxHeight(0.5f),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.Bottom
                    ) {

                        Button(
                            onClick = {
                                showCancelDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        ) {
                            Text(
                                text = "CLOSE",
                                color = Color.Black,
                                fontFamily = Montserrat,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.size(8.dp))
                        Button(
                            onClick = {
                                navController.navigate(Screen.CategoriesScreen.route + "/$mainId/$subId")
                            }
                        ) {
                            Text(
                                text = "CONFIRM",
                                fontFamily = Montserrat,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }


                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            if (product != null) {
                Text(
                    text = product.productName,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Montserrat,
                    fontSize = 18.sp,
                )
            }
        }
        Spacer(modifier = Modifier.size(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
        ) {
            if (product != null) {
                if (product.imageUrl != null) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = "Image of ${product.productName}",
                        placeholder = painterResource(R.drawable.product_image_placeholder),
                        modifier = Modifier
                            .fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.product_image_placeholder),
                        contentDescription = "No Image For Product ${product.productName}",
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Column {
                Text(
                    text = "Main Category: ",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Montserrat,
                    fontSize = 12.sp,
                )
            }
            Column {
                if (category != null) {
                    Text(
                        text = category.categoryName,
                        color = Gray,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Montserrat,
                        fontSize = 12.sp,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Column {
                Text(
                    text = "Sub Category: ",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Montserrat,
                    fontSize = 12.sp,
                )
            }
            Column {
                if (subCategory != null) {
                    Text(
                        text = subCategory.subCategoryName,
                        color = Gray,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Montserrat,
                        fontSize = 12.sp,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Column {
                Text(
                    text = "Stock: ",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Montserrat,
                    fontSize = 12.sp,
                )
            }
            Column {
                if (product != null) {
                    Text(
                        text = product.productStock.toString(),
                        color = Gray,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Montserrat,
                        fontSize = 12.sp,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "New Stock",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontFamily = Montserrat,
                fontSize = 15.sp,
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                FloatingActionButton(
                    onClick = {
                        val newStock = productStock.toInt() + 1
                        productStock = newStock.toString()
                    },
                    containerColor = Blue,
                    shape = CircleShape,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Plus Icon",
                        tint = Color.White
                    )
                }
            }
            TextField(
                value = productStock,
                onValueChange = {
                    if (it.matches(pattern)) {
                        productStock = it
                    }
                },
                modifier = Modifier
                    .width(170.dp),
                colors = TextFieldDefaults.colors(
                    disabledContainerColor = Color(0x0F3CA1FF),
                    errorContainerColor = Color(0x0F3CA1FF),
                    focusedContainerColor = Color(0x0F3CA1FF),
                    unfocusedContainerColor = Color(0x0F3CA1FF),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Gray,
                    cursorColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Column {
                FloatingActionButton(
                    onClick = {
                        val newStock = productStock.toInt() - 1
                        productStock = newStock.toString()
                    },
                    modifier = Modifier,
                    containerColor = Color.White,
                    shape = CircleShape,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.minus_icon),
                        contentDescription = "Minus Icon"
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Leave a comment:",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontFamily = Montserrat,
                fontSize = 15.sp,
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            TextField(
                value = commentText,
                onValueChange = {
                    commentText = it
                },
                placeholder = {
                    Text(
                        text = "Comment",
                        color = Gray,
                        fontFamily = Montserrat,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                colors = TextFieldDefaults.colors(
                    disabledContainerColor = Color(0x0F3CA1FF),
                    errorContainerColor = Color(0x0F3CA1FF),
                    focusedContainerColor = Color(0x0F3CA1FF),
                    unfocusedContainerColor = Color(0x0F3CA1FF),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Gray,
                    cursorColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
            )
        }
        Spacer(modifier = Modifier.size(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Column {
                Button(
                    onClick = {
                        if (product != null) {
                            if (product.productStock != productStock.toInt()) {
                                showCancelDialog = true
                            } else {
                                navController.navigate(Screen.CategoriesScreen.route + "/$mainId/$subId")
                            }
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text(
                        text = "CANCEL",
                        fontWeight = FontWeight.Bold,
                        fontFamily = Montserrat,
                        fontSize = 15.sp,
                    )
                }
            }
            Spacer(modifier = Modifier.size(8.dp))
            Column {
                if (product != null) {
                    Button(
                        onClick = {
                            if (product != null) {
                                if (product.productStock != productStock.toInt()) {
                                    showConfirmDialog = true
                                } else {
                                    navController.navigate(Screen.CategoriesScreen.route + "/$mainId/$subId")
                                }
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Blue,
                            disabledContainerColor = Color(60, 161, 255, 0x8f),
                        ),
                        enabled = product.productStock != productStock.toInt()
                    ) {
                        Text(
                            text = "CONFIRM",
                            fontWeight = FontWeight.Bold,
                            fontFamily = Montserrat,
                            fontSize = 15.sp,
                        )
                    }
                }
            }
        }
    }
}


