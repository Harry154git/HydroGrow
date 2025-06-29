package com.pemrogamanmobile.hydrogrow.di

import com.pemrogamanmobile.hydrogrow.data.repository.GardenRepositoryImpl
import com.pemrogamanmobile.hydrogrow.data.repository.GeminiRepositoryImpl
import com.pemrogamanmobile.hydrogrow.data.repository.PlantRepositoryImpl
import com.pemrogamanmobile.hydrogrow.data.repository.PreferencesRepositoryImpl
import com.pemrogamanmobile.hydrogrow.data.repository.UserRepositoryImpl
import com.pemrogamanmobile.hydrogrow.domain.repository.GardenRepository
import com.pemrogamanmobile.hydrogrow.domain.repository.GeminiRepository
import com.pemrogamanmobile.hydrogrow.domain.repository.PlantRepository
import com.pemrogamanmobile.hydrogrow.domain.repository.PreferencesRepository
import com.pemrogamanmobile.hydrogrow.domain.repository.UserRepository
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
        impl: GeminiRepositoryImpl
    ): GeminiRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

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

}