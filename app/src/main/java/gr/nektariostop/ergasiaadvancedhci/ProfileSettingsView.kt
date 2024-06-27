package gr.nektariostop.ergasiaadvancedhci

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Blue
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Gray
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Montserrat
import gr.nektariostop.ergasiaadvancedhci.util.StorageUtil

@Composable
fun ProfileSettingsView(
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

    val updateActiveUserResult by usersViewModel.updateActiveUserResult.observeAsState()

    var profilePic by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    var imagePicked by remember {
        mutableStateOf(false)
    }

    var selectedImage by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }


    // Create a storage reference from our app
    var storageRef = Firebase.storage.reference
    storageRef.child("images/${usersViewModel.activeUser!!.userId}.jpg").downloadUrl
        .addOnSuccessListener {
                uri ->
            if (!imagePicked){
                println("LOADED IMAGE")
                profilePic = uri
                selectedImage = uri
            }
        }


    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()){
        uri ->
        if (uri != null) {
            println("PICKED IMAGE")
            selectedImage = uri
            imagePicked = true
        }
        else{
            imagePicked = false
        }
    }

    var firstName by remember {
        mutableStateOf(usersViewModel.activeUser!!.firstName)
    }

    var lastName by remember {
        mutableStateOf(usersViewModel.activeUser!!.lastName)
    }

    var email by remember {
        mutableStateOf(usersViewModel.activeUser!!.email)
    }

    var gender by remember {
        mutableStateOf(usersViewModel.activeUser!!.gender)
    }

    var gendersDropdownExpaded by remember { mutableStateOf(false) }

    val gendersIcon = if (!gendersDropdownExpaded)
        Icons.Filled.KeyboardArrowDown //it requires androidx.compose.material:material-icons-extended
    else
        Icons.Filled.KeyboardArrowUp

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    var textfieldSize by remember { mutableStateOf(Size.Zero)}

    val showCancelDialog = remember {
        mutableStateOf(false)
    }

    val showConfirmDialog = remember {
        mutableStateOf(false)
    }

    var isWaitingForUpdate by remember {
        mutableStateOf(false)
    }


    when (updateActiveUserResult){
        is Result.Success -> {
            profilePic = selectedImage
            isWaitingForUpdate = false
            Toast.makeText(context,"Profile was successfully updated",Toast.LENGTH_SHORT).show()
            usersViewModel.clearActiveUserResult()
        }

        is Result.Error -> {
            isWaitingForUpdate = false
            Toast.makeText(context,"There was an error.",Toast.LENGTH_SHORT).show()
        }

        is Result.DoNothing -> {

        }

        else -> {

        }
    }

    BackHandler{
        if ((selectedImage.toString() != profilePic.toString()) || (firstName != usersViewModel.activeUser!!.firstName) || (lastName != usersViewModel.activeUser!!.lastName) || (gender != usersViewModel.activeUser!!.gender) || (email != usersViewModel.activeUser!!.email)){
            showCancelDialog.value = true
        }
        else{
            navController.navigate(Screen.SettingsScreen.route)
        }
    }

    if (isWaitingForUpdate){
        Dialog(onDismissRequest = { /*TODO*/ }) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(48.dp), strokeWidth = 3.dp)
            }
        }
    }

    ActionDialog(
        show = showConfirmDialog,
        message = "Are you sure you want to change you profile information?",
        onCancel = {},
        onConfirm = {
            if (selectedImage.toString() != profilePic.toString()){
                val storage = Firebase.storage
                val listRef = storage.reference.child("images/${usersViewModel.activeUser!!.userId}.jpg")
                listRef.delete()
                StorageUtil.uploadToStorage(selectedImage,context,"image",usersViewModel.activeUser!!.userId)
            }

            usersViewModel.updateActiveUser(firstName,lastName,email,gender)
            showConfirmDialog.value = false
            isWaitingForUpdate = true
        }
    )

    ActionDialog(
        show = showCancelDialog,
        message = "Your changes will be lost.",
        onCancel = {},
        onConfirm = {
            navController.navigate(Screen.SettingsScreen.route)
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp, 64.dp, 24.dp, 100.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                focusManager.clearFocus()
            },
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
            ,
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Profile Settings",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Montserrat,
                    fontSize = 16.sp,
                )
            }
        }
        Spacer(modifier = Modifier.size(24.dp))
        Row(
            modifier = Modifier
                .size(150.dp)
        ) {
            Box(
                contentAlignment = Alignment.BottomEnd
            ) {
                IconButton(
                    onClick = {
                              launcher.launch("image/*")
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Blue
                    ),
                    modifier = Modifier.zIndex(1f)
                ) {
                    Icon(imageVector = Icons.Filled.Create, contentDescription = "Pencil Icon", tint = Color.White)
                }
                DrawShape(shape = CircleShape, color = Gray, size = 150.dp) {

                    ShimmerEffectView(
                        isLoading = selectedImage == Uri.EMPTY,
                        shape = CircleShape
                    ){
                        AsyncImage(
                            model = selectedImage,
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                }
            }
        }
        Spacer(modifier = Modifier.size(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = firstName,
                onValueChange = {
                    firstName = it
                },
                placeholder = {
                    Text(
                        text = firstName,
                        fontFamily = Montserrat,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .focusRequester(focusRequester),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Gray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Gray
                ),
            )
        }
        Spacer(modifier = Modifier.size(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = lastName,
                onValueChange = {
                    lastName = it
                },
                placeholder = {
                    Text(
                        text = lastName,
                        fontFamily = Montserrat,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .focusRequester(focusRequester),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Gray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Gray
                ),
            )
        }
        Spacer(modifier = Modifier.size(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                placeholder = {
                    Text(
                        text = email.ifEmpty {
                            "example@gmail.com"
                        },
                        fontFamily = Montserrat,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .focusRequester(focusRequester),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Gray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Gray
                ),
            )
        }
        Spacer(modifier = Modifier.size(24.dp))
        Row {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                OutlinedTextField(
                    value = gender,
                    onValueChange = { },
                    trailingIcon = {
                        Icon(
                            gendersIcon,"contentDescription",
                            Modifier.clickable {
                                gendersDropdownExpaded = !gendersDropdownExpaded
                            },
                        )
                    },
                    readOnly = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Gray,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Gray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            //This value is used to assign to the DropDown the same width
                            textfieldSize = coordinates.size.toSize()
                        },
                )
                DropdownMenu(
                    expanded = gendersDropdownExpaded,
                    onDismissRequest = { gendersDropdownExpaded = false },
                    modifier = Modifier
                        .width(with(LocalDensity.current){textfieldSize.width.toDp()})
                ) {
                    DropdownMenuItem(
                        text = { Text(text = "Male") },
                        onClick = {
                            gender = "Male"
                            gendersDropdownExpaded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(text = "Female") },
                        onClick = {
                            gender = "Female"
                            gendersDropdownExpaded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(text = "Other") },
                        onClick = {
                            gender = "Other"
                            gendersDropdownExpaded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.End
        ) {
            Column {
                Button(
                    onClick = {
                        if ((selectedImage.toString() != profilePic.toString()) || (firstName != usersViewModel.activeUser!!.firstName) || (lastName != usersViewModel.activeUser!!.lastName) || (gender != usersViewModel.activeUser!!.gender) || (email != usersViewModel.activeUser!!.email)) {
                            showCancelDialog.value = true
                        }
                        else{
                            navController.navigate(Screen.SettingsScreen.route)
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
                Button(
                    onClick = {
                        showConfirmDialog.value = true
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Blue,
                        disabledContainerColor = Color(60, 161, 255, 0x8f),
                    ),
                    enabled = (selectedImage.toString() != profilePic.toString()) || (firstName != usersViewModel.activeUser!!.firstName) || (lastName != usersViewModel.activeUser!!.lastName) || (gender != usersViewModel.activeUser!!.gender) || (email != usersViewModel.activeUser!!.email)
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

@Composable
fun ActionDialog(show: MutableState<Boolean>,message: String,onCancel: () -> Unit,onConfirm: () -> Unit){
    if(show.value){
        Dialog(
            onDismissRequest = { show.value = false },
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
                        text = message,
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
                            show.value = false
                            onCancel()
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
                            show.value = false
                            onConfirm()
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
}