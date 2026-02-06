package com.shawonshagor0.hallow34.data.mapper

import com.shawonshagor0.hallow34.data.model.BannerDto
import com.shawonshagor0.hallow34.domain.model.Banner

fun BannerDto.toDomain(): Banner = Banner(
    id = id,
    message = message,
    createdAt = createdAt,
    isActive = isActive
)

fun Banner.toDto(): BannerDto = BannerDto(
    id = id,
    message = message,
    createdAt = createdAt,
    isActive = isActive
)
