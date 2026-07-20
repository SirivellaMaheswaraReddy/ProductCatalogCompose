package com.example.productcatalog.model

data class ProductResponse(
    val products: List<Product>
)

data class Product(
    val availableSizes: List<String>,
    val currencyFormat: String,
    val currencyId: String,
    val description: String,
    val id: Int,
    val installments: Int,
    val isFav: Boolean,
    val price: Int,
    val sku: String,
    val title: String
)

enum class Vendor(val label: String) {
    APPLE("Apple"),
    SAMSUNG("Samsung"),
    GOOGLE("Google"),
    ONE_PLUS("OnePlus")
}

data class ProductsUiState(
    val products: List<Product> = emptyList(),
    val cartProducts: List<Product> = emptyList(), // Store the actual items here
    val selectedVendor: Vendor? = null,
    val cartCount: Int = 0,
    val favoriteProductIds: Set<Int> = emptySet(),
    val orderedProductIds: Set<Int> = emptySet()
)
