package com.example.productcatalog.domain.usecase

import com.example.productcatalog.domain.model.Product
import com.example.productcatalog.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetFavoritesUseCase(private val repository: ProductRepository) {
    operator fun invoke(): Flow<List<Product>> {
        val allProducts = repository.getProducts()
        return repository.favoriteProductIds.map { favoriteIds ->
            allProducts.filter { it.id in favoriteIds }
        }
    }
}
