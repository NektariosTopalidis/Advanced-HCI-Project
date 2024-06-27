package gr.nektariostop.ergasiaadvancedhci

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import gr.nektariostop.ergasiaadvancedhci.ui.theme.Montserrat

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun SettingsView(
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


    var showLogoutDialog by remember {
        mutableStateOf(false)
    }

    BackHandler{
        navController.navigate(Screen.HomeScreen.route)
    }

    if(showLogoutDialog){
        Dialog(
            onDismissRequest = { showLogoutDialog = false },
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
                            showLogoutDialog = false
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
                            showLogoutDialog = false
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
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp, 0.dp)
            ,
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Settings",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Montserrat,
                    fontSize = 16.sp,
                )
            }
        }
        Spacer(modifier = Modifier.size(24.dp))
        SettingsOption(title = "Profile Settings", route = Screen.ProfileSettingsScreen.route, navController = navController) {
            Image(painter = painterResource(id = R.drawable.user_icon_black), contentDescription = "User Icon")
        }
        Spacer(modifier = Modifier.size(24.dp))
        Divider(color = Color(0x3f9D9D9D), thickness = 1.dp, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.size(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp, 0.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompositionLocalProvider(LocalRippleTheme provides BlackRippleTheme) {
                Button(
                    onClick = {
                        showLogoutDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    )
                ) {
                    Text(
                        text = "Log Out",
                        fontWeight = FontWeight.Bold,
                        fontFamily = Montserrat,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Image(painter = painterResource(id = R.drawable.logout_icon), contentDescription = "Logout Icon")
                }
            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun SettingsOption(title: String,route: String,navController: NavController,icon: @Composable () -> Unit){
    Row(
        modifier = Modifier

            .fillMaxWidth()
            .height(50.dp)
            .padding(24.dp, 0.dp)
            .advancedShadow(alpha = 0.05f, cornersRadius = 10.dp, shadowBlurRadius = 5.dp)
            .background(Color.White, RoundedCornerShape(10.dp))
            .clickable {
                navController.navigate(route)
            }

        ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp,0.dp,0.dp,0.dp)
        )  {
            Row{
                Column {
                    icon()
                }
                Spacer(modifier = Modifier.size(8.dp))
                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Montserrat,
                        fontSize = 16.sp,
                    )       
                }
            }
        }
        Column(
            modifier = Modifier
                .padding(0.dp,0.dp,12.dp,0.dp)
        )  {
            Icon(Icons.Filled.KeyboardArrowRight , contentDescription = "Right Arrow", tint = Color.Black)
        }
    }
}
