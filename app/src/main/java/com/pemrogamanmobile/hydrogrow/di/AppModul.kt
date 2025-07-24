package com.pemrogamanmobile.hydrogrow.di

import com.pemrogamanmobile.hydrogrow.data.repository.*
import com.pemrogamanmobile.hydrogrow.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGardenRepository(impl: GardenRepositoryImpl): GardenRepository

    @Binds
    @Singleton
    abstract fun bindChatBotRepository(impl: ChatBotRepositoryImpl): ChatBotRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindPlantRepository(impl: PlantRepositoryImpl): PlantRepository

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(impl: PreferencesRepositoryImpl): PreferencesRepository

    @Binds
    @Singleton
    abstract fun bindGameRepository(impl: GameRepositoryImpl): GameRepository

    @Binds
    @Singleton
    abstract fun bindAiRepository(impl: AiRepositoryImpl): AiRepository
    // ---------------------------------
}
