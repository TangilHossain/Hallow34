package com.shawonshagor0.hallow34.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.shawonshagor0.hallow34.domain.model.User

class UserRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun saveUser(
        user: User,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        firestore
            .collection("users")
            .document(user.bpNumber) // BP number = unique ID
            .set(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Unknown error") }
    }
}
