package com.example.productcatalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.productcatalog.data.UserPreferences
import com.example.productcatalog.ui.ProductsScreen
import com.example.productcatalog.ui.SignInScreen
import com.example.productcatalog.ui.login.LoginViewModel
import com.example.productcatalog.ui.theme.ProductCatalogTheme

class MainActivity : ComponentActivity() {
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // FIXED: Use applicationContext directly. Casting to MainActivity was causing a crash.
        userPreferences = UserPreferences(applicationContext) // Correct

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.statusBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        enableEdgeToEdge()

        setContent {
            ProductCatalogTheme {
                // Initialize ViewModel using the Custom Factory
                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(userPreferences)
                )

                val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()
                val isLoggedIn by loginViewModel.isLoggedIn.collectAsStateWithLifecycle()

                AppNavigation(
                    isLoggedIn = isLoggedIn,
                    viewModel = loginViewModel
                )
            }
        }
    }

    @Composable
    private fun AppNavigation(
        isLoggedIn: Boolean,
        viewModel: LoginViewModel
    ) {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = "products"
        ) {
            composable("products") {
                ProductsScreen(
                    isLoggedIn = isLoggedIn,
                    onSignInClick = { navController.navigate("signin") }
                )
            }
            composable("signin") {
                SignInScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onSignInSuccess = {
                        navController.navigate("products") {
                            popUpTo("signin") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

class LoginViewModelFactory(private val userPreferences: UserPreferences) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}