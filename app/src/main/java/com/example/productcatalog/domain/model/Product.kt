package com.example.productcatalog.domain.model

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
