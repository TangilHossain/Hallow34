package com.shawonshagor0.hallow34.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.shawonshagor0.hallow34.data.mapper.toDomain
import com.shawonshagor0.hallow34.data.mapper.toDto
import com.shawonshagor0.hallow34.data.model.UserDto
import com.shawonshagor0.hallow34.domain.model.User
import com.shawonshagor0.hallow34.domain.repository.UserRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    override suspend fun getAllUsers(): List<User> {
        val snapshot = firestore.collection("users").get().await()
        return snapshot.documents.map { doc ->
            UserDto(
                bpNumber = doc.id,
                fullName = doc.getString("fullName") ?: "",
                designation = doc.getString("designation") ?: "",
                district = doc.getString("district") ?: "",
                currentRange = doc.getString("currentRange") ?: "",
                bloodGroup = doc.getString("bloodGroup") ?: "",
                phone = doc.getString("phone") ?: "",
                email = doc.getString("email") ?: "",
                password = doc.getString("password") ?: "",
                imageUrl = doc.getString("imageUrl") ?: ""
            ).toDomain()
        }
    }

    override suspend fun saveUser(user: User) {
        val dto = user.toDto()
        firestore.collection("users")
            .document(user.bpNumber)
            .set(dto)
            .await()
    }
}
