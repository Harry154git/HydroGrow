package com.pemrogamanmobile.hydrogrow.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pemrogamanmobile.hydrogrow.data.local.datastore.PreferenceManager
import com.pemrogamanmobile.hydrogrow.data.local.room.AppDatabase
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.GardenDao
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.PlantDao
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.UserDao
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.AuthService
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices.ChatBotService
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices.GameService
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices.GardenService
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices.PlantService
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices.PostingService
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices.UserService
import com.pemrogamanmobile.hydrogrow.data.remote.service.gemini.GeminiApiClient
import com.pemrogamanmobile.hydrogrow.data.remote.service.gemini.GeminiApiService
import com.pemrogamanmobile.hydrogrow.domain.repository.GardenRepository
import com.pemrogamanmobile.hydrogrow.domain.repository.PlantRepository
import com.pemrogamanmobile.hydrogrow.domain.usecase.GardenUseCase
import com.pemrogamanmobile.hydrogrow.domain.usecase.PlantUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
public class ProvideModule {
    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "hydrogrow_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideGardenDao(db: AppDatabase): GardenDao = db.gardenDao()

    @Provides
    fun providePlantDao(db: AppDatabase): PlantDao = db.plantDao()

    @Provides
    @Singleton
    fun providePreferenceManager(@ApplicationContext context: Context): PreferenceManager {
        return PreferenceManager(context)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthService(auth: FirebaseAuth): AuthService {
        return AuthService(auth)
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideChatBotService(db: FirebaseFirestore): ChatBotService = ChatBotService(db)

    @Provides
    @Singleton
    fun providePostingService(db: FirebaseFirestore): PostingService = PostingService(db)

    @Provides
    @Singleton
    fun provideGameService(db: FirebaseFirestore): GameService = GameService(db)

    @Provides
    @Singleton
    fun provideGardenService(db: FirebaseFirestore): GardenService = GardenService(db)

    @Provides
    @Singleton
    fun providePlantService(db: FirebaseFirestore): PlantService = PlantService(db)

    @Provides
    @Singleton
    fun provideUserService(db: FirebaseFirestore): UserService = UserService(db)

    @Provides
    @Singleton
    fun provideGeminiApiService(): GeminiApiService {
        val apiKey = "AIzaSyCi5bwp6JVJ3R5CUzbLroxKcwtYek_dax4"
        return GeminiApiClient.create(apiKey)
    }

    @Provides
    @Singleton
    fun provideGardenUseCase(repository: GardenRepository): GardenUseCase {
        return GardenUseCase(repository)
    }

    @Provides
    @Singleton
    fun providePlantUseCase(repository: PlantRepository): PlantUseCase {
        return PlantUseCase(repository)
    }

}