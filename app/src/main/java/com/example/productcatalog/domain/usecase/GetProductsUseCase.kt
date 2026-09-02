package com.example.productcatalog.domain.usecase

import com.example.productcatalog.domain.model.Product
import com.example.productcatalog.domain.repository.ProductRepository

class GetProductsUseCase(private val repository: ProductRepository) {
    operator fun invoke(): List<Product> = repository.getProducts()
}
