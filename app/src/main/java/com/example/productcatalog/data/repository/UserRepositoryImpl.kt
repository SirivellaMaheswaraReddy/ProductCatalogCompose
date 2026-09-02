package com.example.productcatalog.data.repository

import com.example.productcatalog.data.UserPreferences
import com.example.productcatalog.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl(
    private val userPreferences: UserPreferences
) : UserRepository {
    override val isLoggedIn: Flow<Boolean> = userPreferences.isLoggedIn
    override val loggedInUsername: Flow<String?> = userPreferences.loggedInUsername
    override val darkMode: Flow<Boolean?> = userPreferences.darkMode

    override suspend fun saveLoginStatus(status: Boolean, username: String?) {
        userPreferences.saveLoginStatus(status, username)
    }

    override suspend fun clearUserData() {
        userPreferences.clearUserData()
    }

    override suspend fun setDarkMode(enabled: Boolean?) {
        userPreferences.setDarkMode(enabled)
    }
}
