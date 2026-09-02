package com.example.productcatalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
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
import com.example.productcatalog.ui.FavoritesScreen
import com.example.productcatalog.ui.OffersScreen
import com.example.productcatalog.ui.OrderSuccessScreen
import com.example.productcatalog.ui.OrdersScreen
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

        enableEdgeToEdge()

        setContent {
            val productsViewModel: ProductsViewModel = viewModel(
                factory = ProductsViewModelFactory(userPreferences)
            )
            val darkModePref by productsViewModel.darkMode.collectAsStateWithLifecycle(initialValue = null)
            val useDarkTheme = darkModePref ?: isSystemInDarkTheme()

            ProductCatalogTheme(darkTheme = useDarkTheme) {
                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(userPreferences)
                )

                val isLoggedIn by loginViewModel.isLoggedIn.collectAsStateWithLifecycle()
                val loggedInUsername by loginViewModel.loggedInUsername.collectAsStateWithLifecycle()

                AppNavigation(
                    isLoggedIn = isLoggedIn,
                    loggedInUsername = loggedInUsername,
                    isDarkTheme = useDarkTheme,
                    loginViewModel = loginViewModel,
                    productsViewModel = productsViewModel
                )
            }
        }
    }

    @Composable
    private fun AppNavigation(
        isLoggedIn: Boolean?,
        loggedInUsername: String?,
        isDarkTheme: Boolean,
        loginViewModel: LoginViewModel,
        productsViewModel: ProductsViewModel
    ) {
        val navController = rememberNavController()
        // Shared ViewModel for Products and Checkout
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
                    loggedInUser = loggedInUsername,
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = {
                        productsViewModel.setDarkMode(!isDarkTheme)
                    },
                    onSignInClick = { navController.navigate("signin") },
                    onSignOutClick = {
                        loginViewModel.signOut {
                            productsViewModel.clearAllData()
                            productsViewModel.refresh()
                        }
                    },
                    onCheckoutClick = { navController.navigate("checkout") },
                    onVendorSelected = { vendor -> productsViewModel.selectVendor(vendor) },
                    onAddToCart = { product -> productsViewModel.addToCart(product) },
                    onRemoveFromCart = { product -> productsViewModel.removeFromCart(product) },
                    onFavoriteClick = { productId -> productsViewModel.toggleFavorite(productId) },
                    onFavoritesClick = { navController.navigate("favorites") },
                    onOrdersClick = { navController.navigate("orders") },
                    onOffersClick = { navController.navigate("offers") },
                    onLogoClick = {
                        productsViewModel.selectVendor(null)
                        navController.popBackStack("products", inclusive = false)
                    }
                )
            }

            composable("offers") {
                OffersScreen(onBackClick = { navController.popBackStack() })
            }

            composable("favorites") {
                val favoriteProducts = androidx.compose.runtime.remember(productUiState.favoriteProductIds) {
                    productsViewModel.getFavoriteProducts()
                }
                FavoritesScreen(
                    favoriteProducts = favoriteProducts,
                    onRemoveFavorite = { productId -> productsViewModel.toggleFavorite(productId) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("orders") {
                val orderedProducts = androidx.compose.runtime.remember(productUiState.orderedProductIds) {
                    productsViewModel.getOrderedProducts()
                }
                OrdersScreen(
                    orderedProducts = orderedProducts,
                    loggedInUser = loggedInUsername,
                    onBackClick = { navController.popBackStack() }
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
                    onBackClick = { navController.popBackStack() },
                    onOrderSubmit = { userName ->
                        productsViewModel.clearCart()
                        navController.navigate("order_success/$userName") {
                            popUpTo("products") { inclusive = false }
                        }
                    }
                )
            }

            composable("order_success/{userName}") { backStackEntry ->
                val userName = backStackEntry.arguments?.getString("userName") ?: "Customer"
                OrderSuccessScreen(
                    userName = userName,
                    onContinueShopping = {
                        navController.popBackStack("products", inclusive = false)
                    }
                )
            }

            composable("signin") {
                SignInScreen(
                    viewModel = loginViewModel,
                    isDarkTheme = isDarkTheme,
                    onNavigateBack = { navController.popBackStack() },
                    onSignInSuccess = {
                        productsViewModel.refresh()
                        navController.popBackStack()
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

class ProductsViewModelFactory(private val userPreferences: UserPreferences) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductsViewModel(userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}