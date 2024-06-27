package gr.nektariostop.ergasiaadvancedhci

sealed class Screen(val route: String) {
    data object LoginScreen: Screen("login-screen")
    data object HomeScreen: Screen("home-screen")
    data object CategoriesScreen: Screen("categories-screen")
    data object HistoryScreen: Screen("history-screen")
    data object SettingsScreen: Screen("settings-screen")
    data object ProfileSettingsScreen: Screen("profile-settings-screen")
}
