package com.shawonshagor0.hallow34.di

import com.google.firebase.firestore.FirebaseFirestore
import com.shawonshagor0.hallow34.data.remote.CloudinaryUploader
import com.shawonshagor0.hallow34.data.repository.UserRepositoryImpl
import com.shawonshagor0.hallow34.domain.repository.UserRepository
import com.shawonshagor0.hallow34.domain.usecase.GetAllUsersUseCase
import com.shawonshagor0.hallow34.domain.usecase.SaveUserUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideCloudinaryUploader(): CloudinaryUploader {
        return CloudinaryUploader()
    }

    @Provides
    @Singleton
    fun provideUserRepository(firestore: FirebaseFirestore): UserRepository {
        return UserRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideSaveUserUseCase(userRepository: UserRepository): SaveUserUseCase {
        return SaveUserUseCase(userRepository)
    }

    @Provides
    @Singleton
    fun provideGetAllUsersUseCase(userRepository: UserRepository): GetAllUsersUseCase {
        return GetAllUsersUseCase(userRepository)
    }
}
