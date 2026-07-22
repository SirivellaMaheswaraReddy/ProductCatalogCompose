package com.example.productcatalog.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productcatalog.data.UserPreferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false
)

class LoginViewModel(private val userPreferences: UserPreferences) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    val isLoggedIn: StateFlow<Boolean?> = userPreferences.isLoggedIn
        .map { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        // Observe DataStore and update UI State automatically
        viewModelScope.launch {
            userPreferences.isLoggedIn.collect { status ->
                _uiState.update {
                    Log.e("M333", "status : $status")
                    it.copy(isLoggedIn = status)
                }
            }
        }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun signIn(onSuccess: () -> Unit) {
        val email = _uiState.value.email
        val password = _uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Fields cannot be empty") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Logic to save login status
                userPreferences.saveLoginStatus(true)
                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Login Failed", isLoading = false) }
            }
        }
    }

    fun signOut(onSignOut: () -> Unit = {}) {
        viewModelScope.launch {
            userPreferences.clearUserData()
            onSignOut()
        }
    }
}
