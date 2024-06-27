package gr.nektariostop.ergasiaadvancedhci

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun Navigation(
    innerPadding: PaddingValues?,
    scanViewModel: ScanViewModel = viewModel(),
    categoriesViewModel: CategoriesViewModel = viewModel(),
    alterationsViewModel: AlterationsViewModel = viewModel(),
    productsViewModel: ProductsViewModel = viewModel(),
    usersViewModel: UsersViewModel = viewModel(),
    subcategoriesViewModel: SubcategoriesViewModel = viewModel(),
    commentsViewModel: CommentsViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
){

    NavHost(
        navController = navController,
        startDestination = Screen.LoginScreen.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ){

        composable(Screen.LoginScreen.route){
            LoginView(usersViewModel,navController){
                navController.navigate(Screen.HomeScreen.route)
            }
        }

        composable(Screen.HomeScreen.route){
            usersViewModel.getAllUsers()
            commentsViewModel.getComments()
            HomeView(innerPadding!!,navController,scanViewModel,categoriesViewModel,alterationsViewModel,productsViewModel,usersViewModel,subcategoriesViewModel,commentsViewModel)
        }

        composable(Screen.CategoriesScreen.route){
            CategoriesView(innerPadding!!,navController,scanViewModel,categoriesViewModel,alterationsViewModel,productsViewModel,usersViewModel,subcategoriesViewModel,commentsViewModel)
        }

        composable(Screen.CategoriesScreen.route + "/{id}",
                arguments = listOf(
                    navArgument("id"){
                        type = NavType.LongType
                        defaultValue = 0L
                        nullable = false
                    }
                )
            ){entry ->
            val id = if(entry.arguments != null) entry.arguments!!.getLong("id") else 0L
            SubcategoriesView(id,innerPadding!!,navController,scanViewModel,categoriesViewModel,alterationsViewModel,productsViewModel,usersViewModel,subcategoriesViewModel,commentsViewModel)
        }

        composable(Screen.CategoriesScreen.route + "/{id}" + "/{subId}",
                arguments = listOf(
                    navArgument("id"){
                        type = NavType.LongType
                        defaultValue = 0L
                        nullable = false
                    },
                    navArgument("subId"){
                        type = NavType.LongType
                        defaultValue = 0L
                        nullable = false
                    }
                )
            ){entry ->
            val id = if(entry.arguments != null) entry.arguments!!.getLong("id") else 0L
            val subId = if(entry.arguments != null) entry.arguments!!.getLong("subId") else 0L
            ProductsView(id,subId,innerPadding!!,navController,scanViewModel,categoriesViewModel,alterationsViewModel,productsViewModel,usersViewModel,subcategoriesViewModel,commentsViewModel)
        }

        composable(Screen.CategoriesScreen.route + "/{id}" + "/{subId}" + "/{productId}",
                arguments = listOf(
                    navArgument("id"){
                        type = NavType.LongType
                        defaultValue = 0L
                        nullable = false
                    },
                    navArgument("subId"){
                        type = NavType.LongType
                        defaultValue = 0L
                        nullable = false
                    },
                    navArgument("productId"){
                        type = NavType.LongType
                        defaultValue = 0L
                        nullable = false
                    }
                )
            ){entry ->
            val id = if(entry.arguments != null) entry.arguments!!.getLong("id") else 0L
            val subId = if(entry.arguments != null) entry.arguments!!.getLong("subId") else 0L
            val productId = if(entry.arguments != null) entry.arguments!!.getLong("productId") else 0L
            commentsViewModel.getComments()
            SingleProductView(id,subId,productId,innerPadding!!,navController,scanViewModel,categoriesViewModel,alterationsViewModel,productsViewModel,usersViewModel,subcategoriesViewModel,commentsViewModel)
        }

        composable(Screen.HistoryScreen.route){
            commentsViewModel.getComments()
            HistoryView(innerPadding!!,navController,scanViewModel,categoriesViewModel,alterationsViewModel,productsViewModel,usersViewModel,subcategoriesViewModel,commentsViewModel)
        }

        composable(Screen.HistoryScreen.route + "/{id}",
            arguments = listOf(
                navArgument("id"){
                    type = NavType.LongType
                    defaultValue = 0L
                    nullable = false
                }
            )
        ){entry ->
            val id = if(entry.arguments != null) entry.arguments!!.getLong("id") else 0L
            AlterationView(id,innerPadding!!,navController,scanViewModel,categoriesViewModel,alterationsViewModel,productsViewModel,usersViewModel,subcategoriesViewModel,commentsViewModel)
        }

        composable(Screen.SettingsScreen.route){
            SettingsView(innerPadding!!,navController,scanViewModel,categoriesViewModel,alterationsViewModel,productsViewModel,usersViewModel,subcategoriesViewModel,commentsViewModel)
        }

        composable(Screen.ProfileSettingsScreen.route){
            ProfileSettingsView(innerPadding!!,navController,scanViewModel,categoriesViewModel,alterationsViewModel,productsViewModel,usersViewModel,subcategoriesViewModel,commentsViewModel)
        }
    }

}