package com.example.productcatalog.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productcatalog.data.ProductRepository
import com.example.productcatalog.data.UserPreferences
import com.example.productcatalog.model.Product
import com.example.productcatalog.model.ProductsUiState
import com.example.productcatalog.model.Vendor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductsViewModel(private val userPreferences: UserPreferences) : ViewModel() {
    private val allProducts = ProductRepository.getProducts()

    private val _uiState = MutableStateFlow(
        ProductsUiState(
            products = allProducts,
            selectedVendor = null
        )
    )
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()

    val darkMode = userPreferences.darkMode

    init {
        viewModelScope.launch {
            userPreferences.favoriteProductIds.collect { ids ->
                _uiState.update { it.copy(favoriteProductIds = ids) }
            }
        }
        viewModelScope.launch {
            userPreferences.orderedProductIds.collect { ids ->
                _uiState.update { it.copy(orderedProductIds = ids) }
            }
        }
    }

    fun selectVendor(vendor: Vendor?) {
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

    fun clearCart() {
        val productIds = _uiState.value.cartProducts.map { it.id }
        viewModelScope.launch {
            userPreferences.addOrders(productIds)
        }
        _uiState.update { currentState ->
            currentState.copy(
                cartProducts = emptyList(),
                cartCount = 0
            )
        }
    }

    fun toggleFavorite(productId: Int) {
        viewModelScope.launch {
            userPreferences.toggleFavorite(productId)
        }
    }

    fun setDarkMode(enabled: Boolean?) {
        viewModelScope.launch {
            userPreferences.setDarkMode(enabled)
        }
    }

    fun getFavoriteProducts(): List<Product> {
        val favoriteIds = _uiState.value.favoriteProductIds
        return allProducts.filter { it.id in favoriteIds }
    }

    fun getOrderedProducts(): List<Product> {
        val orderedIds = _uiState.value.orderedProductIds
        return allProducts.filter { it.id in orderedIds }
    }

    // This is a private extension function inside the class
    private fun List<Product>.filterByVendor(vendor: Vendor): List<Product> =
        this.filter {
            it.availableSizes.get(0).contains(vendor.label, true)
//            it.availableSizes.contains(vendor.label)
        }
}