package gr.nektariostop.ergasiaadvancedhci

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import gr.nektariostop.ergasiaadvancedhci.data.Product
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Blue
import gr.nektariostop.ergasiaadvancedhci.ui.theme.ErgasiaAdvancedHCITheme
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Gray
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Montserrat

class MainActivity : ComponentActivity() {


//    @RequiresApi(Build.VERSION_CODES.R)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scanViewModel: ScanViewModel = viewModel()
            val categoriesViewModel: CategoriesViewModel = viewModel()
            val alterationsViewModel: AlterationsViewModel = viewModel()
            val productsViewModel: ProductsViewModel = viewModel()
            val usersViewModel: UsersViewModel = viewModel()
            val subcategoriesViewModel: SubcategoriesViewModel = viewModel()
            val commentsViewModel: CommentsViewModel = viewModel()
            val navController: NavHostController = rememberNavController()

            ErgasiaAdvancedHCITheme {
                LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

                val context = LocalContext.current



                var hasNotificationPermission by remember {
                    mutableStateOf(
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    )
                }

                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = {
                        isGranted ->
                        hasNotificationPermission = isGranted
                    }
                )






                if (alterationsViewModel.showNotification){
                    showNotification()
                    alterationsViewModel.showNotification = false
                }

                if(scanViewModel.textResultState != ""){
                    Dialog(
                        onDismissRequest = { scanViewModel.textResultState = "" },
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
                            val productFound: Product? = productsViewModel.getProductWithBarcode(scanViewModel.textResultState)

                            Row(
                                modifier = Modifier
                                    .padding(12.dp, 0.dp)
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.5f)
                            ) {
                                if (productFound != null){
                                    Text(text = "Are you sure you want to edit the product ${productFound.productName}?", textAlign = TextAlign.Center)
                                }
                                else{
                                    Text(text = "No product was found.", textAlign = TextAlign.Center)
                                }
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
                                        scanViewModel.textResultState = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                ) {
                                    Text(text = "CLOSE", color = Color.Black,fontFamily = Montserrat,fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                if (productFound != null){
                                    Spacer(modifier = Modifier.size(8.dp))
                                    Button(
                                        onClick = {
                                            scanViewModel.textResultState = ""
                                            val productId = productFound.productId
                                            val subCategory = subcategoriesViewModel.getSingleSubCategory(productFound.subCategoryId)
                                            val subCategoryID = subCategory!!.subCategoryId
                                            val mainCategoryID = categoriesViewModel.getCategoryWithID(subCategory.parentCategoryId)!!.categoryId
                                            navController.navigate(Screen.CategoriesScreen.route + "/$mainCategoryID/$subCategoryID/$productId")
                                        }
                                    ) {
                                        Text(text = "CONFIRM",fontFamily = Montserrat,fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }

                                }
                            }

                        }
                    }
                }
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                when (val currentRoute = navBackStackEntry?.destination?.route) {
                    currentRoute ->

                    if (currentRoute == Screen.LoginScreen.route){
                        Navigation(null,scanViewModel,categoriesViewModel,alterationsViewModel,productsViewModel,usersViewModel,subcategoriesViewModel,commentsViewModel,navController)
                    }
                    else{
                        SideEffect {
                            if(!hasNotificationPermission){
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                        AppContainer(scanViewModel,categoriesViewModel,alterationsViewModel,productsViewModel,usersViewModel,subcategoriesViewModel,commentsViewModel,navController)
                    }
                }
            }
        }
    }

    fun showNotification(){
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(
            applicationContext,
            "alteration"
        )
            .setContentText("The alteration was successful!")
            .setContentTitle("Alteration Done")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        notificationManager.notify(1,notification)

    }

}


@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContainer(
    scanViewModel: ScanViewModel,
    categoriesViewModel: CategoriesViewModel,
    alterationsViewModel: AlterationsViewModel,
    productsViewModel: ProductsViewModel,
    usersViewModel: UsersViewModel,
    subcategoriesViewModel: SubcategoriesViewModel,
    commentsViewModel: CommentsViewModel,
    navController: NavHostController){

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    var homeTextColor: Color = Gray
    var categoriesTextColor: Color = Gray
    var historyTextColor: Color = Gray
    var settingsTextColor: Color = Gray

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = stringResource(id = R.string.logo_content_description),
                    )
                }
            )
        },
        floatingActionButton =
        {
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

                    if(permissions[Manifest.permission.CAMERA] == false){
                        val rationalRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                            context as MainActivity,
                            Manifest.permission.CAMERA
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

            val singleProductRoute = Regex("^categories-screen/.*/.*/.*$")
            val alterationRoute = Regex("^history-screen/.*$")


            when (val currentRoute = navBackStackEntry?.destination?.route) {
                currentRoute ->
                if(currentRoute != null){
                    AnimatedVisibility(
                        visible = currentRoute != Screen.HomeScreen.route && currentRoute != Screen.SettingsScreen.route && currentRoute != Screen.ProfileSettingsScreen.route && !currentRoute.matches(singleProductRoute) && !currentRoute.matches(alterationRoute) ,
                        enter = slideInHorizontally(animationSpec = tween(durationMillis = 500)) {
                            fullWidth ->
                            fullWidth
                        }.plus(fadeIn(animationSpec = tween(durationMillis = 500))),
                        exit = slideOutHorizontally(animationSpec = tween(durationMillis = 500)) {
                                fullWidth ->
                            fullWidth
                        }.plus(fadeOut(animationSpec = tween(durationMillis = 500)))
                    ) {
                        FloatingActionButton(
                            onClick = { requestPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.CAMERA
                                )
                            )},
                            containerColor = Blue,) {
                            Image(
                                painter = painterResource(id = R.drawable.barcode_icon_white),
                                contentDescription = " Barcode Icon"
                            )
                        }
                    }

                }
            }
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .padding(0.dp)
                    .drawBehind {
                        val strokeWidth = 2f
                        val x = size.width - strokeWidth
                        val y = size.height - strokeWidth

                        drawLine(
                            color = Gray,
                            start = Offset(0f, 0f),
                            end = Offset(x, 0f),
                            strokeWidth = strokeWidth
                        )
                    },
                actions = {
                            when (val currentRoute = navBackStackEntry?.destination?.route){
                                currentRoute ->

                                Row (
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(0.dp)
                                    ){
                                    Column(modifier = Modifier
                                        .fillMaxWidth(0.25f)
                                        .fillMaxHeight()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null,
                                            onClick = {
                                                navController.navigate(Screen.HomeScreen.route)
                                            }
                                        ), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                        Row {
                                            if (currentRoute == "home-screen") {
                                                homeTextColor = Color.Black
                                                Image(painter = painterResource(id = R.drawable.home_icon_black), contentDescription = "Home Icon Black")
                                            }
                                            else{
                                                homeTextColor = Gray
                                                Image(painter = painterResource(id = R.drawable.home_icon_gray), contentDescription = "Home Icon Gray")
                                            }
                                        }
                                        Row {
                                            Text(text = "HOME",fontFamily = Montserrat, color = homeTextColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                    Column(modifier = Modifier
                                        .fillMaxWidth(0.4f)
                                        .fillMaxHeight()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null,
                                            onClick = {
                                                navController.navigate(Screen.CategoriesScreen.route)
                                            }
                                        ), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                        Row {
                                            if (currentRoute!=null && (currentRoute == "categories-screen" || currentRoute.contains("categories-screen"))){
                                                categoriesTextColor = Color.Black
                                                Image(painter = painterResource(id = R.drawable.categories_icon_black), contentDescription = "Categories Icon Black")
                                            }
                                            else{
                                                categoriesTextColor = Gray
                                                Image(painter = painterResource(id = R.drawable.categories_icon_gray), contentDescription = "Categories Icon Gray")
                                            }
                                        }
                                        Row {
                                            Text(text = "CATEGORIES",fontFamily = Montserrat, color = categoriesTextColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                    Column(modifier = Modifier
                                        .fillMaxWidth(0.5f)
                                        .fillMaxHeight()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null,
                                            onClick = {
                                                navController.navigate(Screen.HistoryScreen.route)
                                            }
                                        ), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                        Row {
                                            if (currentRoute!=null && (currentRoute == "history-screen" || currentRoute.contains("history-screen"))){
                                                historyTextColor = Color.Black
                                                Image(painter = painterResource(id = R.drawable.history_icon_black), contentDescription = "History Icon Black")
                                            }
                                            else{
                                                historyTextColor = Gray
                                                Image(painter = painterResource(id = R.drawable.history_icon_gray), contentDescription = "History Icon Gray")
                                            }
                                        }
                                        Row {
                                            Text(text = "HISTORY",fontFamily = Montserrat, color = historyTextColor, fontWeight = FontWeight.Bold,fontSize = 12.sp)
                                        }
                                    }
                                    Column(modifier = Modifier
                                        .fillMaxWidth(1f)
                                        .fillMaxHeight()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null,
                                            onClick = {
                                                navController.navigate(Screen.SettingsScreen.route)
                                            }
                                        ), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                        Row {
                                            if (currentRoute == "settings-screen" || currentRoute == Screen.ProfileSettingsScreen.route){
                                                settingsTextColor = Color.Black
                                                Image(painter = painterResource(id = R.drawable.settings_icon_black), contentDescription = "Settings Icon Black")
                                            }
                                            else{
                                                settingsTextColor = Gray
                                                Image(painter = painterResource(id = R.drawable.settings_icon_gray), contentDescription = "Settings Icon Gray")
                                            }
                                        }
                                        Row {
                                            Text(text = "SETTINGS",fontFamily = Montserrat, color = settingsTextColor, fontWeight = FontWeight.Bold,fontSize = 12.sp)
                                        }
                                    }
                                }

                        }
                }
            )
        }
    ) {
            innerPadding ->
        Navigation(innerPadding,scanViewModel,categoriesViewModel,alterationsViewModel,productsViewModel,usersViewModel,subcategoriesViewModel,commentsViewModel,navController)
    }
}

