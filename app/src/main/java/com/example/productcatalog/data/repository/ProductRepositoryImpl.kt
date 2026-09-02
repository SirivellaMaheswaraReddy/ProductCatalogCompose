package com.example.productcatalog.data.repository

import com.example.productcatalog.data.ProductRepository
import com.example.productcatalog.data.UserPreferences
import com.example.productcatalog.domain.model.Product
import com.example.productcatalog.domain.repository.ProductRepository as DomainProductRepository
import kotlinx.coroutines.flow.Flow

class ProductRepositoryImpl(
    private val userPreferences: UserPreferences
) : DomainProductRepository {

    override fun getProducts(): List<Product> {
        return ProductRepository.getProducts()
    }

    override val favoriteProductIds: Flow<Set<Int>> = userPreferences.favoriteProductIds
    override val orderedProductIds: Flow<Set<Int>> = userPreferences.orderedProductIds

    override suspend fun toggleFavorite(productId: Int) {
        userPreferences.toggleFavorite(productId)
    }

    override suspend fun addOrders(productIds: List<Int>) {
        userPreferences.addOrders(productIds)
    }
}
