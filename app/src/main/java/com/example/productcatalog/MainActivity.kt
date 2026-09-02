package com.example.productcatalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.productcatalog.data.UserPreferences
import com.example.productcatalog.data.repository.ProductRepositoryImpl
import com.example.productcatalog.data.repository.UserRepositoryImpl
import com.example.productcatalog.domain.usecase.GetProductsUseCase
import com.example.productcatalog.domain.usecase.PlaceOrderUseCase
import com.example.productcatalog.domain.usecase.SignInUseCase
import com.example.productcatalog.domain.usecase.SignOutUseCase
import com.example.productcatalog.domain.usecase.ToggleFavoriteUseCase
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
            val userRepository = UserRepositoryImpl(userPreferences)
            val productRepository = ProductRepositoryImpl(userPreferences)
            
            val productsViewModel: ProductsViewModel = viewModel(
                factory = ProductsViewModelFactory(
                    GetProductsUseCase(productRepository),
                    ToggleFavoriteUseCase(productRepository),
                    PlaceOrderUseCase(productRepository),
                    productRepository,
                    userRepository
                )
            )
            
            val darkModePref by productsViewModel.darkMode.collectAsStateWithLifecycle(initialValue = null)
            val useDarkTheme = darkModePref ?: isSystemInDarkTheme()

            ProductCatalogTheme(darkTheme = useDarkTheme) {
                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(
                        userRepository,
                        SignInUseCase(userRepository),
                        SignOutUseCase(userRepository)
                    )
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
                OffersScreen(onBackClick = { 
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                })
            }

            composable("favorites") {
                val favoriteProducts = androidx.compose.runtime.remember(productUiState.favoriteProductIds) {
                    productsViewModel.getFavoriteProducts()
                }
                FavoritesScreen(
                    favoriteProducts = favoriteProducts,
                    onRemoveFavorite = { productId -> productsViewModel.toggleFavorite(productId) },
                    onBackClick = { 
                        if (navController.previousBackStackEntry != null) {
                            navController.popBackStack()
                        }
                    }
                )
            }

            composable("orders") {
                val orderedProducts = androidx.compose.runtime.remember(productUiState.orderedProductIds) {
                    productsViewModel.getOrderedProducts()
                }
                OrdersScreen(
                    orderedProducts = orderedProducts,
                    loggedInUser = loggedInUsername,
                    onBackClick = { 
                        if (navController.previousBackStackEntry != null) {
                            navController.popBackStack()
                        }
                    }
                )
            }

            composable("checkout") {
                val totalAmount = productUiState.cartProducts.sumOf {
                    it.price.toDouble()
                }

                CheckoutScreen(
                    cartItems = productUiState.cartProducts,
                    totalAmount = totalAmount,
                    onBackClick = { 
                        if (navController.previousBackStackEntry != null) {
                            navController.popBackStack()
                        }
                    },
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
                    onNavigateBack = { 
                        if (navController.previousBackStackEntry != null) {
                            navController.popBackStack()
                        }
                    },
                    onSignInSuccess = {
                        productsViewModel.refresh()
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

class LoginViewModelFactory(
    private val userRepository: com.example.productcatalog.domain.repository.UserRepository,
    private val signInUseCase: SignInUseCase,
    private val signOutUseCase: SignOutUseCase
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(userRepository, signInUseCase, signOutUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ProductsViewModelFactory(
    private val getProductsUseCase: GetProductsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase,
    private val productRepository: com.example.productcatalog.domain.repository.ProductRepository,
    private val userRepository: com.example.productcatalog.domain.repository.UserRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductsViewModel(
                getProductsUseCase,
                toggleFavoriteUseCase,
                placeOrderUseCase,
                productRepository,
                userRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
