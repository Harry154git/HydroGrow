// File: di/ProvideModule.kt

package com.pemrogamanmobile.hydrogrow.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.pemrogamanmobile.hydrogrow.data.local.datastore.PreferenceManager
import com.pemrogamanmobile.hydrogrow.data.local.room.AppDatabase
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.*
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.AuthService
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices.*
import com.pemrogamanmobile.hydrogrow.data.remote.service.gemini.GeminiApiClient
import com.pemrogamanmobile.hydrogrow.data.remote.service.gemini.GeminiApiService
import com.pemrogamanmobile.hydrogrow.data.remote.service.plantnet.PlantNetApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProvideModule {

    // ... (provider untuk Database, DAO, Firebase, dll. tetap sama) ...
    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, "hydrogrow_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideGardenDao(db: AppDatabase): GardenDao = db.gardenDao()

    @Provides
    fun providePlantDao(db: AppDatabase): PlantDao = db.plantDao()

    @Provides
    fun provideChatBotDao(db: AppDatabase): ChatBotDao = db.chatBotDao()

    @Provides
    fun provideGameDao(db: AppDatabase): GameDao = db.gameDao()

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
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    // TAMBAHKAN BLOK INI
    @Provides
    @Singleton
    fun provideCrashlytics(): FirebaseCrashlytics = FirebaseCrashlytics.getInstance()
    // -------------------

    @Provides
    @Singleton
    fun provideAuthService(
        auth: FirebaseAuth,
        crashlytics: FirebaseCrashlytics // <-- Tambahkan parameter ini
    ): AuthService = AuthService(auth, crashlytics) // <-- Lewatkan ke konstruktor

    @Provides
    @Singleton
    fun provideChatBotService(db: FirebaseFirestore): ChatBotService = ChatBotService(db)

    @Provides
    @Singleton
    fun provideGameService(db: FirebaseFirestore): GameService = GameService(db)

    @Provides
    @Singleton
    fun provideGardenService(db: FirebaseFirestore): GardenService = GardenService(db)

    @Provides
    @Singleton
    fun providePlantService(db: FirebaseFirestore): PlantService = PlantService(db)

    // --- Remote API Service Providers ---
    @Provides
    @Singleton
    fun provideGeminiApiService(): GeminiApiService {
        val apiKey = "AIzaSyCi5bwp6JVJ3R5CUzbLroxKcwtYek_dax4"
        return GeminiApiClient.create(apiKey)
    }

    // --- TAMBAHKAN BLOK INI UNTUK PLANTNET ---
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun providePlantNetApiService(client: OkHttpClient): PlantNetApiService {
        return Retrofit.Builder()
            .baseUrl("https://my-api.plantnet.org/") // Base URL untuk Pl@ntNet API
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(PlantNetApiService::class.java)
    }
    // ------------------------------------------
}
