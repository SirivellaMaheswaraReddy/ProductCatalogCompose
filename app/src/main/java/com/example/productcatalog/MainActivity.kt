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
import com.example.productcatalog.ui.CheckoutScreen
import com.example.productcatalog.ui.ProductsContent
import com.example.productcatalog.ui.ProductsViewModel
import com.example.productcatalog.ui.SignInScreen
import com.example.productcatalog.ui.login.LoginViewModel
import com.example.productcatalog.ui.theme.ProductCatalogTheme

class MainActivity : ComponentActivity() {
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userPreferences = UserPreferences(applicationContext)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.statusBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        enableEdgeToEdge()

        setContent {
            ProductCatalogTheme {
                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(userPreferences)
                )

                val isLoggedIn by loginViewModel.isLoggedIn.collectAsStateWithLifecycle()

                AppNavigation(
                    isLoggedIn = isLoggedIn,
                    loginViewModel = loginViewModel
                )
            }
        }
    }

    @Composable
    private fun AppNavigation(
        isLoggedIn: Boolean,
        loginViewModel: LoginViewModel
    ) {
        val navController = rememberNavController()
        // Shared ViewModel for Products and Checkout
        val productsViewModel: ProductsViewModel = viewModel()
        val productUiState by productsViewModel.uiState.collectAsStateWithLifecycle()

        NavHost(
            navController = navController,
            startDestination = "products"
        ) {
            composable("products") {
                // FIXED: Call ProductsContent with its required parameters, not the ViewModel itself
                ProductsContent(
                    uiState = productUiState,
                    isLoggedIn = isLoggedIn,
                    onSignInClick = { navController.navigate("signin") },
                    onCheckoutClick = { navController.navigate("checkout") },
                    onVendorSelected = { vendor -> productsViewModel.selectVendor(vendor) },
                    onAddToCart = { product -> productsViewModel.addToCart(product) },
                    onRemoveFromCart = { product -> productsViewModel.removeFromCart(product) },
                    onFavoriteClick = { productId -> productsViewModel.toggleFavorite(productId) }
                )
            }

            composable("checkout") {
                // FIXED: Use toDoubleOrNull to prevent crashes
                val totalAmount = productUiState.cartProducts.sumOf {
                    it.price.toString().toDoubleOrNull() ?: 0.0
                }

                CheckoutScreen(
                    cartItems = productUiState.cartProducts,
                    totalAmount = totalAmount,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("signin") {
                SignInScreen(
                    viewModel = loginViewModel,
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