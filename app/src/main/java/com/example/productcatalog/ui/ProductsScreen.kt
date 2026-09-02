package com.example.productcatalog.ui

import android.app.Activity
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.productcatalog.R
import com.example.productcatalog.domain.model.Product
import com.example.productcatalog.domain.model.Vendor
import com.example.productcatalog.model.ProductsUiState
import com.example.productcatalog.ui.popup.CartBottomSheet
import com.example.productcatalog.ui.popup.ProductDetailPopup
import com.example.productcatalog.ui.theme.ProductCatalogTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsContent(
    uiState: ProductsUiState,
    onRemoveFromCart: (Product) -> Unit,
    isLoggedIn: Boolean?,
    loggedInUser: String? = null,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onSignInClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onVendorSelected: (Vendor) -> Unit,
    onAddToCart: (Product) -> Unit,
    onFavoriteClick: (Int) -> Unit,
    onFavoritesClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onOffersClick: () -> Unit,
    onLogoClick: () -> Unit,
    onCheckoutClick: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showSignOutDialog by remember { mutableStateOf(false) }

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text(text = "Sign Out") },
            text = { Text(text = "Are you sure want to Signout") },
            confirmButton = {
                TextButton(onClick = {
                    showSignOutDialog = false
                    onSignOutClick()
                }) {
                    Text("Signout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(280.dp)) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { scope.launch { drawerState.close() } },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Menu",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.LocalOffer, contentDescription = null) },
                    label = { Text("Offers") },
                    selected = false,
                    onClick = {
                        onOffersClick()
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.History, contentDescription = null) },
                    label = { Text("Orders") },
                    selected = false,
                    onClick = {
                        onOrdersClick()
                        scope.launch { drawerState.close() }
                    }
                )

                if (isLoggedIn != null) {
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = if (isLoggedIn) Icons.AutoMirrored.Filled.Logout else Icons.AutoMirrored.Filled.Login,
                                contentDescription = null
                            )
                        },
                        label = { Text(if (isLoggedIn) "Sign Out" else "Sign In") },
                        selected = false,
                        onClick = {
                            if (isLoggedIn) {
                                showSignOutDialog = true
                            } else {
                                onSignInClick()
                            }
                            scope.launch { drawerState.close() }
                        }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = null
                        )
                    },
                    label = { Text(if (isDarkTheme) "Light Mode" else "Dark Mode") },
                    selected = false,
                    onClick = {
                        onThemeToggle()
                        scope.launch { drawerState.close() }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(vertical = 1.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.browserstack_logo_icon_167776),
                                contentDescription = "Logo",
                                modifier = Modifier
                                    .height(40.dp)
                                    .padding(horizontal = 2.dp, vertical = 1.dp)
                                    .clickable { onLogoClick() },
                                contentScale = ContentScale.Fit
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        val favoriteCount = uiState.favoriteProductIds.size
                        Box(modifier = Modifier
                            .padding(8.dp)
                            .clickable { onFavoritesClick() }) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Favorites",
                                modifier = Modifier.size(28.dp),
                                tint = if (favoriteCount > 0) Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                            if (favoriteCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                        .align(Alignment.TopEnd),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = favoriteCount.toString(),
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        var showCartSheet by remember { mutableStateOf(false) }
                        val cartCount = uiState.cartCount

                        Box(modifier = Modifier
                            .clickable { showCartSheet = true }
                            .padding(8.dp)) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Cart",
                                modifier = Modifier.size(28.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            if (cartCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.error)
                                        .align(Alignment.TopEnd),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cartCount.toString(),
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onError,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        if (showCartSheet) {
                            val totalAmount = uiState.cartProducts.sumOf {
                                it.price.toString().toDoubleOrNull() ?: 0.0
                            }
                            CartBottomSheet(
                                cartItems = uiState.cartProducts,
                                totalAmount = totalAmount,
                                onRemoveItem = onRemoveFromCart,
                                onCheckoutClick = onCheckoutClick,
                                onDismiss = { showCartSheet = false }
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                color = MaterialTheme.colorScheme.surface
            ) {
                val configuration = LocalConfiguration.current
                val columns = when (configuration.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> 3
                    else -> 2
                }

                var selectedProduct by remember { mutableStateOf<Product?>(null) }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        VendorSection(
                            selectedVendor = uiState.selectedVendor,
                            onVendorSelected = onVendorSelected
                        )
                    }

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = "${uiState.products.size} Product(s) found.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(
                        items = uiState.products,
                        key = { it.id }
                    ) { product ->
                        ProductItem(
                            product = product,
                            isFavorite = product.id in uiState.favoriteProductIds,
                            isLoggedIn = isLoggedIn ?: false,
                            loggedInUser = loggedInUser,
                            onSignInClick = onSignInClick,
                            onAddToCart = { onAddToCart(product) },
                            onFavoriteClick = { onFavoriteClick(product.id) },
                            onClick = { selectedProduct = product }
                        )
                    }
                }

                selectedProduct?.let { product ->
                    ProductDetailPopup(
                        product = product,
                        isFavorite = product.id in uiState.favoriteProductIds,
                        isLoggedIn = isLoggedIn ?: false,
                        loggedInUser = loggedInUser,
                        onSignInClick = onSignInClick,
                        onAddToCart = onAddToCart,
                        onFavoriteClick = { onFavoriteClick(product.id) },
                        onDismiss = { selectedProduct = null }
                    )
                }

                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                            .clickable(enabled = false) {},
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun VendorSection(
    selectedVendor: Vendor?,
    onVendorSelected: (Vendor) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Vendors", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(Vendor.entries) { vendor ->
                // FIXED: Defined isSelected here
                val isSelected = vendor == selectedVendor
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onVendorSelected(vendor) },
                    // FIXED: Uses theme colors
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = vendor.label,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductItem(
    product: Product,
    isFavorite: Boolean,
    isLoggedIn: Boolean,
    loggedInUser: String? = null,
    onSignInClick: () -> Unit,
    onAddToCart: (Product) -> Unit,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    val productImage = when {
        product.title.contains("Pixel", true) -> R.drawable.ic_google_pixel_image
        product.title.contains("Galaxy", true) -> R.drawable.ic_samsung_image
        product.title.contains("Plus", true) -> R.drawable.ic_one_plus_image
        else -> R.drawable.ic_iphone_images
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            if (loggedInUser == "image_not_loader_user") {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            } else {
                Image(
                    painter = painterResource(id = productImage),
                    contentDescription = null,
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .width(180.dp)
                        .fillMaxHeight(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Text(
            text = product.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(product.currencyFormat, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = product.price.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // FIXED: Safe price calculation to prevent crashes
        val installmentPrice = (product.price.toString().toDoubleOrNull() ?: 0.0) / product.installments
        Text(
            text = "or ${product.installments} x ${product.currencyFormat}${installmentPrice.formatPrice()}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp) // Handles spacing between items
        ) {
            Button(
                onClick = { if (isLoggedIn) onAddToCart(product) else onSignInClick() },
                modifier = Modifier
                    .weight(1f) // Makes the button take up remaining space
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp), // Prevents text clipping
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Add to cart",
                    fontSize = 14.sp,
                    maxLines = 1
                )
            }

            IconButton(
                onClick = { if (isLoggedIn) onFavoriteClick() else onSignInClick() },
                modifier = Modifier
                    .size(48.dp) // Standard size for touch targets
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    modifier = Modifier.size(24.dp),
                    tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun Double.formatPrice(): String = "%.2f".format(this)


/*@Preview(
    name = "Product Catalog",
    showBackground = true,
    widthDp = 1280,
    heightDp = 800
)*/
//@Preview(showBackground = true, name = "Portrait")
////@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,orientation=landscape", name = "Landscape")
//@Composable
//private fun ProductsPreview() {
//    ProductCatalogTheme {
//        ProductsContent(
//            onSignInClick = {
//
//            }, isLoggedIn = false,
//            onCheckoutNavigate = TODO(),
//            viewModel = TODO()
//        )
//    }
//}
