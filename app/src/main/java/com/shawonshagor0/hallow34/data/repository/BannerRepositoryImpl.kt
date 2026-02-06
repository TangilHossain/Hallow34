package com.shawonshagor0.hallow34.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.shawonshagor0.hallow34.data.mapper.toDomain
import com.shawonshagor0.hallow34.data.mapper.toDto
import com.shawonshagor0.hallow34.data.model.BannerDto
import com.shawonshagor0.hallow34.domain.model.Banner
import com.shawonshagor0.hallow34.domain.repository.BannerRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class BannerRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : BannerRepository {

    private val bannersCollection = firestore.collection("banners")

    override fun getActiveBanner(): Flow<Banner?> = callbackFlow {
        val listener = bannersCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(5)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }

                // Filter for active banners client-side to avoid composite index requirement
                val banner = snapshot?.documents
                    ?.mapNotNull { doc ->
                        val isActive = doc.getBoolean("isActive") ?: true
                        if (isActive) {
                            BannerDto(
                                id = doc.id,
                                message = doc.getString("message") ?: "",
                                createdAt = doc.getLong("createdAt") ?: 0L,
                                isActive = isActive
                            ).toDomain()
                        } else null
                    }
                    ?.firstOrNull()
                trySend(banner)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun createBanner(message: String): Banner {
        val bannerId = UUID.randomUUID().toString()
        val banner = Banner(
            id = bannerId,
            message = message,
            createdAt = System.currentTimeMillis(),
            isActive = true
        )

        bannersCollection.document(bannerId)
            .set(banner.toDto())
            .await()

        return banner
    }

    override suspend fun deleteBanner(bannerId: String) {
        bannersCollection.document(bannerId)
            .delete()
            .await()
    }

    override suspend fun getAllBanners(): List<Banner> {
        val snapshot = bannersCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()

        return snapshot.documents.map { doc ->
            BannerDto(
                id = doc.id,
                message = doc.getString("message") ?: "",
                createdAt = doc.getLong("createdAt") ?: 0L,
                isActive = doc.getBoolean("isActive") ?: true
            ).toDomain()
        }
    }
}
