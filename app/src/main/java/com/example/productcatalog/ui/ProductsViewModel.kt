package com.example.productcatalog.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productcatalog.domain.model.Product
import com.example.productcatalog.domain.model.Vendor
import com.example.productcatalog.domain.repository.ProductRepository
import com.example.productcatalog.domain.repository.UserRepository
import com.example.productcatalog.domain.usecase.GetProductsUseCase
import com.example.productcatalog.domain.usecase.PlaceOrderUseCase
import com.example.productcatalog.domain.usecase.ToggleFavoriteUseCase
import com.example.productcatalog.model.ProductsUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductsViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val allProducts = getProductsUseCase()

    private val _uiState = MutableStateFlow(
        ProductsUiState(
            products = allProducts,
            selectedVendor = null
        )
    )
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()

    val darkMode = userRepository.darkMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    init {
        refresh()
        viewModelScope.launch {
            productRepository.favoriteProductIds.collect { ids ->
                _uiState.update { it.copy(favoriteProductIds = ids) }
            }
        }
        viewModelScope.launch {
            productRepository.orderedProductIds.collect { ids ->
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
            placeOrderUseCase(productIds)
        }
        _uiState.update { currentState ->
            currentState.copy(
                cartProducts = emptyList(),
                cartCount = 0
            )
        }
    }

    fun clearAllData() {
        _uiState.update { currentState ->
            currentState.copy(
                cartProducts = emptyList(),
                cartCount = 0,
                favoriteProductIds = emptySet(),
                orderedProductIds = emptySet()
            )
        }
    }

    fun toggleFavorite(productId: Int) {
        viewModelScope.launch {
            toggleFavoriteUseCase(productId)
        }
    }

    fun setDarkMode(enabled: Boolean?) {
        viewModelScope.launch {
            userRepository.setDarkMode(enabled)
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

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1200)

            val refreshedProducts = getProductsUseCase()
            _uiState.update { it.copy(
                isLoading = false,
                products = refreshedProducts,
                selectedVendor = null
            ) }
        }
    }

    private fun List<Product>.filterByVendor(vendor: Vendor): List<Product> =
        this.filter {
            it.availableSizes.getOrNull(0)?.contains(vendor.label, true) == true
        }
}
