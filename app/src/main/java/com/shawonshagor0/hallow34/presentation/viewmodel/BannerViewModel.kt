package com.shawonshagor0.hallow34.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shawonshagor0.hallow34.domain.model.Banner
import com.shawonshagor0.hallow34.domain.repository.BannerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BannerViewModel @Inject constructor(
    private val bannerRepository: BannerRepository
) : ViewModel() {

    var banners by mutableStateOf<List<Banner>>(emptyList())
        private set

    var activeBanner by mutableStateOf<Banner?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf("")
        private set

    var successMessage by mutableStateOf("")
        private set

    init {
        loadBanners()
        observeActiveBanner()
    }

    private fun observeActiveBanner() {
        viewModelScope.launch {
            bannerRepository.getActiveBanner()
                .catch { /* Handle error */ }
                .collect { banner ->
                    activeBanner = banner
                }
        }
    }

    fun loadBanners() {
        viewModelScope.launch {
            isLoading = true
            try {
                banners = bannerRepository.getAllBanners()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to load banners"
            } finally {
                isLoading = false
            }
        }
    }

    fun createBanner(message: String) {
        if (message.isBlank()) {
            errorMessage = "Banner message cannot be empty"
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                bannerRepository.createBanner(message)
                successMessage = "Banner created successfully"
                loadBanners()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to create banner"
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteBanner(bannerId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                bannerRepository.deleteBanner(bannerId)
                successMessage = "Banner deleted successfully"
                loadBanners()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to delete banner"
            } finally {
                isLoading = false
            }
        }
    }

    fun clearMessages() {
        errorMessage = ""
        successMessage = ""
    }
}
