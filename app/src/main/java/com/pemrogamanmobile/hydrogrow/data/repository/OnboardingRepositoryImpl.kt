package com.pemrogamanmobile.hydrogrow.data.repository

import android.util.Log
import com.pemrogamanmobile.hydrogrow.data.local.mapper.OnboardingPreferencesLocalMapper
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.OnboardingPreferencesDao
import com.pemrogamanmobile.hydrogrow.data.remote.mapper.OnboardingPreferencesDtoMapper
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices.OnboardingFirestoreService
import com.pemrogamanmobile.hydrogrow.domain.model.OnboardingPreferences
import com.pemrogamanmobile.hydrogrow.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class OnboardingRepositoryImpl @Inject constructor(
    private val firestoreService: OnboardingFirestoreService,
    private val localDao: OnboardingPreferencesDao,
    private val dtoMapper: OnboardingPreferencesDtoMapper,
    private val localMapper: OnboardingPreferencesLocalMapper
) : OnboardingRepository {

    /**
     * Mengambil data preferensi dengan strategi Network-First.
     */
    override fun getOnboardingPreferences(userId: String): Flow<OnboardingPreferences?> {
        return localDao.getPreferences(userId)
            .map { entity ->
                // Selalu map dari entity lokal ke domain model
                entity?.let { localMapper.toDomain(it) }
            }
            .onStart {
                // Blok .onStart akan dieksekusi setiap kali Flow ini mulai dikoleksi (diobservasi).
                // Ini adalah tempat yang tepat untuk memicu sinkronisasi dari jaringan.
                try {
                    // Panggil fungsi sinkronisasi
                    syncPreferences(userId)
                } catch (e: Exception) {
                    // Jika terjadi error jaringan (misal: offline), log error tersebut.
                    // Aplikasi tidak akan crash karena Flow akan tetap lanjut
                    // dengan data yang ada di cache lokal.
                    Log.e("OnboardingRepoImpl", "Network sync failed: ${e.message}")
                }
            }
    }

    /**
     * Menyimpan data ke kedua sumber: Firestore (remote) dan Room (local).
     * Logika ini sudah benar, remote dulu baru update local.
     */
    override suspend fun saveOnboardingPreferences(preferences: OnboardingPreferences) {
        // 1. Simpan ke Firestore
        val dto = dtoMapper.fromDomain(preferences)
        firestoreService.savePreferences(preferences.userId, dto)

        // 2. Simpan juga ke database lokal
        val entity = localMapper.fromDomain(preferences)
        localDao.upsertPreferences(entity)
    }

    /**
     * Fungsi untuk sinkronisasi data dari Firestore ke Room.
     * Fungsi ini sekarang dipanggil secara otomatis oleh getOnboardingPreferences.
     */
    override suspend fun syncPreferences(userId: String) {
        // Ambil data terbaru dari Firestore
        val remotePrefsDto = firestoreService.getPreferences(userId)
        if (remotePrefsDto != null) {
            // Jika ada, konversi dan simpan (update) ke database lokal
            val domainModel = dtoMapper.toDomain(remotePrefsDto)
            val entity = localMapper.fromDomain(domainModel)
            localDao.upsertPreferences(entity)
        }
    }
}