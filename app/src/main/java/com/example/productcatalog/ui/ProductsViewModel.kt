package com.example.productcatalog.ui

import androidx.lifecycle.ViewModel
import com.example.productcatalog.data.ProductRepository
import com.example.productcatalog.model.Product
import com.example.productcatalog.model.ProductsUiState
import com.example.productcatalog.model.Vendor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProductsViewModel : ViewModel() {
    private val allProducts = ProductRepository.getProducts()

    private val _uiState = MutableStateFlow(
        ProductsUiState(
            products = allProducts,
            selectedVendor = null
        )
    )
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()

    fun selectVendor(vendor: Vendor) {
        _uiState.update { currentState ->
            val newVendor = if (currentState.selectedVendor == vendor) null else vendor

            currentState.copy(
                selectedVendor = newVendor,
                products = if (newVendor == null) allProducts else allProducts.filterByVendor(newVendor)
            )
        }
    }

    fun addToCart(product: Product) {
        _uiState.update { currentState ->
            val updatedCart = currentState.cartProducts + product
            currentState.copy(
                cartProducts = updatedCart,
                cartCount = updatedCart.size
            )
        }
    }

    fun removeFromCart(product: Product) {
        _uiState.update { currentState ->
            val updatedCart = currentState.cartProducts.filterNot { it.id == product.id }
            currentState.copy(
                cartProducts = updatedCart,
                cartCount = updatedCart.size
            )
        }
    }

    fun toggleFavorite(productId: Int) {
        _uiState.update { state ->
            val ids = state.favoriteProductIds.toMutableSet()
            if (!ids.add(productId)) ids.remove(productId)
            state.copy(favoriteProductIds = ids)
        }
    }

    // This is a private extension function inside the class
    private fun List<Product>.filterByVendor(vendor: Vendor): List<Product> =
        this.filter {
            it.availableSizes.get(0).contains(vendor.label, true)
//            it.availableSizes.contains(vendor.label)
        }
}