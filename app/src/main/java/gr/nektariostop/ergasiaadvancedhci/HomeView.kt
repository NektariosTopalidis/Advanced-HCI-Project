package gr.nektariostop.ergasiaadvancedhci

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import gr.nektariostop.ergasiaadvancedhci.data.Comment
import gr.nektariostop.ergasiaadvancedhci.data.Product
import gr.nektariostop.ergasiaadvancedhci.data.User
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Blue
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Gray
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Montserrat


@SuppressLint("MutableCollectionMutableState")
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun HomeView(
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

    val categories by categoriesViewModel.getAllCategories.collectAsState(initial = listOf())
    categoriesViewModel.setAllCategories(categories)
    categoriesViewModel.setCategoriesSearchList(categories)

    val subcategories by subcategoriesViewModel.getAllSubcategories.collectAsState(initial = listOf())
    subcategoriesViewModel.setAllSubcategories(subcategories)


    alterationsViewModel.setHomeScreenAlterations()

    val commentsResult by commentsViewModel.getCommentsResult.observeAsState()

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

    val allProducts by productsViewModel.allProducts.collectAsState(initial = listOf())
    productsViewModel.setProducts(allProducts)

    var timesPressedBack by remember{ mutableStateOf(0) }

    var profilePic by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    var storageRef = Firebase.storage.reference

    storageRef.child("images/${usersViewModel.activeUser!!.userId}.jpg").downloadUrl
        .addOnSuccessListener {
            uri ->
            profilePic = uri
        }
        .addOnFailureListener {
            println("FAILED")
        }


    val getAllUsersResult by usersViewModel.getAllUsersResult.observeAsState()
    val removeCommentsResult by commentsViewModel.removeCommentsResult.observeAsState()
    val alterationsResult by alterationsViewModel.getAllAlterations.collectAsState(initial = listOf())

    when (alterationsResult){
        else -> {
            alterationsViewModel.setAllAlterations(alterationsResult)
        }
    }


    when (getAllUsersResult){
        is Result.Success<*> -> {
            usersViewModel.setAllUsers((getAllUsersResult as Result.Success<*>).data as List<User>)
            usersViewModel.clearGetAllUsersResult()
        }

        is Result.Error -> {

        }

        is Result.DoNothing -> {
            println("Do nothing")
        }

        else -> {

        }
    }

    when (removeCommentsResult){
        is Result.Success -> {
            commentsViewModel.clearRemoveComments()
        }

        is Result.Error -> {

        }

        is Result.DoNothing -> {

        }

        else -> {

        }
    }




    BackHandler{
        timesPressedBack++
    }

    val context = LocalContext.current

    val barCodeLauncher = rememberLauncherForActivityResult((ScanContract())){
            result ->
        if(result.contents == null){
            Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
        }
        else{
            scanViewModel.textResultState = result.contents
        }
    }

    fun showCamera(){
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
        options.setPrompt("Scan a Barcode")
        options.setBeepEnabled(false)
        options.setOrientationLocked(false)

        barCodeLauncher.launch(options)
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {
                permissions ->

            if(permissions[android.Manifest.permission.CAMERA] == false){
                val rationalRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    android.Manifest.permission.CAMERA
                )

                if(rationalRequired){
                    Toast.makeText(context,"Camera Required", Toast.LENGTH_LONG).show()
                }
            }
            else{
                showCamera()
            }
        }
    )

    var showLogoutDialog = remember {
        mutableStateOf(false)
    }

    if(showLogoutDialog.value){
        Dialog(
            onDismissRequest = { showLogoutDialog.value = false },
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
                        text = "Are you sure you want to Logout?",
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
                            showLogoutDialog.value = false
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
                            showLogoutDialog.value = false
                            usersViewModel.logout {
                                navController.navigate(Screen.LoginScreen.route)
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


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (timesPressedBack == 1){
            BackButtonPressedToast(context)
        }
        if (timesPressedBack == 2){
            val activity = (context as? Activity)
            activity?.finish()
        }
        Row(modifier = Modifier
            .padding(start = 24.dp, end = 24.dp)
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically)
        {
            UserAndLogoutBtn(usersViewModel,showLogoutDialog,profilePic)
        }
        Spacer(modifier = Modifier.size(32.dp))
        Row(modifier = Modifier
            .padding(start = 24.dp, end = 24.dp)
            .fillMaxWidth())
        {
            Text(text = "Welcome to the ShelfIt App. We help you keep track of your products!",color = Color.Black, fontFamily = Montserrat,fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.size(16.dp))
        Row (modifier = Modifier
            .padding(start = 24.dp, end = 24.dp)
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically)
        {
            Column {
                Image(
                    painter = painterResource(id = R.drawable.ph_barcode_bold),
                    contentDescription = stringResource(id = R.string.barcode_scan_icon_content_description)
                )
            }
            Column {
                Text(text = "Get started by scanning your product!",color = Gray, fontFamily = Montserrat,fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
        Spacer(modifier = Modifier.size(10.dp))
        Row (modifier = Modifier
            .fillMaxWidth()){
            Column(modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
                .paint(
                    painterResource(
                        id = R.drawable.scan_button_background,
                    )
                ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                CompositionLocalProvider(LocalRippleTheme provides BlueRippleTheme) {
                    Button(onClick = { requestPermissionLauncher.launch(
                        arrayOf(
                            android.Manifest.permission.CAMERA
                        )
                    ) }, modifier = Modifier.offset(x = 0.dp,y = (-5).dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                        Text(text = "SCAN NOW", color = Blue,fontFamily = Montserrat,fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.size(10.dp))
        Row(modifier = Modifier
            .padding(start = 24.dp, end = 24.dp)
            .fillMaxWidth()) {
            SectionTitle(title = "Categories")
        }
        Spacer(modifier = Modifier.size(16.dp))
        CategoriesList(context,categoriesViewModel,subcategoriesViewModel, productsViewModel, navController)
        Spacer(modifier = Modifier.size(10.dp))
        Row(modifier = Modifier
            .padding(start = 24.dp, end = 12.dp)
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
            ) {
            Column {
                SectionTitle(title = "History")
            }
            Column {
                CompositionLocalProvider(LocalRippleTheme provides BlueRippleTheme) {
                    Button(
                        onClick = { navController.navigate(Screen.HistoryScreen.route) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x00F8F8F8))
                    ) {
                        Text(text = "Show All History", color = Blue ,fontFamily = Montserrat,fontWeight = FontWeight.SemiBold, fontSize = 10.sp)
                    }
                }
            }
        }
        Row(modifier = Modifier
            .padding(start = 24.dp, end = 12.dp)
            .fillMaxWidth())  {
            AlterationsList(alterationsViewModel,productsViewModel,usersViewModel,commentsViewModel,navController)
        }
    }


}



private object BlueRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor(): Color = Blue

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleTheme.defaultRippleAlpha(
        Color.Black,
        lightTheme = true
    )
}

object BlackRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor(): Color = Color.Black

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleTheme.defaultRippleAlpha(
        Color.Black,
        lightTheme = true
    )
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun BackButtonPressedToast(context: Context){

    Toast.makeText(context,"Press Back Button Again To Exit",Toast.LENGTH_SHORT).show()
}

@Composable
fun DrawShape(
    shape: Shape,
    color: Color,
    size: Dp = 40.dp,
    content: @Composable () -> Unit
    ){
    Box(modifier = Modifier
        .size(size)
        .clip(shape)
        .background(color),
        contentAlignment = Alignment.Center
        ){
        content()
    }
}

@Composable
fun UserAndLogoutBtn(usersViewModel: UsersViewModel,showLogoutDialog: MutableState<Boolean>,profilePic: Uri){



    Column {
        Row {
            DrawShape(shape = CircleShape, Gray,40.dp){
                ShimmerEffectView(
                    isLoading = profilePic == Uri.EMPTY,
                    shape = CircleShape
                ){
                    AsyncImage(
                        model = profilePic,
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(modifier = Modifier.size(8.dp))
            Column {
                Row {
                    Text(text = "${usersViewModel.activeUser!!.firstName} ${usersViewModel.activeUser!!.lastName}", color = Color.Black,fontFamily = Montserrat, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.size(4.dp))
                Row {
                    Text(text = usersViewModel.activeUser!!.role,fontFamily = Montserrat, color = Gray, fontSize = 12.sp)
                }
            }
        }
    }
    Column {
        Button(onClick = {
            showLogoutDialog.value = true
        }) {
            Text(text = "LOGOUT")
        }
    }
}

@Composable
fun SectionTitle(title: String){
    Text(text = title,color = Gray, fontFamily = Montserrat,fontWeight = FontWeight.Bold, fontSize = 13.sp)
}

@Composable
fun CategoriesList(context: Context,categoriesViewModel: CategoriesViewModel,subcategoriesViewModel: SubcategoriesViewModel,productsViewModel: ProductsViewModel,navController: NavController){

    LazyRow(modifier = Modifier
        .padding(start = 4.dp, end = 4.dp)) {
        itemsIndexed(categoriesViewModel.categories){
            index,category ->

            val subcategories = subcategoriesViewModel.getSubCategoriesWithMainCategoryID(category.categoryId)
            val productCount = productsViewModel.getProductsCount(subcategories)

            CompositionLocalProvider(LocalRippleTheme provides BlueRippleTheme) {
                Button(
                    onClick = {
                        val id: Long = category.categoryId
                        navController.navigate(Screen.CategoriesScreen.route + "/$id")
                    },
                    modifier = Modifier
                        .shadow(
                            elevation = 1.dp,
                            shape = RoundedCornerShape(35.dp),
                            ambientColor = DefaultShadowColor,
                            spotColor = Color(0x3F000000)
                        )
                        .width(190.dp)
                        .height(70.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF4F4F4))
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(modifier = Modifier.fillMaxSize(),horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                            if (category.icon != null){
                                DrawShape(shape = CircleShape, color = Color.White,50.dp) {
                                    if (
                                        category.getIconID(context,"Blue") != null
                                    ){
                                        Image(
                                            painter = painterResource(category.getIconID(context,"Blue")!!),
                                            contentDescription = "Category Icon"
                                        )
                                    }
                                }
                            }
                            else{
                                DrawShape(shape = CircleShape, color = Color.White,50.dp){}
                            }
                            Spacer(modifier = Modifier.size(10.dp))
                            Column (modifier = Modifier.height(39.dp),horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.SpaceBetween){
                                Text(text = category.categoryName, color = Color.Black ,fontFamily = Montserrat,fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text(text = "Products $productCount", color = Gray ,fontFamily = Montserrat,fontWeight = FontWeight.Normal, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.size(12.dp))
            if (index == (categoriesViewModel.categories.size-1)){
                CompositionLocalProvider(LocalRippleTheme provides BlackRippleTheme) {
                    Button(
                        onClick = { navController.navigate(Screen.CategoriesScreen.route) },
                        modifier = Modifier
                            .width(172.dp)
                            .height(62.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x00F8F8F8))
                    ) {
                        Text(text = "Show All", color = Color.Black ,fontFamily = Montserrat,fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Icon(Icons.Filled.KeyboardArrowRight , contentDescription = "Right Arrow", tint = Color.Black)
                    }
                }
                Spacer(modifier = Modifier.size(12.dp))
            }
        }

    }

}

@Composable
fun AlterationsList(alterationsViewModel: AlterationsViewModel,productsViewModel: ProductsViewModel,usersViewModel: UsersViewModel,commentsViewModel: CommentsViewModel,navController: NavController){


    if(alterationsViewModel.homeScreenAlterations.isNotEmpty()){
        LazyColumn(modifier = Modifier.fillMaxSize()){
            items(alterationsViewModel.homeScreenAlterations){
                alteration ->
                Row (
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Column {
                        //Image
                        DrawShape(shape = CircleShape, color = Color.Transparent,size = 40.dp){
                            val product: Product? = productsViewModel.getProductWithId(alteration.productId)
                            if (product?.imageUrl != null) {
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
                                    contentDescription = "No Image For Product ${product?.productName}",
                                    modifier = Modifier
                                        .fillMaxSize()
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Column(modifier = Modifier.width(200.dp), horizontalAlignment = Alignment.Start) {
                        Row {
                            // Product Name
                            val product: Product? = productsViewModel.getProductWithId(alteration.productId)
                            if (product != null) {
                                Text(text = product.productName,color = Color.Black, fontFamily = Montserrat,fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                        Spacer(modifier = Modifier.size(5.dp))
                        Row {
                            // Description of alteration
                            var action: String
                            var prefix: String
                            if(alteration.type == "Addition"){
                                action = "added"
                                prefix = "to"
                            }
                            else{
                                action = "removed"
                                prefix = "from"
                            }
                            val user: User? = usersViewModel.getUserWithID(alteration.userId)
                            if(user != null){
                                Text(text = "${user.firstName} $action ${alteration.amount} $prefix the shelf ",color = Gray, fontFamily = Montserrat,fontWeight = FontWeight.Normal, fontSize = 10.sp)
                            }
                        }
                    }
                    Column {
                        // Collapsable menu
                        var expanded by remember { mutableStateOf(false) }
                        IconButton(onClick = {
                            expanded = true
                        }) {
                            Icon(Icons.Filled.MoreVert , contentDescription = "More Vert", tint = Color.Black)
                        }
                        DropdownMenu(modifier = Modifier.background(Color.White),expanded = expanded, onDismissRequest = {
                            expanded = false
                        }) {
                            DropdownMenuItem(
                                text = { Text(text = "More Details") },
                                onClick = {
                                    expanded = false
                                    navController.navigate(Screen.HistoryScreen.route + "/${alteration.id}")
                                })
                            val user: User? = usersViewModel.getUserWithID(alteration.userId)
                            if (user?.userId == usersViewModel.activeUser!!.userId){
                                DropdownMenuItem(
                                    text = { Text(text = "Undo") },
                                    onClick = {
                                        alterationsViewModel.undoAlteration(alteration)
                                        commentsViewModel.deleteAlterationComments(alteration.id)
                                        val product: Product? = productsViewModel.getProductWithId(alteration.productId)
                                        productsViewModel.undoAlteration(product,alteration.amount,alteration.type)
                                        expanded = false
                                    })
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.size(10.dp))
            }
        }
    }
    else{
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                Column {
                    Text(
                        text = "No available history",
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