package com.example.productcatalog.domain.usecase

import com.example.productcatalog.domain.repository.ProductRepository

class ToggleFavoriteUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(productId: Int) = repository.toggleFavorite(productId)
}
