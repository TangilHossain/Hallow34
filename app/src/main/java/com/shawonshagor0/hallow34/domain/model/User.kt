package com.shawonshagor0.hallow34.domain.model

data class User(
    val bpNumber: String = "",
    val fullName: String = "",
    val designation: String = "",
    val district: String = "",
    val currentRange: String = "",
    val bloodGroup: String = "",
    val phone: String = "",
    val email: String = "",
    val imageUrl: String = ""   // will come from Cloudinary later
)
