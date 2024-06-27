package gr.nektariostop.ergasiaadvancedhci

import android.os.Build.VERSION_CODES.Q
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import gr.nektariostop.ergasiaadvancedhci.data.Alteration
import gr.nektariostop.ergasiaadvancedhci.data.Comment
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Blue
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Gray
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Montserrat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryView(
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

    val commentsResult by commentsViewModel.getCommentsResult.observeAsState()


    var startDate by remember {
        mutableStateOf<LocalDate>(LocalDate.now().minusDays(1))
    }

    var endDate by remember {
        mutableStateOf<LocalDate>(LocalDate.now())
    }

    var tempStartDate by remember {
        mutableStateOf<LocalDate?>(null)
    }

    var tempEndDate by remember {
        mutableStateOf<LocalDate?>(null)
    }

    val startDateDialogState = rememberMaterialDialogState()
    val endDateDialogState = rememberMaterialDialogState()

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val filtersExpanded = remember {
        mutableStateOf(false)
    }

    val userID = remember {
        mutableStateOf<String?>(null)
    }
    val mainCatID = remember {
        mutableStateOf<Long?>(null)
    }
    val subCatID = remember {
        mutableStateOf<Long?>(null)
    }

    BackHandler{
        if (filtersExpanded.value){
            filtersExpanded.value = false
        }
        else{
            navController.navigate(Screen.HomeScreen.route)
        }
    }

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

    ExpandableSection(title = "Filters",modifier = Modifier.padding(0.dp,72.dp),filtersExpanded = filtersExpanded) {
        Filters(
            startDate = startDate,
            endDate = endDate,
            formatter = formatter,
            startDateDialogState = startDateDialogState,
            endDateDialogState = endDateDialogState,
            usersViewModel = usersViewModel,
            categoriesViewModel = categoriesViewModel,
            subCategoriesViewModel = subcategoriesViewModel,
            userID = userID,
            mainCatID = mainCatID,
            subCatID = subCatID
        )
    }

    MaterialDialog(
        dialogState = endDateDialogState,
        buttons = {
            positiveButton(text = "SELECT", textStyle = TextStyle(color = Blue)){
                if (tempEndDate != null){
                    endDate = tempEndDate as LocalDate
                }
            }
            negativeButton(text = "CANCEL", textStyle = TextStyle(color = Color.Red))
        }
    ) {
        datepicker(
            initialDate = endDate,
            title = "Pick End Date",
            allowedDateValidator = {
                it.isAfter(startDate)
            },
            colors = com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults.colors(
                headerBackgroundColor = Blue,
                headerTextColor = Color.White,
                dateActiveBackgroundColor = Blue,
                dateActiveTextColor = Color.White
            )
        ){
            tempEndDate = it
        }
    }

    MaterialDialog(
        dialogState = startDateDialogState,
        buttons = {
            positiveButton(text = "OK", textStyle = TextStyle(color = Blue)){
                if (tempStartDate != null){
                    startDate = tempStartDate as LocalDate
                }
            }
            negativeButton(text = "CANCEL", textStyle = TextStyle(color = Color.Red))
        }
    ) {
        datepicker(
            initialDate = startDate,
            title = "Pick Start Date",
            allowedDateValidator = {
                it.isBefore(endDate)
            },
            colors = com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults.colors(
                headerBackgroundColor = Blue,
                headerTextColor = Color.White,
                dateActiveBackgroundColor = Blue,
                dateActiveTextColor = Color.White
            )
        ){
            tempStartDate = it
        }
    }

    AnimatedVisibility(
        visible = !filtersExpanded.value,
        enter = slideInVertically(animationSpec = tween(durationMillis = 500)) {
            fullHeight ->
            fullHeight
        },
        exit = slideOutVertically(animationSpec = tween(durationMillis = 500)) {
            fullHeight ->
            fullHeight
        }

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally){



            Row {
                if (alterationsViewModel.waitingForDeletion){
                    CircularProgressIndicator(color = Blue)
                }
                else{
                    AlterationsList(
                        alterationsViewModel,
                        startDate,
                        endDate,
                        userID.value,
                        mainCatID.value,
                        subCatID.value,
                        navController,
                        productsViewModel,
                        usersViewModel,
                        categoriesViewModel,
                        subcategoriesViewModel
                    )
                }
            }
        }
    }

}

@RequiresApi(Q)
@Composable
fun AlterationsList(alterationsViewModel: AlterationsViewModel,startDate: LocalDate,endDate: LocalDate,userID: String?,mainCatID: Long?,subCatID: Long?,navController: NavController,productsViewModel: ProductsViewModel,usersViewModel: UsersViewModel,categoriesViewModel: CategoriesViewModel,subcategoriesViewModel: SubcategoriesViewModel){
    if(alterationsViewModel.filterAlterations(startDate, endDate, userID,mainCatID, subCatID,productsViewModel, categoriesViewModel, subcategoriesViewModel).isNotEmpty()){
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp, 130.dp, 24.dp, 72.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ){
            items(alterationsViewModel.filterAlterations(startDate, endDate, userID, mainCatID, subCatID,productsViewModel, categoriesViewModel, subcategoriesViewModel)){
                alteration ->
                    AlterationsListItem(alteration,navController,productsViewModel,usersViewModel)
                    Spacer(modifier = Modifier.size(12.dp))
            }
        }
    }
    else{
        Column{
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

@RequiresApi(Q)
@Composable
fun AlterationsListItem(alteration: Alteration,navController: NavController,productsViewModel: ProductsViewModel,usersViewModel: UsersViewModel){

    val months = listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")

    val day = alteration.date.dayOfWeek.toString().lowercase().replaceFirstChar { it.uppercaseChar() }

    val chipColor: Color =
        if (alteration.type == "Addition"){
            colorResource(id = R.color.chipGreen)
        }
        else{
            colorResource(id = R.color.chipRed)
        }

    val product = productsViewModel.getProductWithId(alteration.productId)
    val user = usersViewModel.getUserWithID(alteration.userId)

    Row(
        modifier = Modifier
            .advancedShadow(
                alpha = 0.08f,
                cornersRadius = 12.dp,
                shadowBlurRadius = 5.dp
            )
            .fillMaxWidth()
            .height(230.dp)
            .background(Color.White, RoundedCornerShape(12.dp))

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "$day, ${alteration.date.dayOfMonth} ${months[alteration.date.monthValue]}",fontFamily = Montserrat,color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
                Column {
                    AssistChip(
                        onClick = {},
                        label = { 
                            Text(text = alteration.type,fontFamily = Montserrat,color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = chipColor
                        ),
                        border = AssistChipDefaults.assistChipBorder(
                            borderColor = Color.Transparent,
                            borderWidth = 0.dp,
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.size(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.product_icon_blue),
                        contentDescription = "Box Icon"
                    )
                }
                Spacer(modifier = Modifier.size(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Text(text = "Product",fontFamily = Montserrat,color = Gray, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                    Row {
                        Text(text = product?.productName?: "Product Name",fontFamily = Montserrat,color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.size(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.user_icon_blue),
                        contentDescription = "User Icon"
                    )
                }
                Spacer(modifier = Modifier.size(22.dp))
                Column(
                    modifier = Modifier
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Text(text = "User",fontFamily = Montserrat,color = Gray, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                    Row {
                        Text(text = "${user?.firstName} ${user?.lastName}",fontFamily = Montserrat,color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.size(18.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                              navController.navigate(Screen.HistoryScreen.route + "/${alteration.id}")
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "SHOW MORE",fontFamily = Montserrat, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }
        }
    }
}

@RequiresApi(Q)
fun Modifier.advancedShadow(
    color: Color = Color.Black,
    alpha: Float = 1f,
    cornersRadius: Dp = 0.dp,
    shadowBlurRadius: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp
) = drawBehind {

    val shadowColor = color.copy(alpha = alpha).toArgb()
    val transparentColor = color.copy(alpha = 0f).toArgb()

    drawIntoCanvas {
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = transparentColor
        frameworkPaint.setShadowLayer(
            shadowBlurRadius.toPx(),
            offsetX.toPx(),
            offsetY.toPx(),
            shadowColor
        )
        it.drawRoundRect(
            0f,
            0f,
            this.size.width,
            this.size.height,
            cornersRadius.toPx(),
            cornersRadius.toPx(),
            paint
        )
    }
}

@Composable
fun Filters(
    startDate: LocalDate,
    endDate: LocalDate,
    formatter: DateTimeFormatter,
    startDateDialogState: MaterialDialogState,
    endDateDialogState: MaterialDialogState,
    usersViewModel: UsersViewModel,
    categoriesViewModel: CategoriesViewModel,
    subCategoriesViewModel: SubcategoriesViewModel,
    userID: MutableState<String?>,
    mainCatID: MutableState<Long?>,
    subCatID: MutableState<Long?>,
){


    val username = if(userID.value?.let { usersViewModel.getUserWithID(it) } != null){
        "${usersViewModel.getUserWithID(userID.value!!)?.firstName} ${usersViewModel.getUserWithID(userID.value!!)?.lastName}"
    }
    else{
        null
    }

    val mainCatName = if(mainCatID.value != null){
        categoriesViewModel.getCategoryWithID(mainCatID.value!!)!!.categoryName
    }
    else{
        null
    }

    val subCatName = if(subCatID.value != null){
        subCategoriesViewModel.getSingleSubCategory(subCatID.value!!)!!.subCategoryName
    }
    else{
        null
    }

    var usersDropdownExpaded by remember { mutableStateOf(false) }
    var mainCatDropdownExpaded by remember { mutableStateOf(false) }
    var subCatDropdownExpaded by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf(username) }
    var selectedMainCategory by remember { mutableStateOf(mainCatName) }
    var selectedSubCategory by remember { mutableStateOf(subCatName) }

    val usersIcon = if (!usersDropdownExpaded)
        Icons.Filled.KeyboardArrowDown //it requires androidx.compose.material:material-icons-extended
    else
        Icons.Filled.KeyboardArrowUp

    val mainCatIcon = if (!mainCatDropdownExpaded)
        Icons.Filled.KeyboardArrowDown //it requires androidx.compose.material:material-icons-extended
    else
        Icons.Filled.KeyboardArrowUp

    val subCatIcon = if (!subCatDropdownExpaded)
        Icons.Filled.KeyboardArrowDown //it requires androidx.compose.material:material-icons-extended
    else
        Icons.Filled.KeyboardArrowUp

    var textfieldSize by remember { mutableStateOf(Size.Zero)}


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(Blue, RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ){
            Box(modifier = Modifier
                .fillMaxSize(),
                contentAlignment = Alignment.BottomEnd){
                Image(
                    painter = painterResource(id = R.drawable.background_history_filters1),
                    contentDescription = "Background"
                )
            }
            Box(modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.TopStart){
                Image(
                    painter = painterResource(id = R.drawable.background_history_filters2),
                    contentDescription = "Background",
                    modifier = Modifier
                        .offset((-20).dp,(-20).dp)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Row {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row {
                            Text(text = "Start Date",fontFamily = Montserrat,color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        Row {
                            Column {
                                FloatingActionButton(
                                    onClick = {
                                        startDateDialogState.show()
                                    },
                                    shape = RoundedCornerShape(8.dp,0.dp,0.dp,8.dp),
                                    containerColor = Color.White
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.date_icon),
                                        contentDescription = "Date Icon"
                                    )
                                }
                            }
                            Column {
                                OutlinedTextField(
                                    value = startDate.format(formatter),
                                    onValueChange = { },
                                    shape = RoundedCornerShape(0.dp,8.dp,8.dp,0.dp),
                                    readOnly = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.White,
                                        unfocusedBorderColor = Color.White,
                                        disabledBorderColor = Color.White,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        disabledTextColor = Color.White,
                                    ),
                                )
                            }
                        }
                        Spacer(modifier = Modifier.size(12.dp))
                        Row {
                            Text(text = "End Date",fontFamily = Montserrat,color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        Row {
                            Column {
                                FloatingActionButton(
                                    onClick = {
                                        endDateDialogState.show()
                                    },
                                    shape = RoundedCornerShape(8.dp,0.dp,0.dp,8.dp),
                                    containerColor = Color.White
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.date_icon),
                                        contentDescription = "Date Icon"
                                    )
                                }
                            }
                            Column {
                                OutlinedTextField(
                                    value = endDate.format(formatter),
                                    onValueChange = { },
                                    readOnly = true,
                                    shape = RoundedCornerShape(0.dp,8.dp,8.dp,0.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.White,
                                        unfocusedBorderColor = Color.White,
                                        disabledBorderColor = Color.White,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        disabledTextColor = Color.White,
                                    ),
                                )
                            }
                        }
                        Spacer(modifier = Modifier.size(12.dp))
                        Row {
                            Text(text = "Select User",fontFamily = Montserrat,color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        Row {
                            Box{
                                OutlinedTextField(
                                    value = selectedUser?: "Select User",
                                    onValueChange = { },
                                    trailingIcon = {
                                        Icon(
                                            usersIcon,"contentDescription",
                                            Modifier.clickable {
                                                usersDropdownExpaded = !usersDropdownExpaded
                                            },
                                            tint = Color.White
                                        )
                                    },
                                    readOnly = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.White,
                                        unfocusedBorderColor = Color.White,
                                        disabledBorderColor = Color.White,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        disabledTextColor = Color.White,
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onGloballyPositioned { coordinates ->
                                            //This value is used to assign to the DropDown the same width
                                            textfieldSize = coordinates.size.toSize()
                                        },
                                )
                                DropdownMenu(
                                    expanded = usersDropdownExpaded,
                                    onDismissRequest = { usersDropdownExpaded = false },
                                    modifier = Modifier
                                        .width(with(LocalDensity.current){textfieldSize.width.toDp()})
                                ) {
                                    usersViewModel.getUsers().forEach { user ->
                                        DropdownMenuItem(
                                            text = { Text(text = "${user.firstName} ${user.lastName}") },
                                            onClick = {
                                                userID.value = user.userId
                                                selectedUser = "${user.firstName} ${user.lastName}"
                                                usersDropdownExpaded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.size(12.dp))
                        Row {
                            Text(text = "Select Main Category",fontFamily = Montserrat,color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        Row {
                            Box{
                                OutlinedTextField(
                                    value = selectedMainCategory?: "Select Main Category",
                                    onValueChange = { },
                                    trailingIcon = {
                                        Icon(
                                            mainCatIcon,"contentDescription",
                                            Modifier.clickable {
                                                mainCatDropdownExpaded = !mainCatDropdownExpaded
                                            },
                                            tint = Color.White
                                        )
                                    },
                                    readOnly = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.White,
                                        unfocusedBorderColor = Color.White,
                                        disabledBorderColor = Color.White,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        disabledTextColor = Color.White,
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onGloballyPositioned { coordinates ->
                                            //This value is used to assign to the DropDown the same width
                                            textfieldSize = coordinates.size.toSize()
                                        },
                                )
                                DropdownMenu(
                                    expanded = mainCatDropdownExpaded,
                                    onDismissRequest = { mainCatDropdownExpaded = false },
                                    modifier = Modifier
                                        .width(with(LocalDensity.current){textfieldSize.width.toDp()})
                                ) {
                                    categoriesViewModel.categories.forEach { cat ->
                                        DropdownMenuItem(
                                            text = { Text(text = cat.categoryName) },
                                            onClick = {
                                                mainCatID.value = cat.categoryId
                                                selectedMainCategory = cat.categoryName
                                                selectedSubCategory = null
                                                mainCatDropdownExpaded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.size(12.dp))
                        Row {
                            Text(text = "Select Sub Category",fontFamily = Montserrat,color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        Row {
                            Box{
                                OutlinedTextField(
                                    value = selectedSubCategory?: "Select Sub Category",
                                    onValueChange = { },
                                    trailingIcon = {
                                        Icon(
                                            subCatIcon,"contentDescription",
                                            Modifier.clickable {
                                                if(mainCatID.value != null){
                                                    subCatDropdownExpaded = !subCatDropdownExpaded
                                                }
                                            },
                                            tint = if(mainCatID.value != null){ Color.White } else { Color(0x3fFFFFFF) }
                                        )
                                    },
                                    readOnly = true,
                                    enabled = mainCatID.value != null,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.White,
                                        unfocusedBorderColor = Color.White,
                                        disabledBorderColor = Color(0x3fFFFFFF),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        disabledTextColor = Color(0x3fFFFFFF),
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onGloballyPositioned { coordinates ->
                                            //This value is used to assign to the DropDown the same width
                                            textfieldSize = coordinates.size.toSize()
                                        },
                                )
                                DropdownMenu(
                                    expanded = subCatDropdownExpaded,
                                    onDismissRequest = { subCatDropdownExpaded = false },
                                    modifier = Modifier
                                        .width(with(LocalDensity.current){textfieldSize.width.toDp()})
                                ) {
                                    subCategoriesViewModel.getSubCategoriesWithMainCategoryID(mainCatID.value!!).forEach { cat ->
                                        DropdownMenuItem(
                                            text = { Text(text = cat.subCategoryName) },
                                            onClick = {
                                                subCatID.value = cat.subCategoryId
                                                selectedSubCategory = cat.subCategoryName
                                                subCatDropdownExpaded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun ExpandableSectionTitle(modifier: Modifier = Modifier, isExpanded: Boolean, title: String) {

    val icon = if (isExpanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown

    Row(modifier = modifier
        .padding(8.dp)
        .fillMaxWidth()
        .drawBehind {
            val strokeWidth = 4f
            val x = size.width - strokeWidth
            val y = size.height - strokeWidth + 26

            drawLine(
                color = Gray,
                start = Offset(0f, y),
                end = Offset(x, y),
                strokeWidth = strokeWidth
            )
        }
        , verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = title,fontFamily = Montserrat,color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Image(
            modifier = Modifier.size(32.dp),
            imageVector = icon,
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer),
            contentDescription = "Expand or collapse icon"
        )
    }
}

@Composable
fun ExpandableSection(
    modifier: Modifier = Modifier,
    title: String,
    filtersExpanded: MutableState<Boolean>,
    content: @Composable () -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = modifier
            .clickable {
                isExpanded = !isExpanded
                filtersExpanded.value = !filtersExpanded.value
            }
            .background(color = Color.White)
            .fillMaxWidth()
    ) {
        ExpandableSectionTitle(isExpanded = filtersExpanded.value, title = title)

        AnimatedVisibility(
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth(),
            visible = filtersExpanded.value
        ) {
            content()
        }
    }
}