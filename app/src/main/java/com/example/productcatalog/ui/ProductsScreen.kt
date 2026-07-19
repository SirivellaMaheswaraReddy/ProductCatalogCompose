package com.example.productcatalog.ui

import android.content.res.Configuration
import android.util.Log
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.productcatalog.R
import com.example.productcatalog.model.Product
import com.example.productcatalog.model.ProductsUiState
import com.example.productcatalog.model.Vendor
import com.example.productcatalog.ui.popup.CartBottomSheet
import com.example.productcatalog.ui.theme.ProductCatalogTheme

@Composable
fun ProductsScreen(
    isLoggedIn: Boolean,
    onSignInClick: () -> Unit,
    viewModel: ProductsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProductsContent(
        uiState = uiState,
        onRemoveFromCart = { product -> viewModel.removeFromCart(product) }, // New lambda
        isLoggedIn = isLoggedIn,
        onSignInClick = onSignInClick,
        onVendorSelected = viewModel::selectVendor,
        onAddToCart = { product -> viewModel.addToCart(product) },
        onFavoriteClick = viewModel::toggleFavorite
    )
}

@Composable
private fun ProductsContent(
    uiState: ProductsUiState,
    onRemoveFromCart: (Product) -> Unit,
    isLoggedIn: Boolean,
    onSignInClick: () -> Unit,
    onVendorSelected: (Vendor) -> Unit,
    onAddToCart: (Product) -> Unit,
    onFavoriteClick: (Int) -> Unit,
) {
    val view = androidx.compose.ui.platform.LocalView.current
    if (!view.isInEditMode) {
        androidx.compose.runtime.SideEffect {
            val window = (view.context as android.app.Activity).window
            val controller = androidx.core.view.WindowCompat.getInsetsController(window, view)
            controller.hide(androidx.core.view.WindowInsetsCompat.Type.statusBars())
            controller.systemBarsBehavior = androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val configuration = LocalConfiguration.current

        // Determine column count based on orientation
        val columns = when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> 3 // 3 columns for landscape
            else -> 2 // 2 columns for portrait
        }
        val totalAmount = uiState.cartProducts.sumOf {
            it.price.toString().toDoubleOrNull() ?: 0.0
        }
        ProductTopBar(cartCount = uiState.cartCount,
            cartItems = uiState.cartProducts,
            totalAmount =   totalAmount,
            isLoggedIn = isLoggedIn,
            onSignInClick = onSignInClick,
            onRemoveItem = onRemoveFromCart
        )


        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = 16.dp, // Reduced horizontal padding for better portrait fit
                vertical = 12.dp
            ),
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
                    color = Color.DarkGray
                )
            }
            items(
                items = uiState.products,
                key = { it.id }
            ) { product ->
                ProductItem(
                    product = product,
                    isFavorite = product.id in uiState.favoriteProductIds,
                    onAddToCart = { onAddToCart(product) } ,
                    onFavoriteClick = { onFavoriteClick(product.id) }
                )
            }
        }
    }
}

@Composable
private fun ProductTopBar(cartCount: Int,
                          cartItems: List<Product>, // Added parameter
                          totalAmount: Double,
                          isLoggedIn: Boolean,
                          onSignInClick: () -> Unit,
                          onRemoveItem: (Product) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_browser_stack_logo),
            contentDescription = "BrowserStack Logo",
            modifier = Modifier
                .height(40.dp)
                .wrapContentSize(),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.width(10.dp))

        LazyRow(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            item { Text("Offers", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
            item { Text("Orders", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
            item { Text("Favourites", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
            Log.e("M333", "isLoggedIn pro: $isLoggedIn")
            if (!isLoggedIn) {
                item {
                    Text(
                        text = "Sign In",
                        modifier = Modifier.clickable { onSignInClick() }, // Now this works!
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        var showCartSheet by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .padding(16.dp)
                .clickable { showCartSheet = true } // Click listener added here
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Cart",
                modifier = Modifier.size(42.dp)
            )


            if (cartCount >= 0) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFD600))
                        .align(Alignment.BottomEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cartCount.toString(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        if (showCartSheet) {
            CartBottomSheet(
                cartItems = cartItems, // Pass your list of products
                totalAmount = totalAmount,
                onRemoveItem = onRemoveItem,
                onDismiss = { showCartSheet = false }
            )
        }
    }
}

@Composable
private fun VendorSection(
    selectedVendor: Vendor,
    onVendorSelected: (Vendor) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Vendors:",
            fontSize = 16.sp,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            items(Vendor.entries) { vendor ->
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onVendorSelected(vendor) },
                    color = if (vendor == selectedVendor) {
                        Color(0xFFEFB8C8)
                    } else {
                        Color(0xFFF0F0F0)
                    }
                ) {
                    Text(
                        text = vendor.label,
                        modifier = Modifier.padding(
                            horizontal = 15.dp,
                            vertical = 10.dp
                        ),
                        fontSize = 16.sp
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
    onAddToCart: (Product) -> Unit,
    onFavoriteClick: () -> Unit
) {
    val productImage = when {
        product.title.contains("Pixel", ignoreCase = true) -> R.drawable.ic_google_pixel_image
        product.title.contains("Galaxy", ignoreCase = true) -> R.drawable.ic_samsung_image
        product.title.contains("Plus", ignoreCase = true) -> R.drawable.ic_one_plus_image
        product.title.contains("iphone", ignoreCase = true) -> R.drawable.ic_iphone_img
        else -> R.drawable.ic_iphone_img // Default fallback image
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .height(240.dp)
                .wrapContentSize() // This ensures the Box matches the image's visual width
        ) {
            Image(
                painter = painterResource(id = productImage),
                contentDescription = product.title,
                modifier = Modifier
                    .height(240.dp) // Maintain consistent height
                    .aspectRatio(1f), // Keep it square or adjust to your image ratio
                contentScale = ContentScale.Fit
            )

            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd) // Aligns to the top-end of the Image
                    .padding(4.dp)
                    .background(Color.White.copy(alpha = 0.7f), CircleShape) // Optional: background for visibility
                    .size(32.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favourite",
                    tint = if (isFavorite) Color.Red else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = product.title,
            fontSize = 18.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Center,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .width(52.dp)
                .height(5.dp)
                .background(Color(0xFFE5C400))
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.Bottom) {
            Text(product.currencyFormat, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = product.price.toString(),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
            Text(".00", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "or ${product.installments} x ${product.currencyFormat} " +
                    "${product.price.toDouble().div(product.installments).formatPrice()}",
            fontSize = 20.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onAddToCart(product) },
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1C1B20)
            )
        ) {
            Text(
                text = "Add to cart",
                fontSize = 20.sp,
                color = Color.White
            )
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
@Preview(showBackground = true, name = "Portrait")
//@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,orientation=landscape", name = "Landscape")
@Composable
private fun ProductsPreview() {
    ProductCatalogTheme {
        ProductsScreen(onSignInClick = {

        }, isLoggedIn = false)
    }
}
