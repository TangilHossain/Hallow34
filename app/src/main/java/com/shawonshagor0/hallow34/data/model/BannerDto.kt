package com.shawonshagor0.hallow34.data.model

data class BannerDto(
    val id: String = "",
    val message: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)
