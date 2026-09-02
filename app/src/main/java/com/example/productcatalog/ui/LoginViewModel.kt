package com.example.productcatalog.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productcatalog.domain.repository.UserRepository
import com.example.productcatalog.domain.usecase.SignInUseCase
import com.example.productcatalog.domain.usecase.SignOutUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false
)

class LoginViewModel(
    private val userRepository: UserRepository,
    private val signInUseCase: SignInUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    val isLoggedIn: StateFlow<Boolean?> = userRepository.isLoggedIn
        .map { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val loggedInUsername: StateFlow<String?> = userRepository.loggedInUsername
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        viewModelScope.launch {
            userRepository.isLoggedIn.collect { status ->
                _uiState.update { it.copy(isLoggedIn = status) }
            }
        }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun resetState() {
        _uiState.update { LoginUiState(isLoggedIn = it.isLoggedIn) }
    }

    fun signIn(onSuccess: () -> Unit) {
        val email = _uiState.value.email
        val password = _uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Fields cannot be empty") }
            return
        }

        val validCredentials = mapOf(
            "image_not_loader_user" to "testingfun99",
            "demouser" to "testingfun99",
            "locked_user" to "testingfun99",
            "fav_user" to "testingfun99",
            "existing_order_user" to "testingfun99"
        )

        if (validCredentials[email] != password) {
            _uiState.update { it.copy(errorMessage = "Invalid email or password") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                signInUseCase(email)
                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Login Failed", isLoading = false) }
            }
        }
    }

    fun signOut(onSignOut: () -> Unit = {}) {
        viewModelScope.launch {
            signOutUseCase()
            onSignOut()
        }
    }
}
