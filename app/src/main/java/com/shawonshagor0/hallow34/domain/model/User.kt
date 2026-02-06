package com.shawonshagor0.hallow34.domain.model

data class User(
    val bpNumber: String = "",
    val fullName: String = "",
    val designation: String = "",
    val district: String = "",
    val postingPlace: String = "",
    val bloodGroup: String = "",
    val phone: String = "",
    val email: String = "",
    val password: String = "",
    val imageUrl: String = "",
    val facebookProfileLink: String = "" // Added Facebook profile link field
)
