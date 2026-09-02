package com.example.productcatalog.model

import com.example.productcatalog.domain.model.Product
import com.example.productcatalog.domain.model.Vendor

data class ProductsUiState(
    val products: List<Product> = emptyList(),
    val cartProducts: List<Product> = emptyList(), // Store the actual items here
    val selectedVendor: Vendor? = null,
    val cartCount: Int = 0,
    val favoriteProductIds: Set<Int> = emptySet(),
    val orderedProductIds: Set<Int> = emptySet(),
    val isLoading: Boolean = false
)
