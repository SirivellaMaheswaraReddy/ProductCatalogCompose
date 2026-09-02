package com.example.productcatalog.domain.repository

import com.example.productcatalog.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): List<Product>
    val favoriteProductIds: Flow<Set<Int>>
    val orderedProductIds: Flow<Set<Int>>
    suspend fun toggleFavorite(productId: Int)
    suspend fun addOrders(productIds: List<Int>)
}
