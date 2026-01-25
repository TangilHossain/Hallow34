package com.shawonshagor0.hallow34.domain.repository

import com.shawonshagor0.hallow34.domain.model.User

interface UserRepository {
    suspend fun getAllUsers(): List<User>
    suspend fun saveUser(user: User)
}
