package com.example.productcatalog.domain.usecase

import com.example.productcatalog.domain.repository.ProductRepository

class PlaceOrderUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(productIds: List<Int>) = repository.addOrders(productIds)
}
