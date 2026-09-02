package com.example.productcatalog.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val isLoggedIn: Flow<Boolean>
    val loggedInUsername: Flow<String?>
    suspend fun saveLoginStatus(status: Boolean, username: String? = null)
    suspend fun clearUserData()
    val darkMode: Flow<Boolean?>
    suspend fun setDarkMode(enabled: Boolean?)
}
