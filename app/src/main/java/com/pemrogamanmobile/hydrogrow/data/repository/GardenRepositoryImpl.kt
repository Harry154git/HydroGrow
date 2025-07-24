package com.pemrogamanmobile.hydrogrow.data.repository

import android.net.Uri
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.GardenDao
import com.pemrogamanmobile.hydrogrow.data.local.mapper.toDomain
import com.pemrogamanmobile.hydrogrow.data.local.mapper.toEntity
import com.pemrogamanmobile.hydrogrow.data.remote.mapper.toDomain
import com.pemrogamanmobile.hydrogrow.data.remote.mapper.toDto
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.AuthService
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.ImageUploader
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices.GardenService
import com.pemrogamanmobile.hydrogrow.domain.model.Garden
import com.pemrogamanmobile.hydrogrow.domain.repository.GardenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlinx.coroutines.flow.emitAll

class GardenRepositoryImpl @Inject constructor(
    private val dao: GardenDao,
    private val firestore: GardenService,
    private val authService: AuthService,
    private val imageUploader: ImageUploader
) : GardenRepository {

    private fun getUserId(): String {
        return authService.getCurrentUser()?.uid.orEmpty()
    }

    override suspend fun insertGarden(garden: Garden) {
        // Langsung kirim ke Firestore
        firestore.uploadGarden(getUserId(), garden.toDto())
        // Kemudian simpan ke database lokal
        dao.insertGarden(garden.toEntity())
    }

    override suspend fun uploadGardenImage(uri: Uri): String {
        return imageUploader.uploadImageToStorage(uri, "gardens", getUserId())
    }

    override suspend fun updateGarden(garden: Garden) {
        // Langsung update ke Firestore
        firestore.uploadGarden(getUserId(), garden.toDto())
        // Kemudian update di database lokal
        dao.updateGarden(garden.toEntity())
    }

    override suspend fun deleteGarden(garden: Garden) {
        // Langsung hapus dari Firestore
        firestore.deleteGarden(getUserId(), garden.id)
        // Kemudian hapus dari database lokal
        dao.deleteGarden(garden.toEntity())
    }

    override fun getAllGardens(): Flow<List<Garden>> {
        val userId = getUserId()
        return createSyncedGardenFlow(userId)
    }

    /**
     * Helper function untuk membuat Flow yang sinkron antara Firestore dan Room.
     * Mengambil data dari Firestore, menyimpannya ke Room, lalu mengembalikan Flow dari Room.
     */
    private fun createSyncedGardenFlow(userId: String): Flow<List<Garden>> = flow {
        if (userId.isNotEmpty()) {
            try {
                // 1. Ambil data terbaru dari Firestore
                val remoteData = firestore.getGardens(userId).map { it.toDomain() }

                // 2. Hapus data lama di lokal untuk user ini
                dao.deleteAllGardensByUserId(userId)

                // 3. Masukkan data baru dari Firestore ke lokal
                dao.insertGardens(remoteData.map { it.toEntity() })
            } catch (e: Exception) {
                // Jika gagal (misal: tidak ada internet), flow akan lanjut
                // dan mengandalkan data yang sudah ada di cache lokal.
                // Anda bisa menambahkan logging di sini.
            }
        }

        // 4. Emit semua data dari Room dan terus memantaunya untuk perubahan.
        // Ini adalah "Single Source of Truth" untuk UI.
        emitAll(dao.getGardensByUserId(userId).map { entities ->
            entities.map { it.toDomain() }
        })
    }


    override suspend fun getGardenById(gardenId: String): Garden? {
        val userId = getUserId()
        return try {
            // 1. Selalu coba ambil dari Firestore terlebih dahulu
            val remoteGardenDto = firestore.getGardenById(userId, gardenId)
            if (remoteGardenDto != null) {
                val garden = remoteGardenDto.toDomain()
                // 2. Simpan/update data terbaru ke lokal
                dao.insertGarden(garden.toEntity())
                garden
            } else {
                // Jika tidak ada di Firestore, hapus dari lokal untuk konsistensi
                dao.deleteGardenById(gardenId)
                null
            }
        } catch (e: Exception) {
            // 3. Jika network error, fallback ke data lokal
            dao.getGardenById(gardenId)?.toDomain()
        }
    }
}