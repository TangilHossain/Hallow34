package com.shawonshagor0.hallow34.domain.model

data class Banner(
    val id: String = "",
    val message: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)
