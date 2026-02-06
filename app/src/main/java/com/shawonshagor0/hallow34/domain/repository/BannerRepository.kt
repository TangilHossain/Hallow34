package com.shawonshagor0.hallow34.domain.repository

import com.shawonshagor0.hallow34.domain.model.Banner
import kotlinx.coroutines.flow.Flow

interface BannerRepository {
    fun getActiveBanner(): Flow<Banner?>
    suspend fun createBanner(message: String): Banner
    suspend fun deleteBanner(bannerId: String)
    suspend fun getAllBanners(): List<Banner>
}
