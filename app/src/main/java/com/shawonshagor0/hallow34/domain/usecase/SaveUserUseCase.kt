package com.shawonshagor0.hallow34.domain.usecase

import com.shawonshagor0.hallow34.domain.model.User
import com.shawonshagor0.hallow34.domain.repository.UserRepository

class SaveUserUseCase(
    private val repository: UserRepository
) {
    // Call this function to save a user
    suspend operator fun invoke(user: User) {
        repository.saveUser(user)
    }
}
