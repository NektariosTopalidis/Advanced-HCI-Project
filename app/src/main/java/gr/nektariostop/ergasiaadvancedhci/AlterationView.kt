package gr.nektariostop.ergasiaadvancedhci

import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import gr.nektariostop.ergasiaadvancedhci.data.Comment
import gr.nektariostop.ergasiaadvancedhci.data.Product
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Blue
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Gray
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Montserrat
import java.time.LocalDateTime
import java.util.Timer
import kotlin.concurrent.schedule

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AlterationView(
    alterationID: Long,
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

    val addCommentResult by commentsViewModel.addCommentsResult.observeAsState()

    when (addCommentResult){
        is Result.Success<*> -> {

        }

        is Result.Error -> {

        }

        else -> {

        }
    }


    val alteration = alterationsViewModel.getAlterationWithID(alterationID)
    val months = listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")

    var product: Product? = null
    var prefixOfAmount: String = ""
    var day: String = ""

    if (alteration != null){
        product = productsViewModel.getProductWithId(alteration.productId)
        prefixOfAmount = if (alteration.type == "Addition"){
                                "+"
                            }
                            else{
                                "-"
                            }

        day = alteration.date.dayOfWeek.toString().lowercase().replaceFirstChar { it.uppercaseChar() }
    }





    val commentsExpanded = remember {
        mutableStateOf(false)
    }

    var commentText by remember {
        mutableStateOf("")
    }

    var showUndoDialog by remember {
        mutableStateOf(false)
    }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    BackHandler{
        if (commentsExpanded.value){
            commentsExpanded.value = false
        }
        else{
            navController.navigate(Screen.HistoryScreen.route)
        }
    }

    if(showUndoDialog){
        Dialog(
            onDismissRequest = { showUndoDialog = false },
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
                        text = "Are you sure you want undo this alteration?",
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
                            showUndoDialog = false
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
                            alterationsViewModel.waitingForDeletion = true
                            if (alteration  != null){
                                commentsViewModel.deleteAlterationComments(alterationID)
                                productsViewModel.undoAlteration(product,alteration.amount,alteration.type)
                                Toast.makeText(context,"Alteration was undone successfully",Toast.LENGTH_SHORT).show()
                                navController.navigate(Screen.HistoryScreen.route)
                                Timer().schedule(500){
                                    alterationsViewModel.undoAlteration(alteration)
                                    alterationsViewModel.waitingForDeletion = false
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp, 64.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally){


        AnimatedVisibility(
            visible = !commentsExpanded.value
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    if (product != null) {
                        Text(text = product.productName,fontFamily = Montserrat,color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 24.sp, textAlign = TextAlign.Center)
                    }
                }
                Spacer(modifier = Modifier.size(18.dp))
                Row(
                    modifier = Modifier
                        .background(Blue, CircleShape)
                        .size(120.dp)
                        .padding(8.dp, 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (alteration != null) {
                        Text(text = "${prefixOfAmount}${alteration.amount}",fontFamily = Montserrat,color = Color.White, fontSize = 32.sp)
                    }
                }
                Spacer(modifier = Modifier.size(18.dp))
                Row {
                    Column {
                        CompositionLocalProvider(LocalRippleTheme provides BlackRippleTheme) {
                            if (alteration != null) {
                                Button(
                                    onClick = {
                                        showUndoDialog = true
                                    },
                                    modifier = if (alteration.userId == usersViewModel.activeUser!!.userId) {
                                        Modifier
                                            .advancedShadow(
                                                alpha = 0.05f,
                                                cornersRadius = 12.dp,
                                                shadowBlurRadius = 5.dp
                                            )
                                    } else {
                                        Modifier
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        disabledContainerColor = Color(255,255,255,0xF0),
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = alteration.userId == usersViewModel.activeUser!!.userId
                                ) {
                                    if (alteration.userId == usersViewModel.activeUser!!.userId) {
                                        Image(
                                            painter = painterResource(id = R.drawable.undo_icon),
                                            contentDescription = "Undo Icon"
                                        )
                                    } else{
                                        Image(
                                            painter = painterResource(id = R.drawable.undo_icon_gray),
                                            contentDescription = "Undo Icon",
                                            modifier = Modifier
                                                .alpha(0.5f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.size(4.dp))
                                    Text(
                                        text = "UNDO",
                                        fontFamily = Montserrat,
                                        color = if (alteration.userId == usersViewModel.activeUser!!.userId) {
                                            Color(0xFFFF0000)
                                        } else{
                                            Gray
                                        },
                                        modifier = if (alteration.userId == usersViewModel.activeUser!!.userId){
                                            Modifier
                                        } else{
                                            Modifier.alpha(0.5f)
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Column {
                        Button(
                            onClick = { /*TODO*/ },
                            modifier = Modifier
                                    .advancedShadow(
                                        alpha = 0.05f,
                                        cornersRadius = 12.dp,
                                        shadowBlurRadius = 5.dp
                                    ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                            ),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            if (alteration != null) {
                                Text(
                                    text = alteration.type.uppercase(),
                                    fontFamily = Montserrat,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.size(18.dp))
                Row {
                    Column {
                        Text(text = "Stock after update: ",fontFamily = Montserrat,color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, textAlign = TextAlign.Center)
                    }
                    Spacer(modifier = Modifier.size(4.dp))
                    Column {
                        if (alteration != null) {
                            Text(text = alteration.stockAfterUpdate.toString(),fontFamily = Montserrat,color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
                Spacer(modifier = Modifier.size(18.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(unbounded = true)
                        .background(Color(0xFFF2F2F2), RoundedCornerShape(8.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Date",fontFamily = Montserrat,color = Color.Black, fontSize = 14.sp)
                            }
                            Column {
                                if (alteration != null) {
                                    Text(text = "$day, ${alteration.date.dayOfMonth} ${months[alteration.date.monthValue]}",fontFamily = Montserrat,color = Color.Black, fontSize = 14.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.size(14.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "User",fontFamily = Montserrat,color = Color.Black, fontSize = 14.sp)
                            }
                            Column {
                                if (alteration != null) {
                                    Text(text = "${usersViewModel.getUserWithID(alteration.userId)!!.firstName} ${usersViewModel.getUserWithID(alteration.userId)!!.lastName}",fontFamily = Montserrat,color = Color.Black, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.size(18.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.9f)
                        .background(Color(0xFFF2F2F2), RoundedCornerShape(8.dp))
                ){
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Comments",
                                            fontFamily = Montserrat,
                                            color = Color.Black,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Column {
                                        IconButton(onClick = { commentsExpanded.value = true }, colors = IconButtonDefaults.iconButtonColors(
                                            containerColor = Color.White
                                        )) {
                                            Icon(imageVector = Icons.Filled.KeyboardArrowUp, contentDescription = "Arrow",modifier = Modifier.rotate(90f))
                                        }
                                    }
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .padding(0.dp, 12.dp)
                                ) {
                                    CommentsList(alterationID,2,commentsViewModel,usersViewModel)
                                }
                            }
                        }
            }
        }
        AnimatedVisibility(
            visible = commentsExpanded.value,
            enter = slideInVertically(animationSpec = tween(durationMillis = 500)) {
                    fullHeight ->
                fullHeight
            },
            exit = slideOutVertically(animationSpec = tween(durationMillis = 500)) {
                0
            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp, 32.dp)
                    .background(Color(0xFFF2F2F2), RoundedCornerShape(8.dp))
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                        .fillMaxHeight()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Comments",
                                fontFamily = Montserrat,
                                color = Color.Black,
                                fontSize = 12.sp
                            )
                        }
                        Column {
                            IconButton(onClick = { commentsExpanded.value = false }, colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.White
                            )) {
                                Icon(imageVector = Icons.Filled.KeyboardArrowUp, contentDescription = "Arrow",modifier = Modifier.rotate(-90f))
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.7f)
                            .padding(0.dp, 12.dp)
                    ) {
                        CommentsList(alterationID,null,commentsViewModel,usersViewModel)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.3f)
                    ) {

                        OutlinedTextField(
                            value = commentText,
                            onValueChange = {
                                commentText = it
                            },
                            maxLines = 5,
                            placeholder = {
                                Text(
                                    text = "Comment",
                                    fontFamily = Montserrat,
                                )
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White)
                                .focusRequester(focusRequester),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Gray,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Gray
                            ),
                            trailingIcon = {
                                Box(modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(8.dp), contentAlignment = Alignment.BottomEnd){
                                    IconButton(
                                        onClick = {
                                            focusManager.clearFocus()
                                            val comment = Comment(0,alterationID,usersViewModel.activeUser!!.userId,commentText,LocalDateTime.now())
                                            commentsViewModel.addComment(comment)
                                            commentText = ""
                                        },
                                        enabled = commentText.isNotEmpty(),
                                        colors = IconButtonDefaults.iconButtonColors(
                                            containerColor = Blue,
                                            disabledContainerColor = Color(0x3F3CA1FF)
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Send,
                                            contentDescription = "Send Icon",
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        )

                    }
                }
            }
        }
    }
}

@Composable
fun CommentsList(alterationID: Long,numberOfComments: Int?,commentsViewModel: CommentsViewModel,usersViewModel: UsersViewModel){

    if (commentsViewModel.getAlterationComments(alterationID,numberOfComments).isNotEmpty()){
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ){
            items(commentsViewModel.getAlterationComments(alterationID,numberOfComments)){
                comment ->
                CommentsListItem(comment, usersViewModel)
                Spacer(modifier = Modifier.size(8.dp))
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
                        text = "No available comments",
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
fun CommentsListItem(comment: Comment,usersViewModel: UsersViewModel){

    val months = listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")

    val username = if (comment.userId == usersViewModel.activeUser!!.userId){
        "You"
    }
    else{
        "${usersViewModel.getUserWithID(comment.userId)!!.firstName} ${usersViewModel.getUserWithID(comment.userId)!!.lastName}"
    }

    val usernameColor: Color = if (comment.userId == usersViewModel.activeUser!!.userId){
        Blue
    }
    else{
        Gray
    }

    val dateTime = "${comment.date.dayOfMonth}  ${months[comment.date.monthValue]}, ${comment.date.hour}:${comment.date.minute}"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(unbounded = true)
            .background(Color.White, RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            DrawShape(shape = CircleShape, color = Gray, size = 30.dp) {
                                var profilePic by remember {
                                    mutableStateOf<Uri>(Uri.EMPTY)
                                }

                                var storageRef = Firebase.storage.reference


                                storageRef.child("images/${comment.userId}.jpg").downloadUrl
                                    .addOnSuccessListener {
                                            uri ->
                                        profilePic = uri
                                    }
                                    .addOnFailureListener {
                                        println("FAILED")
                                    }
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
                        Spacer(modifier = Modifier.size(4.dp))
                        Column {
                            Text(text = username, color = usernameColor, fontSize = 12.sp)
                        }
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = dateTime, color = Gray, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.size(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = comment.commentText, color = Color.Black, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.size(8.dp))
        }
    }

}
