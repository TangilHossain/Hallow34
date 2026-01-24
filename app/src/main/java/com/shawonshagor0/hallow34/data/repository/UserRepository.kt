package com.shawonshagor0.hallow34.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.shawonshagor0.hallow34.data.model.User

class UserRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun getAllUsers(onResult: (List<User>) -> Unit) {
        firestore.collection("users")
            .get()
            .addOnSuccessListener { snapshot ->
                val users = snapshot.documents.map { doc ->
                    User(
                        bpNumber = doc.id,
                        fullName = doc.getString("fullName") ?: "",
                        designation = doc.getString("designation") ?: "",
                        phone = doc.getString("phone") ?: "",
                        bloodGroup = doc.getString("bloodGroup") ?: "",
                        email = doc.getString("email") ?: "",
                        district = doc.getString("district") ?: "",
                        range = doc.getString("range") ?: "",
                        profileImageUrl = doc.getString("profileImageUrl") ?: ""
                    )
                }
                onResult(users)
            }
    }
}
