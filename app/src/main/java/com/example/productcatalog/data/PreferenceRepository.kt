package com.example.productcatalog.data

import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {
    val isLoggedIn: Flow<Boolean>
    suspend fun updateLoginStatus(status: Boolean)
}
class PreferenceRepositoryImpl(private val userPreferences: UserPreferences) : PreferenceRepository {
    override val isLoggedIn = userPreferences.isLoggedIn
    override suspend fun updateLoginStatus(status: Boolean) = userPreferences.saveLoginStatus(status)
}