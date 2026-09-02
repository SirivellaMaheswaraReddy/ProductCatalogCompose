package com.example.productcatalog.domain.usecase

import com.example.productcatalog.domain.repository.UserRepository

class SignInUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(username: String) {
        repository.saveLoginStatus(true, username)
    }
}
