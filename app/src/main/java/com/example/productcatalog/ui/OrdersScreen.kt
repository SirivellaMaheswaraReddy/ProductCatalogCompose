package com.example.productcatalog.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.productcatalog.R
import com.example.productcatalog.domain.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    orderedProducts: List<Product>,
    loggedInUser: String? = null,
    onBackClick: () -> Unit
) {
    val finalOrders = if (loggedInUser == "existing_order_user") {
        getExistingUserOrders() + orderedProducts
    } else {
        orderedProducts
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (finalOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No orders yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(finalOrders) { product ->
                    OrderItem(product = product)
                }
            }
        }
    }
}

private fun getExistingUserOrders(): List<Product> {
    return listOf(
        Product(listOf("Apple"), "$", "USD", "iPhone 12", 5, 1, false, 10, "iPhone12-device-info.png", "iPhone 12"),
        Product(listOf("Apple"), "$", "USD", "iPhone 12 Pro Max", 6, 1, false, 10, "iPhone12-device-info.png", "iPhone 12 Pro Max"),
        Product(listOf("Apple"), "$", "USD", "iPhone 11", 7, 1, false, 10, "iPhone11-device-info.png", "iPhone 11"),
        Product(listOf("Samsung"), "$", "USD", "Galaxy S20+", 8, 1, false, 10, "samsung-S20+-device-info.png", "Galaxy S20+"),
        Product(listOf("Google"), "$", "USD", "Pixel 3", 9, 1, false, 10, "GooglePixel3-device-info.png", "Pixel 3"),
        
        Product(listOf("Google"), "$", "USD", "Pixel 3", 10, 1, false, 10, "GooglePixel3-device-info.png", "Pixel 3"),
        Product(listOf("Samsung"), "$", "USD", "Galaxy S9", 11, 1, false, 10, "samsung-s9-device-info.png", "Galaxy S9"),
        Product(listOf("OnePlus"), "$", "USD", "One Plus 8T", 12, 1, false, 10, "OnePlus8-device-info.png", "One Plus 8T"),
        
        Product(listOf("Samsung"), "$", "USD", "Galaxy S9", 13, 1, false, 10, "samsung-s9-device-info.png", "Galaxy S9"),
        Product(listOf("OnePlus"), "$", "USD", "One Plus 8 Pro", 14, 1, false, 10, "OnePlus8-device-info.png", "One Plus 8 Pro"),
        Product(listOf("Apple"), "$", "USD", "iPhone XS Max", 15, 1, false, 10, "infocard.png", "iPhone XS Max"),
        
        Product(listOf("Apple"), "$", "USD", "iPhone 12 Pro", 16, 1, false, 10, "iPhone12Pro-device-info.png", "iPhone 12 Pro"),
        Product(listOf("Samsung"), "$", "USD", "Galaxy Note 20", 17, 1, false, 10, "Note20-device-info.png", "Galaxy Note 20"),
        Product(listOf("Google"), "$", "USD", "Pixel 4", 18, 1, false, 10, "GooglePixel4-device-info.png", "Pixel 4"),
        
        Product(listOf("OnePlus"), "$", "USD", "One Plus 8", 19, 1, false, 10, "OnePlus8-device-info.png", "One Plus 8"),
        Product(listOf("Samsung"), "$", "USD", "Galaxy Note 20 Ultra", 20, 1, false, 10, "Note20Ultra-device-info.png", "Galaxy Note 20 Ultra"),
        Product(listOf("Apple"), "$", "USD", "iPhone 11 Pro", 21, 1, false, 10, "infocardiphone11Pro.png", "iPhone 11 Pro")
    )
}

@Composable
private fun OrderItem(product: Product) {
    val productImage = when {
        product.title.contains("Pixel", true) -> R.drawable.ic_google_pixel_image
        product.title.contains("Galaxy", true) -> R.drawable.ic_samsung_image
        product.title.contains("Plus", true) -> R.drawable.ic_one_plus_image
        else -> R.drawable.ic_iphone_images
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = productImage),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
            
            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Status: Delivered",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(product.currencyFormat, fontSize = 14.sp)
                    Text(
                        text = product.price.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
