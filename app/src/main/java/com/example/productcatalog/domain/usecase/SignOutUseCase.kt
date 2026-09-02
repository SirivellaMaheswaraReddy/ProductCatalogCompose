package com.example.productcatalog.domain.usecase

import com.example.productcatalog.domain.repository.UserRepository

class SignOutUseCase(private val repository: UserRepository) {
    suspend operator fun invoke() = repository.clearUserData()
}
