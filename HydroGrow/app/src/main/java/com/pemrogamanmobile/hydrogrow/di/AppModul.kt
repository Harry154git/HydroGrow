package com.pemrogamanmobile.hydrogrow.di

import com.pemrogamanmobile.hydrogrow.data.repository.GardenRepositoryImpl
import com.pemrogamanmobile.hydrogrow.data.repository.ChatBotRepositoryImpl
import com.pemrogamanmobile.hydrogrow.data.repository.PlantRepositoryImpl
import com.pemrogamanmobile.hydrogrow.data.repository.PreferencesRepositoryImpl
import com.pemrogamanmobile.hydrogrow.data.repository.AuthRepositoryImpl
import com.pemrogamanmobile.hydrogrow.data.repository.PostingRepositoryImpl
import com.pemrogamanmobile.hydrogrow.domain.repository.GardenRepository
import com.pemrogamanmobile.hydrogrow.domain.repository.ChatBotRepository
import com.pemrogamanmobile.hydrogrow.domain.repository.PlantRepository
import com.pemrogamanmobile.hydrogrow.domain.repository.PreferencesRepository
import com.pemrogamanmobile.hydrogrow.domain.repository.AuthRepository
import com.pemrogamanmobile.hydrogrow.domain.repository.PostingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindGardenRepository(
        impl: GardenRepositoryImpl
    ): GardenRepository

    @Binds
    @Singleton
    abstract fun bindGeminiRepository(
        impl: ChatBotRepositoryImpl
    ): ChatBotRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindPlantRepository(
        impl: PlantRepositoryImpl
    ): PlantRepository

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        impl: PreferencesRepositoryImpl
    ): PreferencesRepository

    @Binds
    @Singleton
    abstract fun bindPostingRepository(
        postingRepositoryImpl: PostingRepositoryImpl
    ): PostingRepository

}