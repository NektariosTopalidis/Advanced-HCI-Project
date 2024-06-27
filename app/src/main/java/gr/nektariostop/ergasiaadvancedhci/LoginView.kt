package gr.nektariostop.ergasiaadvancedhci

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import gr.nektariostop.ergasiaadvancedhci.data.User
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Blue
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Gray
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Montserrat

@Composable
fun LoginView(
    usersViewModel: UsersViewModel,
    navController: NavController,
    onSignInSuccess: () -> Unit
) {

    val context = LocalContext.current

    val authResult by usersViewModel.authResult.observeAsState()
    val activeUserResult by usersViewModel.activeUserResult.observeAsState(emptyList<User>())


    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var isWaitingForLogin by remember {
        mutableStateOf(false)
    }

    var passwordVisible by remember { mutableStateOf(false) }



    when (activeUserResult){
        is Result.Success<*> -> {

            usersViewModel.setActiveUserAfterReq(((activeUserResult as Result.Success<*>).data as List<User>))
            isWaitingForLogin = false
            onSignInSuccess()
        }

        is Result.Error -> {
            isWaitingForLogin = false
        }

        else -> {

        }
    }


    when (authResult) {
        is Result.Success -> {
            println("HAHAAAAAAAAA")
            usersViewModel.getActiveUser()
        }

        is Result.Error -> {
            isWaitingForLogin = false
        }

        is Result.DoNothing -> {
            
        }

        else -> {

        }
    }


    if (isWaitingForLogin){
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                focusManager.clearFocus()
            },
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row {
            AsyncImage(
                model = R.mipmap.login_background,
                contentDescription = "Login Background",
                placeholder = painterResource(R.drawable.product_image_placeholder),
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
        Row {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = stringResource(id = R.string.logo_content_description),
            )
        }
        Spacer(modifier = Modifier.size(32.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp, 0.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Log-in",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontFamily = Montserrat,
                fontSize = 24.sp,
            )
        }
        Spacer(modifier = Modifier.size(32.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp, 0.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                label = {
                    Text(
                        text = "Your Email",
                        fontFamily = Montserrat,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .focusRequester(focusRequester),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Gray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Gray
                ),
            )
        }
        Spacer(modifier = Modifier.size(32.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp, 0.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                label = {
                    Text(
                        text = "Password",
                        fontFamily = Montserrat,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .focusRequester(focusRequester),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Gray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Gray
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    // Localized description for accessibility services
                    val description = if (passwordVisible) "Hide password" else "Show password"

                    // Toggle button to hide or display password
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                }
            )
        }
        Spacer(modifier = Modifier.size(32.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp, 0.dp)
        ) {
            Button(
                onClick = {
                    isWaitingForLogin = true
                    usersViewModel.login(email, password)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                enabled = (password.isNotEmpty() && email.isNotEmpty()),
                colors = ButtonDefaults.buttonColors(
                    disabledContentColor = if (isWaitingForLogin){
                        Blue
                    }
                    else{
                        Gray
                    }
                )
            ) {
                Text(
                    text = "LOGIN",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Montserrat,
                    fontSize = 18.sp,
                )
            }
        }
    }
}