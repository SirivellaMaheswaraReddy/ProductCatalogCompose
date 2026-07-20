package com.example.productcatalog.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "settings")

class UserPreferences(context: Context) {
    private val dataStore = context.dataStore

    private companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val FAVORITE_PRODUCT_IDS = stringSetPreferencesKey("favorite_product_ids")
        val ORDERED_PRODUCT_IDS = stringSetPreferencesKey("ordered_product_ids")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    val isLoggedIn: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { it[IS_LOGGED_IN] ?: false }

    val darkMode: Flow<Boolean?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { it[DARK_MODE] }

    val favoriteProductIds: Flow<Set<Int>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[FAVORITE_PRODUCT_IDS]?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
        }

    val orderedProductIds: Flow<Set<Int>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[ORDERED_PRODUCT_IDS]?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
        }

    suspend fun saveLoginStatus(status: Boolean) {
        dataStore.edit { it[IS_LOGGED_IN] = status }
    }

    suspend fun setDarkMode(enabled: Boolean?) {
        dataStore.edit { preferences ->
            if (enabled == null) {
                preferences.remove(DARK_MODE)
            } else {
                preferences[DARK_MODE] = enabled
            }
        }
    }

    suspend fun toggleFavorite(productId: Int) {
        dataStore.edit { preferences ->
            val currentIds = preferences[FAVORITE_PRODUCT_IDS] ?: emptySet()
            val productIdStr = productId.toString()
            val newIds = if (currentIds.contains(productIdStr)) {
                currentIds - productIdStr
            } else {
                currentIds + productIdStr
            }
            preferences[FAVORITE_PRODUCT_IDS] = newIds
        }
    }

    suspend fun addOrders(productIds: List<Int>) {
        dataStore.edit { preferences ->
            val currentIds = preferences[ORDERED_PRODUCT_IDS] ?: emptySet()
            val newIds = currentIds + productIds.map { it.toString() }
            preferences[ORDERED_PRODUCT_IDS] = newIds
        }
    }
}
