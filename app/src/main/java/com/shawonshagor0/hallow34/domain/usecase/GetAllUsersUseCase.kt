package com.shawonshagor0.hallow34.domain.usecase

import com.shawonshagor0.hallow34.domain.model.User
import com.shawonshagor0.hallow34.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAllUsersUseCase @Inject constructor(
    private val repository: UserRepository
) {
    // Now returns Flow<List<User>>
    operator fun invoke(): Flow<List<User>> = flow {
        val users = repository.getAllUsers()  // suspend function returning List<User>
        emit(users)  // emit into the Flow
    }
}
